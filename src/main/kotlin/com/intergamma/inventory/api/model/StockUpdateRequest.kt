package com.intergamma.inventory.api.model

import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.Min

class StockUpdateRequest(
        @field:Schema(example = "99")
        @field:Min(1)
        val totalStock: Int
)