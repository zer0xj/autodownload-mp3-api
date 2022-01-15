package com.julien.search.dao

import com.julien.search.model.ErrorCode
import com.julien.search.repository.UserRepository
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.dao.DataAccessException
import org.springframework.stereotype.Service

@Service
class DefaultUserDAO : UserDAO {

    @Autowired
    private lateinit var userRepository: UserRepository

    private val logger: Logger = LoggerFactory.getLogger(this.javaClass.name)

    @Throws(DAOException::class)
    override fun validateUserName(userId: Int) {
        try {
            val userLookup = userRepository.selectUserByUserName(userId)
            if (userLookup == null) {
                throw DAOException("Could not find a valid user for userId[$userId]", "validateUserName($userId)",
                    ErrorCode.INVALID_USERNAME)
            } else if (logger.isDebugEnabled) {
                logger.debug("Successfully validated userId[$userId]: $userLookup")
            } else {
                logger.info("Successfully validated userName[${userLookup.userName}] by userId[$userId]")
            }
        } catch (d: DataAccessException) {
            throw DAOException("Caught DataAccessException trying to validate if user exists for userId[$userId]", d,
                "validateUserName($userId)", ErrorCode.DATABASE_ERROR)
        } catch (e: Exception) {
            throw DAOException("Caught ${e.javaClass.simpleName} trying to validate if user exists for userName[$userId]",
                "validateUserName($userId)", ErrorCode.DATABASE_ERROR)
        }
    }
}
