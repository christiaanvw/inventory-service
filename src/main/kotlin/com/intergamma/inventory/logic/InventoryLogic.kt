package com.intergamma.inventory.logic

import com.intergamma.inventory.api.model.ReservationRequest
import com.intergamma.inventory.api.model.StockCreateRequest
import com.intergamma.inventory.api.model.StockUpdateRequest
import com.intergamma.inventory.domain.Reservation
import com.intergamma.inventory.domain.Stock
import com.intergamma.inventory.exception.InventoryException
import com.intergamma.inventory.repository.ReservationRepository
import com.intergamma.inventory.repository.StockRepository
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Component

@Component
class InventoryLogic(
        private val stockRepository: StockRepository,
        private val reservationRepository: ReservationRepository
) {

    fun createStock(request: StockCreateRequest): Stock {
        val existing = stockRepository.findByStoreNameAndProductCode(request.storeName, request.productCode)

        if (existing != null) {
            throw InventoryException("Stock[storeName=${existing.storeName}, productCode=${existing.productCode}] already exists")
        }

        val stock = Stock(storeName = request.storeName, productCode = request.productCode, totalStock = request.totalStock)
        return stockRepository.save(stock)
    }

    fun updateStock(stockId: Long, request: StockUpdateRequest): Stock {
        val stock = stockRepository.findByIdOrNull(stockId) ?: throw InventoryException("Could not find Stock with id: $stockId]")

        if (request.totalStock < stock.reservedStock()) {
            throw InventoryException("Stock[id=${stock.id}] new totalStock(${request.totalStock}) cannot be smaller than reservedStock(${stock.reservedStock()})")
        }

        stock.totalStock = request.totalStock
        return stockRepository.save(stock)
    }

    fun reserveStock(request: ReservationRequest): Reservation {
        val stock = stockRepository.findByStoreNameAndProductCode(request.storeName, request.productCode)
                ?: throw InventoryException("Could not find Stock with: storeName=${request.storeName}, productCode=${request.productCode}")

        if (request.amount > stock.availableStock()) {
            throw InventoryException("Stock[id=${stock.id}] requested reserveAmount(${request.amount}) cannot be greater than availableAmount(${stock.availableStock()})")
        }

        val reservation = Reservation(amount = request.amount, stock = stock)
        stock.addReservation(reservation)
        return stockRepository.save(stock).reservations.last()
    }

    fun findStockById(stockId: Long): Stock {
        return stockRepository.findByIdOrNull(stockId) ?: throw InventoryException("Could not find Stock with id: $stockId]")
    }

    fun findReservationById(reservationId: Long): Reservation {
        return reservationRepository.findByIdOrNull(reservationId) ?: throw InventoryException("Could not find Reservation with id: $reservationId]")
    }

    fun findAllStock(): List<Stock> {
        return stockRepository.findAll()
    }

    fun deleteReservationById(reservationId: Long) {
        val stock = findReservationById(reservationId).stock
        stock.removeReservation(reservationId)
        stockRepository.save(stock)
    }

    fun deleteStockById(stockId: Long) {
        stockRepository.deleteById(stockId)
    }
}