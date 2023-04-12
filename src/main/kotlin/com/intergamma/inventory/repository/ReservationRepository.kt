package com.intergamma.inventory.repository

import com.intergamma.inventory.domain.Reservation
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import java.time.LocalDateTime

interface ReservationRepository: JpaRepository<Reservation, Long> {

    @Query(value = "select id from Reservation where createdDate <= :date")
    fun findReservationIdsOlderThan(date: LocalDateTime): List<Long>
}