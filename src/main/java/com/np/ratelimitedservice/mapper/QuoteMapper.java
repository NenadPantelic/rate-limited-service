package com.np.ratelimitedservice.mapper;

import com.np.ratelimitedservice.dto.QuoteResponse;
import com.np.ratelimitedservice.model.Quote;

public class QuoteMapper {

    public static QuoteResponse mapToDTO(Quote quote) {
        if (quote == null) {
            return null;
        }

        return QuoteResponse.builder()
                .id(quote.getId())
                .author(quote.getAuthor())
                .text(quote.getText())
                .build();

    }
}
