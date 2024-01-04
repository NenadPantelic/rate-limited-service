package com.np.ratelimitedservice.dto;

import lombok.Builder;

@Builder
public record QuoteResponse(String id, String text, String author) {
}
