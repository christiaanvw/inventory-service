package com.intergamma.inventory.repository

import com.intergamma.inventory.domain.Stock
import org.springframework.data.jpa.repository.JpaRepository

interface StockRepository: JpaRepository<Stock, Long> {

    fun findByStoreNameAndProductCode(storeName: String, productCode: Long): Stock?
}