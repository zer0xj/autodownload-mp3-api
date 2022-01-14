package com.julien.search.controller.v1

import com.julien.search.model.YoutubeVideo
import com.julien.search.service.SearchService
import io.swagger.annotations.ApiOperation
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.*

@Controller
class SearchController {

    @Autowired
    private lateinit var searchService: SearchService

    @ApiOperation(value = "Search YouTube for a given term", notes =
    """
        Example request:
        GET /v1/search?query=song%20name
        (no body)
    """)
    @RequestMapping(value = ["/v1/search"], method = [RequestMethod.GET])
    @ResponseBody
    fun search(@RequestParam query: String): List<YoutubeVideo> {

        return searchService.search(query)
    }

    @ApiOperation(value = "Search YouTube for a given term and download the mp3", notes =
    """
        Example request:
        GET /v1/search/download?query=song%20name
        (no body)
    """)
    @RequestMapping(value = ["/v1/search/download"], method = [RequestMethod.GET])
    @ResponseBody
    fun searchAndDownload(@RequestParam query: String): ResponseEntity<String> {

        val response = searchService.searchAndDownload(query)

        return if (response != null) {
            ResponseEntity.ok("Successfully downloaded ${response.url} for search query[$query]" +
                    if (response.filename != null) {
                        " as \"${response.filename}\""
                    } else {
                        ""
                    })
        } else {
            ResponseEntity.badRequest().body("Could not find a video for query[$query]")
        }
    }
}
