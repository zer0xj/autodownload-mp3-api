package com.julien.search.config

import org.apache.tomcat.jdbc.pool.DataSource
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate


@Configuration
class DatabaseConfiguration {

    @Bean("mySqlNamedJdbcTemplate")
    fun mySqlNamedJdbcTemplate(@Qualifier("mySqlDS") datasource: DataSource): NamedParameterJdbcTemplate {
        return NamedParameterJdbcTemplate(datasource)
    }

    @Bean("mySqlJdbcTemplate")
    fun mySqlJdbcTemplate(@Qualifier("mySqlDS") datasource: DataSource): JdbcTemplate {
        return JdbcTemplate(datasource)
    }

    @Bean("mySqlDS")
    fun mySqlDS(
            @Value("\${database.driver:com.mysql.cj.jdbc.Driver}") driverClassName: String,
            @Value("\${database.url}") url: String,
            @Value("\${database.user}") username: String,
            @Value("\${database.password}") password: String,
            @Value("\${database.minIdle:20}") minIdle: Int,
            @Value("\${database.maxIdle:45}") maxIdle: Int,
            @Value("\${database.maxTotal:45}") maxTotal: Int,
            @Value("\${database.initialSize:20}") initialSize: Int,
            @Value("\${database.maxWaitMillis:8000}") maxWaitMillis: Int,
            @Value("\${database.validationQuery:select 1 from systables limit 1}") validationQuery: String): DataSource {

        val dataSource: DataSource = DataSource()
        dataSource.setDriverClassName(driverClassName)
        dataSource.setUsername(username)
        dataSource.setPassword(password)
        dataSource.setUrl(url)
        dataSource.setMinIdle(minIdle)
        dataSource.setMaxActive(maxTotal)
        dataSource.setMaxIdle(maxIdle)
        dataSource.setInitialSize(initialSize)
        dataSource.setMaxWait(maxWaitMillis)
        dataSource.setTestOnBorrow(true)
        dataSource.setTestOnReturn(true)
        dataSource.setTestWhileIdle(true)
        dataSource.setValidationQuery(validationQuery)

        return dataSource
    }
}
