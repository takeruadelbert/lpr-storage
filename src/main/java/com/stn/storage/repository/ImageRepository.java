package com.stn.storage.repository;

import com.stn.storage.entity.Image;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface ImageRepository extends MongoRepository<Image, String> {
    Optional<Image> findFirstByToken(String token);
}
