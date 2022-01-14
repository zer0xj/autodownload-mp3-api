package com.julien.search.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import springfox.documentation.builders.PathSelectors
import springfox.documentation.builders.RequestHandlerSelectors
import springfox.documentation.service.ApiInfo
import springfox.documentation.spi.DocumentationType
import springfox.documentation.spring.web.plugins.Docket
import springfox.documentation.swagger2.annotations.EnableSwagger2
import java.util.*


@Configuration
@EnableSwagger2
class SwaggerConfiguration {

    @Bean
    fun api(): Docket {
        val docket = Docket(DocumentationType.SWAGGER_2)

        val apiInfo = ApiInfo("Search YouTube API", "Search YouTube API",
                null, null, null, null, null, Collections.emptyList())

        docket.select()
                .apis(RequestHandlerSelectors.basePackage("com.julien.search"))
                .paths(PathSelectors.any())
                .build()
                .apiInfo(apiInfo)
                .useDefaultResponseMessages(true)

        return docket
    }
}
