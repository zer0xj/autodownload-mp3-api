package com.julien.search.repository

import com.google.common.cache.CacheBuilder
import com.google.common.cache.CacheLoader
import com.google.common.cache.LoadingCache
import com.julien.search.model.User
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Primary
import org.springframework.stereotype.Repository
import java.util.concurrent.TimeUnit
import kotlin.jvm.Throws

@Primary
@Repository
class CachedJdbcUserRepository : UserRepository {

    @Autowired
    private lateinit var jdbcUserRepository: JdbcUserRepository

    @Value("\${cache.userData.enabled:true}")
    private var userDataCacheEnabled: Boolean = true

    @Value("\${cache.userData.minutesAfterWriteTTL:10}")
    private var userDataCacheMinutesAfterWriteTTL: Long = 10

    private val userDataCache: LoadingCache<Int, User> = CacheBuilder.newBuilder()
        .maximumSize(2048)
        .expireAfterWrite(userDataCacheMinutesAfterWriteTTL, TimeUnit.MINUTES)
        .build(object : CacheLoader<Int, User>() {
            @Throws(Exception::class)
            override fun load(userId: Int): User = jdbcUserRepository.selectUserByUserName(userId)
        })

    private val logger: Logger = LoggerFactory.getLogger(this.javaClass.name)

    override fun selectUserByUserName(userId: Int): User =
        if (userDataCacheEnabled) {
            try {
                userDataCache.get(userId)
            } catch (e: Exception) {
                logger.debug("Could not find userId[$userId] record in cache or in the database - returning empty User object.")
                User()
            }
        } else {
            jdbcUserRepository.selectUserByUserName(userId)
        }
}
