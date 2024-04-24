package com.example.testBackend.Controller;

import com.example.testBackend.Entity.Course;
import com.example.testBackend.Service.CourseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/courses")
public class CourseController {

    @Autowired
    private CourseService courseService;

    @PostMapping("/{userId}/create")
    public ResponseEntity<Course> createCourse(
            @PathVariable String userId,
            @RequestParam("file") MultipartFile file,
            @RequestParam("title") String title,
            @RequestParam("instructor") String instructor  ,
            @RequestParam("description") String description,
            @RequestParam("category") String category

    ) {
        Course course = new Course();
        course.setTitle(title);
        course.setInstructor(instructor);
        course.setDescription(description);
        course.setCategory(category);
        course.setUserId(userId); // Set the userId for the course

        Course createdCourse = courseService.createCourse(course, file, userId);
        if (createdCourse != null) {
            return new ResponseEntity<>(createdCourse, HttpStatus.CREATED);
        } else {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/all/{userId}")
    public ResponseEntity<List<Course>> getAllCourses(@PathVariable("userId") String userId) {
        List<Course> courses = courseService.getAllCourses(userId);
        if (courses.isEmpty()) {
            // Fetch all courses from the database
            courses = courseService.getAllCoursess();
        }
        return ResponseEntity.ok(courses);
    }


    @PutMapping("/edit/{userId}/{courseId}")
    public ResponseEntity<?> updateCourse(
                                          @PathVariable String userId,
                                          @PathVariable String courseId,
                                          @RequestParam(value = "file", required = false) MultipartFile file,
                                          @RequestParam("title") String title,
                                          @RequestParam("instructor") String instructor,
                                          @RequestParam("description") String description,
                                          @RequestParam("category") String category
                                          ){

        Course updatedCourse = new Course();
        updatedCourse.setTitle(title);
        updatedCourse.setInstructor(instructor);
        updatedCourse.setDescription(description);
        updatedCourse.setCategory(category);
        updatedCourse.setUserId(userId);

        // Check if a new image file is provided
        if (file != null && !file.isEmpty()) {
            try {
                // Convert MultipartFile to byte array and set it in the course object
                updatedCourse.setImage(file.getBytes());
            } catch (IOException e) {
                // Handle exception
                e.printStackTrace();
                // You might want to throw an exception or handle the error accordingly
            }
        }


        Course course = courseService.updateCourse(courseId, updatedCourse, file);
        if (course != null) {
            return ResponseEntity.ok(course);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/delete/{courseId}")
    public ResponseEntity<?> deleteCourse(@PathVariable String courseId) {
        boolean deleted = courseService.deleteCourse(courseId);
        if (deleted) {
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/{courseId}/image")
    public ResponseEntity<String> uploadCourseImage(@PathVariable String courseId, @RequestParam("file") MultipartFile file) {
        String result = courseService.uploadCourseImage(courseId, file);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/api/users/{userId}/courses/search")
    public List<Course> searchCoursesByUserId( @RequestParam String term) {

        return courseService.searchCoursesByUserIdAndTerm(term);
    }
}
