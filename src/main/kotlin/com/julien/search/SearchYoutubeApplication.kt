package com.julien.search

import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.scheduling.annotation.EnableAsync

@SpringBootApplication
@EnableAsync
class SearchYoutubeApplication

fun main(args: Array<String>) {
	SpringApplication(SearchYoutubeApplication::class.java).run(*args)
}
