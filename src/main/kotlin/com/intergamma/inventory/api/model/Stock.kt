package com.intergamma.inventory.api.model

import io.swagger.v3.oas.annotations.media.Schema

class Stock(
        @field:Schema(example = "1")
        val id: Long,

        @field:Schema(example = "GAMMA Utrecht")
        val storeName: String,

        @field:Schema(example = "123456789")
        val productCode: Long,

        @field:Schema(example = "99")
        val totalStock: Int,

        @field:Schema(example = "9")
        val reservedStock: Int,

        @field:Schema(example = "90")
        val availableStock: Int,

        val reservations: List<Reservation>
)