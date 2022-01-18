package com.julien.search.controller.v1

import com.julien.search.model.JobResponse
import com.julien.search.model.Mp3DownloadResponse
import com.julien.search.model.YoutubeVideo
import com.julien.search.service.SearchService
import io.swagger.annotations.ApiOperation
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.*

@Controller
class SearchController {

    @Autowired
    private lateinit var searchService: SearchService

    @ApiOperation(value = "Search YouTube for a given term", notes =
    """
        Example request:
        GET /v1/user/{userId}/search?query=song%20name
        (no body)
    """)
    @RequestMapping(value = ["/v1/user/{userId}/search"], method = [RequestMethod.GET])
    @ResponseBody
    fun search(@PathVariable("userId") userId: Int,
               @RequestParam(required = true) query: String): List<YoutubeVideo> = searchService.search(userId, query)

    @ApiOperation(value = "Search YouTube for a given term and download the mp3", notes =
    """
        Example request:
        GET /v1/user/{userId}/search/download?query=song%20name
        (no body)
    """)
    @RequestMapping(value = ["/v1/user/{userId}/search/download"], method = [RequestMethod.GET])
    @ResponseBody
    fun searchAndDownload(@PathVariable("userId") userId: Int,
                          @RequestParam(required = true) query: String): JobResponse? = searchService.searchAndDownload(userId, query)

    @ApiOperation(value = "Search YouTube for a given term and download the mp3", notes =
    """
        Example request:
        GET /v1/user/{userId}/search/download/status/{jobId}
        (no body)
    """)
    @RequestMapping(value = ["/v1/user/{userId}/search/download/status/{jobId}"], method = [RequestMethod.GET])
    @ResponseBody
    fun getDownloadStatus(@PathVariable("userId") userId: Int,
                          @PathVariable("jobId") jobId: String): Mp3DownloadResponse? = searchService.getDownloadStatus(userId, jobId)
}
