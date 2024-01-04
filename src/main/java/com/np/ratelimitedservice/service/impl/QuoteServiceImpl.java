package com.np.ratelimitedservice.service.impl;

import com.np.ratelimitedservice.properties.QuoteGenerationConfig;
import com.np.ratelimitedservice.dto.QuoteResponse;
import com.np.ratelimitedservice.exception.ApiException;
import com.np.ratelimitedservice.mapper.QuoteMapper;
import com.np.ratelimitedservice.model.Quote;
import com.np.ratelimitedservice.repository.QuoteRepository;
import com.np.ratelimitedservice.service.QuoteService;
import com.np.ratelimitedservice.util.RandomGenerator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class QuoteServiceImpl implements QuoteService {

    private final QuoteGenerationConfig quoteGenerationConfig;
    private final QuoteRepository quoteRepository;

    public QuoteServiceImpl(QuoteGenerationConfig quoteGenerationConfig,
                            QuoteRepository quoteRepository) {
        this.quoteGenerationConfig = quoteGenerationConfig;
        this.quoteRepository = quoteRepository;
    }

    @Transactional
    @Override
    public List<QuoteResponse> generateQuotes(String author, int numOfQuotes) {
        if (author == null || author.isBlank()) {
            author = RandomGenerator.getRandomFullName();
            log.info("The author has not been provided, using random author {}...", author);
        }

        List<Quote> quotes = generateRawQuotes(author, numOfQuotes);
        return quotes.stream()
                .map(QuoteMapper::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<QuoteResponse> listQuotes(String author, int numOfQuotes) {
        log.info(
                "Listing quotes based on the following requirements: author={}, numOfQuotes={}",
                author, numOfQuotes
        );

        validateAuthor(author);

        PageRequest page = PageRequest.of(0, numOfQuotes, Sort.by(Sort.Direction.DESC, "createdAt"));
        List<Quote> quotes = quoteRepository.findByAuthor(author, page);

        return quotes.stream()
                .map(QuoteMapper::mapToDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public List<Quote> generateRawQuotes(String author, int numOfQuotes) {
        log.info(
                "Generating quotes based on the following requirements: author={}, numOfQuotes={}",
                author, numOfQuotes
        );

        validateNumOfQuotes(numOfQuotes);
        List<Quote> quotes = new ArrayList<>();

        for (int i = 0; i < numOfQuotes; i++) {
            quotes.add(generateQuote(author));
        }

        quoteRepository.saveAll(quotes);
        return quotes;
    }

    private void validateNumOfQuotes(int numOfQuotes) {
        if (numOfQuotes <= 0) {
            String errorMessage = String.format(
                    "The number of quotes must be a positive number not greater than %d.", quoteGenerationConfig.getLimit()
            );
            log.warn(errorMessage);
            throw ApiException.BAD_REQUEST.withError(errorMessage);

        }
    }

    private void validateAuthor(String author) {
        if (author == null || author.isBlank()) {
            String errorMessage = "The author must be provided.";
            log.warn(errorMessage);
            throw ApiException.BAD_REQUEST.withError(errorMessage);
        }
    }

    private Quote generateQuote(String author) {
        return Quote.builder()
                .text(
                        RandomGenerator.getRandomText(
                                quoteGenerationConfig.getMinWordsCounter(),
                                quoteGenerationConfig.getMaxWordsCounter()
                        )
                )
                .author(author)
                .build();
    }
}
