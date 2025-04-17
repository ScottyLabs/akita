package org.scottylabs.akita

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class AkitaApplication

fun main(args: Array<String>) {
	runApplication<AkitaApplication>(*args)
}
