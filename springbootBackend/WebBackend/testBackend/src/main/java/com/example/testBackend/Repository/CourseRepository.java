package com.example.testBackend.Repository;

import com.example.testBackend.Entity.Course;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CourseRepository extends MongoRepository<Course, String> {
    List<Course> findByUserId(String userId);

    List<Course> findByUserIdAndTitleContaining( String title);

}
