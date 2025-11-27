package pt.isel.service

import jakarta.annotation.PreDestroy
import jakarta.inject.Named
import org.slf4j.LoggerFactory
import pt.isel.User
import pt.isel.agenda.Event
import pt.isel.agenda.Participant
import pt.isel.agenda.TimeSlot
import pt.isel.repo.TransactionManager
import java.time.Instant
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.TimeUnit
import java.util.concurrent.locks.ReentrantLock
import kotlin.concurrent.withLock

@Named
class TimeSlotPublisher(
    val trxManager: TransactionManager,
) {
    companion object {
        private val logger = LoggerFactory.getLogger(TimeSlotPublisher::class.java)
    }

    // Important: mutable state on a singleton service
    private val listeners = mutableMapOf<Event, List<UpdatedTimeSlotEmitter>>()
    private var currentId = 0L
    private val lock = ReentrantLock()

    // A scheduler to send the periodic keep-alive events
    private val scheduler: ScheduledExecutorService =
        Executors.newScheduledThreadPool(1).also {
            it.scheduleAtFixedRate({ keepAlive() }, 2, 2, TimeUnit.SECONDS)
        }

    fun sendMessageToAll(
        ev: Event,
        ts: TimeSlot,
        user: User,
        action: ActionKind,
        participant: Participant? = null,
    ) {
        listeners[ev]?.forEach {
            try {
                it.emit(
                    UpdatedTimeSlot.Message(
                        ++currentId,
                        TimeSlotData(ev.id, ts.id, action, user.id, user.name, user.email, participant?.id),
                    ),
                )
            } catch (ex: Exception) {
                logger.info("Exception while sending Message signal - {}", ex.message)
            }
        }
    }

    fun addEmitter(
        eventId: Int,
        listener: UpdatedTimeSlotEmitter,
    ) = lock.withLock {
        val ev =
            trxManager.run {
                repoEvents.findById(eventId)
            }
        requireNotNull(ev)

        logger.info("adding listener")
        val oldListeners = listeners.getOrDefault(ev, emptyList())
        listeners[ev] = oldListeners + listener
        listener.onCompletion {
            logger.info("onCompletion")
            removeEmitter(ev, listener)
        }
        listener.onError {
            logger.info("onError")
            removeEmitter(ev, listener)
        }
        listener
    }

    private fun removeEmitter(
        ev: Event,
        listener: UpdatedTimeSlotEmitter,
    ) = lock.withLock {
        logger.info("removing listener")
        val oldListeners = listeners[ev]
        requireNotNull(oldListeners)
        listeners.replace(ev, oldListeners - listener)
    }

    private fun keepAlive() =
        lock.withLock {
//            logger.info("keepAlive, sending to {} listeners", listeners.values.flatten().size)
            val signal = UpdatedTimeSlot.KeepAlive(Instant.now())
            listeners.values.flatten().forEach {
                try {
                    it.emit(signal)
                } catch (ex: Exception) {
                    logger.info("Exception while sending keepAlive signal - {}", ex.message)
                }
            }
        }

    // Shutdown the executor service when the application is stopped
    @PreDestroy
    fun shutdown() {
        logger.info("Shutting down TimeSlotPublisher scheduler")
        scheduler.shutdown()
    }
}
