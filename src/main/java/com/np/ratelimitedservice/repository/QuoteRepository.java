package com.np.ratelimitedservice.repository;

import com.np.ratelimitedservice.model.Quote;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface QuoteRepository extends MongoRepository<Quote, String> {

    List<Quote> findByAuthor(String author, Pageable pageable);
}
