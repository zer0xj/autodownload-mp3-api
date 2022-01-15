package com.julien.search.repository

import com.julien.search.model.User
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate
import org.springframework.stereotype.Repository

@Repository
class JdbcUserRepository : UserRepository {

    @Autowired
    @Qualifier("mySqlNamedJdbcTemplate")
    private lateinit var mySql: NamedParameterJdbcTemplate

    private val logger: Logger = LoggerFactory.getLogger(this.javaClass.name)

    override fun selectUserByUserName(userId: Int): User? {

        val parameters = MapSqlParameterSource()
        parameters.addValue("userId", userId)
        val resultSet: List<MutableMap<String, Any>> = mySql.queryForList(SELECT_SQL, parameters)

        if (resultSet.isEmpty()) {
            return null
        } else if (resultSet.size > 1) {
            logger.error("Found ${resultSet.size} rows for userId[$userId]. Returning first one")
            logger.debug("Rows found: $resultSet")
        }

        return try {
            User.ModelMapper.from(resultSet[0])
        } catch (e: Exception) {
            logger.error("Caught ${e.javaClass.simpleName} trying to create User object from ${resultSet[0]}")
            return null
        }
    }

    private val SELECT_SQL: String = "select user_name, user_email, admin_user from users where (user_id = :userId);"
}
