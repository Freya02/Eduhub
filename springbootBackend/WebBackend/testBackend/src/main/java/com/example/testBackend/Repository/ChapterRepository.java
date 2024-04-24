package com.example.testBackend.Repository;

import com.example.testBackend.Entity.Chapter;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface ChapterRepository extends MongoRepository<Chapter, String> {
    List<Chapter> findByCourseId(String courseId);

}
