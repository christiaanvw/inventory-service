package com.intergamma.inventory.api.model

import io.swagger.v3.oas.annotations.media.Schema
import java.time.LocalDateTime

class Reservation (

        @field:Schema(example = "123456789")
        val id: Long,
        @field:Schema(example = "GAMMA Utrecht")
        val storeName: String,
        @field:Schema(example = "123456789")
        val productCode: Long,
        @field:Schema(example = "99")
        val amount: Int,
        @field:Schema(example = "2023-01-01T23:59:59.000000")
        val createdDate: LocalDateTime

)
