package com.intergamma.inventory.service

import com.intergamma.inventory.api.model.ReservationRequest
import com.intergamma.inventory.api.model.StockCreateRequest
import com.intergamma.inventory.api.model.StockUpdateRequest
import com.intergamma.inventory.domain.Reservation
import com.intergamma.inventory.domain.Stock
import com.intergamma.inventory.logic.InventoryLogic
import org.springframework.stereotype.Service
import com.intergamma.inventory.api.model.Reservation as ExposedReservation
import com.intergamma.inventory.api.model.Stock as ExposedStock

@Service
class InventoryService(
        private val logic: InventoryLogic
) {

    fun createStock(request: StockCreateRequest): ExposedStock {
        val stock = logic.createStock(request)
        return toApi(stock)
    }

    fun updateStock(id: Long, request: StockUpdateRequest): ExposedStock {
        val stock = logic.updateStock(id, request)
        return toApi(stock)
    }

    fun createReservation(request: ReservationRequest): ExposedReservation {
        val reservation = logic.reserveStock(request)
        return toApi(reservation)
    }

    fun findStockById(stockId: Long): ExposedStock {
        val stock = logic.findStockById(stockId)
        return toApi(stock)
    }

    fun findAllStock(): List<ExposedStock> {
        return logic.findAllStock().map { toApi(it) }
    }

    fun deleteStock(stockId: Long) {
        logic.deleteStockById(stockId)
    }

    private fun toApi(stock: Stock): ExposedStock {
        return ExposedStock(
                id = stock.id,
                storeName = stock.storeName,
                productCode = stock.productCode,
                totalStock = stock.totalStock,
                reservedStock = stock.reservedStock(),
                availableStock = stock.availableStock(),
                reservations = stock.reservations.map { toApi(it) }
        )
    }

    private fun toApi(reservation: Reservation): ExposedReservation {
        return ExposedReservation(
                id = reservation.id,
                storeName = reservation.stock.storeName,
                productCode = reservation.stock.productCode,
                amount = reservation.amount,
                createdDate = reservation.createdDate
        )
    }

}