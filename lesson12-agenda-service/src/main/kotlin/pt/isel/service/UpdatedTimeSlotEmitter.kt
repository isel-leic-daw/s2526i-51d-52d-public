package pt.isel.service

interface UpdatedTimeSlotEmitter {
    fun emit(signal: UpdatedTimeSlot)

    fun onCompletion(callback: () -> Unit)

    fun onError(callback: (Throwable) -> Unit)
}
