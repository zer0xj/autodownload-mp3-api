package com.julien.search

import org.assertj.core.api.Assertions
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.web.server.LocalServerPort
import org.springframework.core.ParameterizedTypeReference
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.http.MediaType
import org.springframework.test.context.junit4.SpringRunner
import org.springframework.web.client.RestTemplate

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
class SearchYoutubeApplicationTests {

    @Autowired
    private RestTemplate restTemplate = new RestTemplate()

    @LocalServerPort
    private String localport

    private final ParameterizedTypeReference<Map<String, Object>> genericResponseType =
            new ParameterizedTypeReference<HashMap<String, Object>>() {}

    private static HttpHeaders getHeaders(long userId) {
        HttpHeaders headers = getBaseHeaders()
        return headers
    }

    private static HttpHeaders getBaseHeaders() {
        HttpHeaders headers = new HttpHeaders()
        headers.setContentType(MediaType.APPLICATION_JSON)
        return headers
    }

    @Test
    void paymentControllerTest1() {

        // Get a user's payment types

        String paymentMethods = ""

        HttpEntity<String> httpEntity = new HttpEntity()

        try {
            paymentMethods = Arrays.asList(this.restTemplate.exchange("http://localhost:$localport/v1/$failureUserId/paymentmethods", HttpMethod.POST,
                    httpEntity, String.class).getBody())
        } catch (Exception ignored) {}
        Assertions.assertThat(paymentMethods.length() == 0)
    }
}
