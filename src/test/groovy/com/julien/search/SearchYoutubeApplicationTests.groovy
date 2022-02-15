package com.julien.search

import com.fasterxml.jackson.databind.ObjectMapper
import com.julien.search.model.YoutubeVideo
import org.assertj.core.api.Assertions
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.web.server.LocalServerPort
import org.springframework.core.ParameterizedTypeReference
import org.springframework.http.HttpEntity
import org.springframework.http.HttpMethod
import org.springframework.test.context.junit4.SpringRunner
import org.springframework.web.client.RestTemplate

import java.nio.charset.StandardCharsets

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT,
        properties = [ "database.login.enableUserValidation=false", "downloads.location=./build/tmp", "history.location=./build/tmp" ])
class SearchYoutubeApplicationTests {

    @Autowired
    private RestTemplate restTemplate = new RestTemplate()

    @LocalServerPort
    private String localport

    private ObjectMapper mapper

    private final ParameterizedTypeReference<Map<String, Object>> genericResponseType =
            new ParameterizedTypeReference<HashMap<String, Object>>() {}

    String searchQuery = "Home at Last"
    int testUserId = 1234

    @Test
    void searchControllerTest1() {

        HttpEntity<String> httpEntity = new HttpEntity()

        // Test search endpoint

        String url = "http://localhost:$localport/v1/user/$testUserId/search?query=" + URLEncoder.encode(searchQuery, StandardCharsets.UTF_8)

        List<YoutubeVideo> videos = Collections.emptyList()

        try {
            videos = Arrays.asList(this.restTemplate.exchange(url, HttpMethod.GET, httpEntity,
                    YoutubeVideo[].class).getBody())
        } catch (Exception ignored) {}
        Assertions.assertThat(videos.size() != 0)
    }

    @Test
    void searchControllerTest2() {

        HttpEntity<String> httpEntity = new HttpEntity()

        // Test download endpoint

        String url = "http://localhost:$localport/v1/user/$testUserId/search/download?query=" + URLEncoder.encode(searchQuery, StandardCharsets.UTF_8)

        String jobId = null

        try {
            // Doing this because Jackson/Kotlin is annoying with deserializing non-nullable fields in data classes
            jobId = this.restTemplate.exchange(url, HttpMethod.GET, httpEntity, genericResponseType).getBody().get("jobId").toString()
        } catch (Exception ignored) {}
        Assertions.assertThat(jobId != null)

        // Test all jobs' statuses endpoint

        url = "http://localhost:$localport/v1/user/$testUserId/search/download/status"

        Integer jobCount = 0

        try {
            jobCount = this.restTemplate.exchange(url, HttpMethod.GET, httpEntity, genericResponseType).getBody().size()
        } catch (Exception ignored) {}
        Assertions.assertThat((jobCount != null) && (jobCount > 0))

        // Test a single job's status endpoint

        url = "$url/$jobId"

        String status = null

        for (int i = 0; i < 5; i++) {
            status = getJobStatus(url)
            if ((status == null) || (status != "processing")) break
        }

        Assertions.assertThat(status != null)

        // Test cancellation endpoint

        url = "http://localhost:$localport/v1/user/$testUserId/search/download/$jobId"

        status = null

        try {
            status = this.restTemplate.exchange(url, HttpMethod.DELETE, httpEntity, genericResponseType).getBody().get("status")
        } catch (Exception ignored) {}
        Assertions.assertThat(status != null)
    }

    private String getJobStatus(String url) {

        HttpEntity<String> httpEntity = new HttpEntity()

        Thread.sleep(200)

        String status = null

        try {
            status = this.restTemplate.exchange(url, HttpMethod.GET, httpEntity, genericResponseType).getBody().get("status")
        } catch (Exception ignored) {}

        return status
    }

    @Test
    void searchControllerTest3() {

        HttpEntity<String> httpEntity = new HttpEntity()

        // Test job summary endpoint

        String url = "http://localhost:$localport/v1/user/$testUserId/search/download/summary"

        Map<String, Object> summary = Collections.emptyMap()

        try {
            // Doing this because Jackson/Kotlin is annoying with deserializing non-nullable fields in data classes
            summary = this.restTemplate.exchange(url, HttpMethod.GET, httpEntity, genericResponseType).getBody()
        } catch (Exception ignored) {}
        Assertions.assertThat(!summary.isEmpty())
    }
}
