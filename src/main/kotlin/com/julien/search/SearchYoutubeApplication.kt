package com.julien.search

import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication

@SpringBootApplication
class SearchYoutubeApplication

fun main(args: Array<String>) {
	SpringApplication(SearchYoutubeApplication::class.java).run(*args)
}
