package com.intergamma.inventory.domain

import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.Id
import jakarta.persistence.ManyToOne
import java.time.LocalDateTime

@Entity
class Reservation(
        @Id
        @GeneratedValue
        val id: Long = 0,

        val amount: Int,

        val createdDate: LocalDateTime = LocalDateTime.now(),

        @ManyToOne
        var stock: Stock
)
