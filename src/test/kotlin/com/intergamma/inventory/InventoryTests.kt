package com.intergamma.inventory

import com.intergamma.inventory.api.InventoryController
import com.intergamma.inventory.api.model.ReservationRequest
import com.intergamma.inventory.api.model.StockCreateRequest
import com.intergamma.inventory.api.model.StockUpdateRequest
import com.intergamma.inventory.exception.InventoryException
import com.intergamma.inventory.scheduled.ReservationExpiryJob
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.util.ReflectionTestUtils
import org.springframework.transaction.annotation.Transactional

@SpringBootTest
@Transactional
class InventoryTests {

	@Autowired
	lateinit var controller: InventoryController

	@Autowired
	lateinit var reservationExpiryJob: ReservationExpiryJob

	private val defaultStore = "GAMMA"
	private val defaultProduct = 123456789L
	private val defaultTotalStock = 10
	private val secondaryTotalStock = 20

	@Test
	fun `it should create stock`() {
		val createRequest = stockCreateRequest()
		val result = controller.createStock(createRequest)

		assertNotNull(result.id)
		assertEquals(createRequest.storeName, result.storeName)
		assertEquals(createRequest.productCode, result.productCode)
		assertEquals(createRequest.totalStock, result.totalStock)
		assertEquals(createRequest.totalStock, result.availableStock)
		assertEquals((0), result.reservedStock)
	}

	@Test
	fun `it should throw when create duplicate stock`() {
		val createRequest = stockCreateRequest()
		controller.createStock(createRequest)
		assertThrows(InventoryException::class.java) { controller.createStock(createRequest) }
	}

	@Test
	fun `it should update stock`() {
		val created = controller.createStock(stockCreateRequest())
		val updateRequest = stockUpdateRequest()
		val result = controller.updateStock(created.id, updateRequest)

		assertEquals(created.id, result.id)
		assertNotEquals(created.totalStock, result.totalStock)
		assertEquals(updateRequest.totalStock, result.totalStock)
		assertEquals(updateRequest.totalStock, result.availableStock)
		assertEquals((0), result.reservedStock)
	}

	@Test
	fun `it should find stock`() {
		val created = controller.createStock(stockCreateRequest())
		val result = controller.findStockById(created.id)

		assertNotNull(result)
		assertEquals(created.id, result.id)
		assertEquals(created.storeName, result.storeName)
		assertEquals(created.productCode, result.productCode)
		assertEquals(created.totalStock, result.totalStock)
		assertEquals(created.availableStock, result.availableStock)
		assertEquals(created.reservedStock, result.reservedStock)
	}

	@Test
	fun `it should find all stock`() {
		val created = controller.createStock(stockCreateRequest())
		val result = controller.findAllStock()

		assertNotNull(result)
		assertEquals((1), result.size)
		assertEquals(created.id, result[0].id)
		assertEquals(created.storeName, result[0].storeName)
		assertEquals(created.productCode, result[0].productCode)
		assertEquals(created.totalStock, result[0].totalStock)
		assertEquals(created.availableStock, result[0].availableStock)
		assertEquals(created.reservedStock, result[0].reservedStock)
	}

	@Test
	fun `it should delete stock`() {
		val created = controller.createStock(stockCreateRequest())
		controller.deleteInventory(created.id)
		val result = controller.findAllStock().find { it.id == created.id }
		assertNull(result)
	}

	@Test
	fun `it should reserve stock`() {
		val createdStock = controller.createStock(stockCreateRequest())
		val reservationRequest = reservationRequest()
		val reservation = controller.createReservation(reservationRequest)
		val result = controller.findStockById(createdStock.id)

		assertEquals(reservationRequest.storeName, reservation.storeName)
		assertEquals(reservationRequest.productCode, reservation.productCode)
		assertEquals(reservationRequest.amount, reservation.amount)

		assertEquals(reservation.amount, result.reservedStock)
		assertEquals((createdStock.totalStock - reservation.amount), result.availableStock)

		assertEquals(reservation.id, result.reservations[0].id)
		assertEquals(reservation.storeName, result.reservations[0].storeName)
		assertEquals(reservation.productCode, result.reservations[0].productCode)
		assertEquals(reservation.amount, result.reservations[0].amount)
		assertEquals(reservation.createdDate, result.reservations[0].createdDate)
	}

	@Test
	fun `it should throw when reserve more than available stock`() {
		controller.createStock(stockCreateRequest())
		assertThrows(InventoryException::class.java) { controller.createReservation(reservationRequest(Int.MAX_VALUE)) }
	}

	@Test
	fun `it should throw when update total stock with less than reserved stock`() {
		val createdStock = controller.createStock(stockCreateRequest())
		controller.createReservation(reservationRequest())

		assertThrows(InventoryException::class.java) { controller.updateStock(createdStock.id, stockUpdateRequest((0))) }
	}

	@Test
	fun `it should expire reservation`() {
		val created = controller.createStock(stockCreateRequest())
		val reservation = controller.createReservation(reservationRequest())

		reservationExpiryJob.execute()
		val stockWithReservation = controller.findStockById(created.id)
		assertEquals((1), stockWithReservation.reservations.size)
		assertEquals(reservation.id, stockWithReservation.reservations[0].id)

		val expireInSeconds = ReflectionTestUtils.getField(reservationExpiryJob, "expireInSeconds")
		ReflectionTestUtils.setField(reservationExpiryJob, "expireInSeconds", 0)

		reservationExpiryJob.execute()

		ReflectionTestUtils.setField(reservationExpiryJob, "expireInSeconds", expireInSeconds)

		val stock = controller.findStockById(created.id)
		assertEquals((0), stock.reservations.size)
	}

	private fun stockCreateRequest(): StockCreateRequest {
		return StockCreateRequest(storeName = defaultStore, productCode = defaultProduct, totalStock = defaultTotalStock)
	}

	private fun stockUpdateRequest(amount: Int = secondaryTotalStock): StockUpdateRequest {
		return StockUpdateRequest(totalStock = amount)
	}

	private fun reservationRequest(amount: Int = defaultTotalStock): ReservationRequest {
		return ReservationRequest(storeName = defaultStore, productCode = defaultProduct, amount = amount)
	}

}
