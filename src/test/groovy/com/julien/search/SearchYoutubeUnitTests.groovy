package com.julien.search

import com.julien.search.model.YoutubeVideo
import org.junit.jupiter.api.Assertions
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

    @Test
    void searchControllerTest1() {

        // Test search endpoint

        String url = "http://localhost:$localport/v1/user/$testUserId/search?query=" + URLEncoder.encode(searchQuery, StandardCharsets.UTF_8)

        List<YoutubeVideo> videos = Collections.emptyList()

        try {
            videos = Arrays.asList(this.restTemplate.exchange(url, HttpMethod.GET, HttpEntity.EMPTY,
                    YoutubeVideo[].class).getBody())
        } catch (Exception ignored) {}
        Assertions.assertNotEquals(0, videos.size())
    }

    @Test
    void searchControllerTest2() {

        // Test download endpoint

        String url = "http://localhost:$localport/v1/user/$testUserId/search/download?query=" + URLEncoder.encode(searchQuery, StandardCharsets.UTF_8)

        String jobId = null

        try {
            // Doing this because Jackson/Kotlin is annoying with deserializing non-nullable fields in data classes
            jobId = this.restTemplate.exchange(url, HttpMethod.GET, HttpEntity.EMPTY, genericResponseType).getBody().get("jobId").toString()
        } catch (Exception ignored) {}
        Assertions.assertNotEquals(null, jobId)

        // Test all jobs' statuses endpoint

        url = "http://localhost:$localport/v1/user/$testUserId/search/download/status"

        Integer jobCount = 0

        try {
            jobCount = this.restTemplate.exchange(url, HttpMethod.GET, HttpEntity.EMPTY, genericResponseType).getBody().size()
        } catch (Exception ignored) {}
        Assertions.assertNotEquals(null, jobCount)
        Assertions.assertTrue(jobCount > 0)

        // Test a single job's status endpoint

        url = "$url/$jobId"

        String status = null

        for (int i = 0; i < 5; i++) {
            status = getJobStatus(url)
            if ((status == null) || (status != "processing")) break
        }

        Assertions.assertNotEquals(null, status)

        // Test cancellation endpoint

        url = "http://localhost:$localport/v1/user/$testUserId/search/download/$jobId"

        status = null

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
    void searchControllerTest3() {

        // Test job summary endpoint

        String url = "http://localhost:$localport/v1/user/$testUserId/search/download/summary"

        Map<String, Object> summary = Collections.emptyMap()

        try {
            // Doing this because Jackson/Kotlin is annoying with deserializing non-nullable fields in data classes
            summary = this.restTemplate.exchange(url, HttpMethod.GET, HttpEntity.EMPTY, genericResponseType).getBody()
        } catch (Exception ignored) {}
        Assertions.assertFalse(summary.isEmpty())
    }
}
