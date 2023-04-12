package com.intergamma.inventory.domain

import jakarta.persistence.*

@Entity
@Table(uniqueConstraints = [
    UniqueConstraint(name = "UniqueStoreAndProduct", columnNames = ["storeName", "productCode"])
])
class Stock(
        @Id
        @GeneratedValue
        val id: Long = 0,

        val storeName: String,

        val productCode: Long,

        var totalStock: Int = 0,

        @OneToMany(mappedBy = "stock", fetch = FetchType.EAGER, cascade = [CascadeType.ALL], orphanRemoval = true)
        val reservations: MutableList<Reservation> = mutableListOf()
) {

    fun availableStock() = totalStock - reservedStock()

    fun reservedStock() = reservations.sumOf { it.amount }

    fun addReservation(reservation: Reservation) {
        reservations.add(reservation)
        reservation.stock = this
    }

    fun removeReservation(reservationId: Long) {
        reservations.removeIf { it.id == reservationId }
    }
}