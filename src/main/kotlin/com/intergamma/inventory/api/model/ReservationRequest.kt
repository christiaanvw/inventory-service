package com.intergamma.inventory.api.model

import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size

class ReservationRequest(
        @field:Schema(example = "GAMMA Utrecht")
        @field:NotBlank
        @field:Size(min = 3, max = 255)
        val storeName: String,

        @field:Schema(example = "123456789")
        @field:Min(1)
        val productCode: Long,

        @field:Schema(example = "10")
        @field:Min(1)
        val amount: Int
)