package com.example.testBackend.Service;

import com.example.testBackend.Entity.Course;
import com.example.testBackend.Repository.CourseRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;
import java.io.*;


@Service
public class CourseService {

    @Autowired
    private AmazonS3Client amazonS3Client;

    @Autowired
    private CourseRepository courseRepository;

    @Value("${amazon.s3.courseImageFolder}")
    private String courseImageFolder;

    public Course createCourse(Course course, MultipartFile file,  String userId){
        try {
            course.setImage(file.getBytes());
            course.setUserId(userId);
            return courseRepository.save(course);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public Optional<Course> getCourseById(String id)  {
        return courseRepository.findById(id);
    }
    public List<Course> getAllCourses(String userId) {
        return courseRepository.findByUserId(userId) ;
    }

    public List<Course> getAllCoursess() {
        return courseRepository.findAll();
    }
    public Course updateCourse(String courseId, Course updatedCourse,  MultipartFile file) {
        Optional<Course> optionalCourse = courseRepository.findById(courseId);
        if (optionalCourse.isPresent()) {
            Course existingCourse = optionalCourse.get();
            existingCourse.setTitle(updatedCourse.getTitle());
            existingCourse.setInstructor(updatedCourse.getInstructor());
            existingCourse.setDescription(updatedCourse.getDescription());
            existingCourse.setCategory (updatedCourse.getCategory ());

            if (file != null && !file.isEmpty()) {
                try {
                    existingCourse.setImage(file.getBytes());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return courseRepository.save(existingCourse);
        } else {
            return null;
        }
    }

    public boolean deleteCourse(String courseId) {
        courseRepository.deleteById(courseId);
        return false;
    }

    public String uploadCourseImage(String courseId, MultipartFile file) {
        Course course = courseRepository.findById(courseId).orElse(null);
        if (course == null) {
            throw new EntityNotFoundException ("Course not found");
        }
        return amazonS3Client.uploadCourseImage(file, courseId);
    }
    public List<Course> searchCoursesByUserIdAndTerm(String term) {
        return courseRepository.findByUserIdAndTitleContaining( term);
    }
}
