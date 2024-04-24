package com.example.testBackend.Repository;

import com.example.testBackend.Entity.Subchapter;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SubchapterRepository extends MongoRepository<Subchapter, String> {

    List<Subchapter> findByChapterId(String chapterId);
}
