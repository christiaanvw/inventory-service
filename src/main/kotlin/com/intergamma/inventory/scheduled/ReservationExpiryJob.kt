package com.intergamma.inventory.scheduled

import com.intergamma.inventory.logic.InventoryLogic
import com.intergamma.inventory.repository.ReservationRepository
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import java.time.LocalDateTime

val LOG = LoggerFactory.getLogger(ReservationExpiryJob::class.java)

@Component
class ReservationExpiryJob (
        @Value("\${stock.reservation.expireInSeconds}")
        private val expireInSeconds: Long,

        @Value("\${stock.reservation.intervalInMillis}")
        private val intervalInMillis: Long,

        private val inventoryLogic: InventoryLogic,

        private val reservationRepository: ReservationRepository
) {

    @Scheduled(fixedRateString = "\${stock.reservation.intervalInMillis}")
    fun execute() {
        val dateToExpire = calculateDateToExpire()
        val reservationIds = reservationRepository.findReservationIdsOlderThan(dateToExpire)

        for (id in reservationIds) {
            try {
                inventoryLogic.deleteReservationById(id)
            } catch (e: Exception) {
                LOG.error("Error trying to delete reservation with id: $id", e)
            }
        }
    }

    private fun calculateDateToExpire(): LocalDateTime {
        val marginInSeconds = intervalInMillis / 1000L
        val timeToExpireInSeconds = expireInSeconds - marginInSeconds
        return LocalDateTime.now().minusSeconds(timeToExpireInSeconds)
    }
}