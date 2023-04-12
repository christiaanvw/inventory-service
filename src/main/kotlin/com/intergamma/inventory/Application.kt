package com.intergamma.inventory

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.scheduling.annotation.EnableScheduling

@SpringBootApplication
@EnableScheduling
class InventoryApplication

fun main(args: Array<String>) {
	runApplication<InventoryApplication>(*args)
}
