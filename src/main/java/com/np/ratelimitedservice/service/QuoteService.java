package com.np.ratelimitedservice.service;

import com.np.ratelimitedservice.dto.QuoteResponse;
import com.np.ratelimitedservice.model.Quote;

import java.util.List;

public interface QuoteService {

    /**
     * Generates fresh quotes. It will generate exactly {@code numOfQuotes} quotes of the wanted author.
     *
     * @param author      author whose quotes we wish
     * @param numOfQuotes the number of wanted quotes
     * @return the list of the quotes
     */
    List<QuoteResponse> generateQuotes(String author, int numOfQuotes);

    /**
     * List quotes of the wanted author. It will return no more than {@code numOfQuotes} quotes.
     *
     * @param author      author whose quotes we wish
     * @param numOfQuotes the number of wanted quotes
     * @return the list of the quotes
     */
    List<QuoteResponse> listQuotes(String author, int numOfQuotes);

    /**
     * Generates fresh raw quotes. It will generate exactly {@code numOfQuotes} quotes of the wanted author.
     *
     * @param author      author whose quotes we wish
     * @param numOfQuotes the number of wanted quotes
     * @return the list of the quotes (unmapped, entity type)
     */
    List<Quote> generateRawQuotes(String author, int numOfQuotes);

}
