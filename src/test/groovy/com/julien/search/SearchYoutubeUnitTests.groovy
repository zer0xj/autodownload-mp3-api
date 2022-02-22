package com.julien.search

import com.julien.search.model.YoutubeVideo
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.web.server.LocalServerPort
import org.springframework.core.ParameterizedTypeReference
import org.springframework.http.HttpEntity
import org.springframework.http.HttpMethod
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.springframework.web.client.RestTemplate

import java.nio.charset.StandardCharsets

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT,
        properties = [ "database.login.enableUserValidation=false", "downloads.location=./build/tmp", "history.location=./build/tmp" ])
class SearchYoutubeUnitTests {

    @Autowired
    private RestTemplate restTemplate = new RestTemplate()

    @LocalServerPort
    private String localport

    private final ParameterizedTypeReference<Map<String, Object>> genericResponseType =
            new ParameterizedTypeReference<HashMap<String, Object>>() {}

    String searchQuery = "Home at Last"
    int testUserId = 1234

    static String jobId = null

    @Test
    @DisplayName("searchController search endpoint")
    void searchControllerTest1() {

        String url = "http://localhost:$localport/v1/user/$testUserId/search?query=" +
                URLEncoder.encode(searchQuery, StandardCharsets.UTF_8.name())

        List<YoutubeVideo> videos = Collections.emptyList()

        try {
            videos = Arrays.asList(this.restTemplate.exchange(url, HttpMethod.GET, HttpEntity.EMPTY,
                    YoutubeVideo[].class).getBody())
        } catch (Exception ignored) {}
        Assertions.assertNotEquals(0, videos.size())
    }

    @Test
    @DisplayName("searchController search and download endpoint")
    void searchControllerTest2() {

        String url = "http://localhost:$localport/v1/user/$testUserId/search/download?query=" +
                URLEncoder.encode(searchQuery, StandardCharsets.UTF_8.name())

        jobId = null

        try {
            // Doing this because Jackson/Kotlin is annoying with deserializing non-nullable fields in data classes
            jobId = this.restTemplate.exchange(url, HttpMethod.GET, HttpEntity.EMPTY, genericResponseType).getBody().get("jobId").toString()
        } catch (Exception ignored) {}
        Assertions.assertNotEquals(null, jobId)

    }

    @Test
    @DisplayName("searchController all jobs' statuses endpoint")
    void searchControllerTest3() {

        String url = "http://localhost:$localport/v1/user/$testUserId/search/download/status"

        Integer jobCount = 0

        try {
            jobCount = this.restTemplate.exchange(url, HttpMethod.GET, HttpEntity.EMPTY, genericResponseType).getBody().size()
        } catch (Exception ignored) {}
        Assertions.assertNotEquals(null, jobCount)
        Assertions.assertTrue(jobCount > 0)

    }

    @Test
    @DisplayName("searchController single job's status endpoint")
    void searchControllerTest4() {

        String url = "http://localhost:$localport/v1/user/$testUserId/search/download/status/$jobId"

        String status = null

        for (int i = 0; i < 5; i++) {
            status = getJobStatus(url)
            if ((status == null) || (status != "processing")) break
        }

        Assertions.assertNotEquals(null, status)

    }

    @Test
    @DisplayName("searchController job cancellation endpoint")
    void searchControllerTest5() {

        String url = "http://localhost:$localport/v1/user/$testUserId/search/download/$jobId"

        String status = null

        try {
            status = this.restTemplate.exchange(url, HttpMethod.DELETE, HttpEntity.EMPTY, genericResponseType).getBody().get("status")
        } catch (Exception ignored) {}
        Assertions.assertNotEquals(null, status)
    }

    private String getJobStatus(String url) {

        Thread.sleep(200)

        String status = null

        try {
            status = this.restTemplate.exchange(url, HttpMethod.GET, HttpEntity.EMPTY, genericResponseType).getBody().get("status")
        } catch (Exception ignored) {}

        return status
    }

    @Test
    @DisplayName("searchController job summary endpoint")
    void searchControllerTest6() {

        String url = "http://localhost:$localport/v1/user/$testUserId/search/download/summary"

        Map<String, Object> summary = Collections.emptyMap()

        try {
            // Doing this because Jackson/Kotlin is annoying with deserializing non-nullable fields in data classes
            summary = this.restTemplate.exchange(url, HttpMethod.GET, HttpEntity.EMPTY, genericResponseType).getBody()
        } catch (Exception ignored) {}
        Assertions.assertFalse(summary.isEmpty())
    }
}
