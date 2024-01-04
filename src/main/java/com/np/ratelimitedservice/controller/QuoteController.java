package com.np.ratelimitedservice.controller;

import com.np.ratelimitedservice.dto.QuoteResponse;
import com.np.ratelimitedservice.service.QuoteService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping(path = "/api/v1/quotes")
public class QuoteController {

    private final QuoteService quoteService;

    public QuoteController(QuoteService quoteService) {
        this.quoteService = quoteService;
    }

    @GetMapping
    public List<QuoteResponse> generateQuotes(@RequestParam("author") String author,
                                              @RequestParam(value = "numOfQuotes", defaultValue = "1") int numOfQuotes) {
        log.info("Received a request to generate quotes: author={}, numOfQuotes={}", author, numOfQuotes);
        return quoteService.generateQuotes(author, numOfQuotes);
    }

    @GetMapping("/{author}")
    public List<QuoteResponse> listQuotes(@PathVariable("author") String author,
                                          @RequestParam(value = "numOfQuotes") int numOfQuotes) {
        log.info("Received a request to list quotes: author={}, numOfQuotes={}", author, numOfQuotes);
        return quoteService.listQuotes(author, numOfQuotes);
    }

}
