package com.julien.search.controller

import com.fasterxml.jackson.databind.ObjectMapper
import com.google.common.cache.Cache
import com.julien.search.controller.v1.SearchController
import com.julien.search.dao.*
import com.julien.search.model.*
import com.julien.search.repository.JdbcUserRepository
import com.julien.search.service.YoutubeSearchService
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.BDDMockito
import org.mockito.Mockito.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.boot.test.mock.mockito.SpyBean
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.springframework.test.web.servlet.MockMvc
import java.util.UUID


@ExtendWith(SpringExtension::class)
@WebMvcTest(SearchController::class)
@ContextConfiguration(classes = [
    DefaultUserDAO::class,
    LocalHistoryDAO::class,
    JdbcUserRepository::class,
    YoutubeSearchDAO::class,
    YoutubeSearchService::class])
class SearchYoutubeIntegrationTests {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @SpyBean
    private lateinit var searchService: YoutubeSearchService

    @MockBean
    private lateinit var processedVideos: Cache<String, ProcessingJob>

    @SpyBean
    private lateinit var historyDAO: LocalHistoryDAO

    @SpyBean
    private lateinit var searchDAO: YoutubeSearchDAO

    @MockBean
    @Qualifier("mySqlNamedJdbcTemplate")
    private lateinit var mySql: NamedParameterJdbcTemplate

    @SpyBean
    private lateinit var userRepository: JdbcUserRepository

    @SpyBean
    private lateinit var userDAO: DefaultUserDAO

    @SpyBean
    private lateinit var videoDownloadDAO: YoutubeVideoDownloadDAO

    val mapper: ObjectMapper = ObjectMapper()

    val searchQuery = "Home at Last"
    val testUserId = 1234

    // Helper functions
    private fun getMp3DownloadResponse(previouslyDownloaded: Boolean? = null, success: Boolean? = null, filename: String? = "$searchQuery.mp3") =
        Mp3DownloadResponse(
            filename = filename,
            previouslyDownloaded = previouslyDownloaded ?: false,
            query = searchQuery,
            success = success ?: true)

    private fun getProcessingJob(previouslyDownloaded: Boolean? = null, success: Boolean? = null) = ProcessingJob(
        jobId = UUID.randomUUID().toString(),
        response = getMp3DownloadResponse(previouslyDownloaded, success),
        userId = testUserId
    )

    @BeforeEach
    fun setup() {
        BDDMockito.given(mySql.queryForList(anyString(), any(MapSqlParameterSource::class.java))).willReturn(listOf(mapOf("user_name" to "test")))
    }

    @Test
    fun `userRepository when user is found`() {
        val result = userRepository.selectUserByUserName(testUserId)
        Assertions.assertNotNull(result)
        Assertions.assertNotNull(result.userName)
    }

    @Test
    fun `userRepository when user is not found`() {
        BDDMockito.given(mySql.queryForList(anyString(), any(MapSqlParameterSource::class.java))).willReturn(emptyList())
        Assertions.assertEquals(userRepository.selectUserByUserName(testUserId), User())
    }

    @Test
    fun `searchService search functionality`() {
        BDDMockito.given(searchDAO.search(anyString())).willReturn(listOf(YoutubeVideo(title = searchQuery)))
        Assertions.assertEquals(searchService.search(testUserId, searchQuery).isEmpty(), false)
    }

    @Test
    fun `searchService download functionality for videos already downloaded`() {
        BDDMockito.given(processedVideos.getIfPresent(searchQuery)).willReturn(getProcessingJob(true))
        Assertions.assertNotNull(searchService.searchAndDownload(testUserId, searchQuery).jobId)
    }
}
