package com.julien.search.controller.v1

import com.julien.search.model.Mp3DownloadResponse
import com.julien.search.model.ProcessingJob
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
                          @RequestParam(required = true) query: String): ProcessingJob = searchService.searchAndDownload(userId, query)

    @ApiOperation(value = "Get download statuses for your user", notes =
    """
        Example request:
        GET /v1/user/{userId}/search/download/status
        (no body)
        
        Note: admin users will see the status of all jobs
    """)
    @RequestMapping(value = ["/v1/user/{userId}/search/download/status"], method = [RequestMethod.GET])
    @ResponseBody
    fun getDownloadStatus(@PathVariable("userId") userId: Int): List<Mp3DownloadResponse> = searchService.getProcessingJobs(userId)
}
