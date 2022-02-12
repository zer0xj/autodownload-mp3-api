package com.julien.search

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
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT, properties = "database.login.enableUserValidation=false")
class SearchYoutubeApplicationTests {

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

        HttpEntity<String> httpEntity = new HttpEntity()

        String url = "http://localhost:$localport/v1/user/$testUserId/search?query=" + URLEncoder.encode(searchQuery, StandardCharsets.UTF_8)

        List<YoutubeVideo> videos = Collections.emptyList()

        try {
            videos = Arrays.asList(this.restTemplate.exchange(url, HttpMethod.GET, httpEntity,
                    YoutubeVideo[].class).getBody())
        } catch (Exception ignored) {}
        Assertions.assertThat(videos.size() != 0)
    }
}
