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
            @Value("\${database.login.driver:com.mysql.cj.jdbc.Driver}") driverClassName: String,
            @Value("\${database.login.url}") url: String,
            @Value("\${database.login.user}") username: String,
            @Value("\${database.login.password}") password: String,
            @Value("\${database.login.minIdle:20}") minIdle: Int,
            @Value("\${database.login.maxIdle:45}") maxIdle: Int,
            @Value("\${database.login.maxTotal:45}") maxTotal: Int,
            @Value("\${database.login.initialSize:20}") initialSize: Int,
            @Value("\${database.login.maxWaitMillis:8000}") maxWaitMillis: Int,
            @Value("\${database.login.enableUserValidation:true}") enableUserValidation: Boolean,
            @Value("\${database.login.ignoreExceptionOnPreLoad:}") ignoreExceptionOnPreLoad: Boolean?,
            @Value("\${database.login.testOnBorrow:true}") testOnBorrow: Boolean,
            @Value("\${database.login.testOnReturn:true}") testOnReturn: Boolean,
            @Value("\${database.login.testWhileIdle:true}") testWhileIdle: Boolean,
            @Value("\${database.login.timeBetweenEvictionRunsMillis:5000}") timeBetweenEvictionRunsMillis: Int,
            @Value("\${database.login.validationQuery:select 1 from users limit 1}") validationQuery: String): DataSource {

        val dataSource = DataSource()
        dataSource.driverClassName = driverClassName
        dataSource.username = username
        dataSource.password = password
        dataSource.url = url
        dataSource.minIdle = minIdle
        dataSource.maxActive = maxTotal
        dataSource.maxIdle = maxIdle
        dataSource.initialSize = initialSize
        dataSource.maxWait = maxWaitMillis
        dataSource.isIgnoreExceptionOnPreLoad = ignoreExceptionOnPreLoad ?: !enableUserValidation
        dataSource.isTestOnBorrow = testOnBorrow
        dataSource.isTestOnReturn = testOnReturn
        dataSource.isTestWhileIdle = testWhileIdle
        dataSource.timeBetweenEvictionRunsMillis = timeBetweenEvictionRunsMillis
        dataSource.validationQuery = validationQuery

        return dataSource
    }
}
