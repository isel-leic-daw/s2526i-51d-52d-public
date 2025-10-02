package pt.isel

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Primary
import org.springframework.stereotype.Component
import pt.isel.repo.TransactionManager
import pt.isel.repo.mem.TransactionManagerInMem

@Component
class TestConfig {
    @Bean
    @Primary
    fun trxManagerInMem(): TransactionManager = TransactionManagerInMem()
}
