package com.intergamma.inventory.api

import com.intergamma.inventory.api.model.*
import com.intergamma.inventory.service.InventoryService
import io.swagger.v3.oas.annotations.Operation
import jakarta.validation.Valid
import org.springframework.web.bind.annotation.*

@RestController
class InventoryController (
    private val service: InventoryService
) {

    @Operation(summary = "Create stock")
    @PostMapping("/stock")
    fun createStock(@Valid @RequestBody request: StockCreateRequest): Stock {
        return service.createStock(request)
    }

    @Operation(summary = "Update stock")
    @PatchMapping("/stock/{id}")
    fun updateStock(@PathVariable("id") stockId: Long, @Valid @RequestBody request: StockUpdateRequest): Stock {
        return service.updateStock(stockId, request)
    }

    @Operation(summary = "Create a reservation")
    @PostMapping("/reservation")
    fun createReservation(@Valid @RequestBody request: ReservationRequest): Reservation {
        return service.createReservation(request)
    }

    @Operation(summary = "Find stock by id")
    @GetMapping("/stock/{id}")
    fun findStockById(@PathVariable("id") stockId: Long): Stock {
        return service.findStockById(stockId)
    }

    @Operation(summary = "Find all stock")
    @GetMapping("/stock")
    fun findAllStock(): List<Stock> {
        return service.findAllStock()
    }

    @Operation(summary = "Delete stock by id")
    @DeleteMapping("/stock/{id}")
    fun deleteInventory(@PathVariable("id") stockId: Long) {
        service.deleteStock(stockId)
    }

}