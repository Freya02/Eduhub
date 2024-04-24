package com.example.testBackend.Controller;

import com.example.testBackend.Entity.Subchapter;
import com.example.testBackend.Service.AmazonS3Client;
import com.example.testBackend.Service.SubchapterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/courses/{courseId}/chapters/{chapterId}/subchapters")
public class SubchapterController {

    @Autowired
    private SubchapterService subchapterService;

    @Autowired
    private AmazonS3Client amazonS3Client;

    @PostMapping
    public ResponseEntity<Subchapter> createSubchapter(@PathVariable String courseId,
                                                       @PathVariable String chapterId,
                                                       @RequestPart("content") MultipartFile content,
                                                       @RequestParam("title") String title,
                                                       @RequestParam("description") String description) {
        Subchapter subchapter = new Subchapter();
        subchapter.setTitle(title);
        subchapter.setDescription(description);
        subchapter.setChapterId (chapterId);

        try {
            Subchapter createdSubchapter = subchapterService.createSubchapter(courseId, chapterId, subchapter, content);
            return new ResponseEntity<>(createdSubchapter, HttpStatus.CREATED);
        } catch (IOException e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    @GetMapping
    public ResponseEntity<List<Subchapter>> getAllSubchaptersByChapterId(@PathVariable String chapterId) {
        List<Subchapter> subchapters = subchapterService.getAllSubchaptersByChapterId(chapterId);
        return new ResponseEntity<>(subchapters, HttpStatus.OK);
    }

    @GetMapping("/{subchapterId}")
    public ResponseEntity<Subchapter> getSubchapterById(@PathVariable String subchapterId) {
        Optional<Subchapter> subchapter = Optional.ofNullable (subchapterService.getSubchapterById (subchapterId));
        return subchapter.map(value -> new ResponseEntity<>(value, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @PutMapping("/{subchapterId}")
    public ResponseEntity<Subchapter> updateSubchapter(@PathVariable String courseId,
                                                       @PathVariable String chapterId,
                                                       @PathVariable String subchapterId,
                                                       @RequestParam("newTitle") String newTitle,
                                                       @RequestParam("newDescription") String newDescription,
                                                       @RequestPart(value = "newContent", required = false) MultipartFile newContent) {
        try {
            Subchapter updatedSubchapter;
            Subchapter existingSubchapter = subchapterService.getSubchapterById(subchapterId);

            if (newContent != null) {
                // If new content is provided, delete the old file from S3
                amazonS3Client.deleteFile(existingSubchapter.getContentUrl());
                // Upload the new content to S3 and update the subchapter
                String contentUrl = amazonS3Client.uploadContent(newContent, courseId, chapterId, subchapterId);
                existingSubchapter.setContentUrl(contentUrl); // Update the content URL
            }  // Update the subchapter details
            existingSubchapter.setTitle(newTitle);
            existingSubchapter.setDescription(newDescription);

            // Save the updated subchapter
            Subchapter updatedSubchapterr = subchapterService.updateSubchapter(subchapterId, newTitle, newDescription, newContent);

            return ResponseEntity.ok(updatedSubchapterr);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }



    @DeleteMapping("/{subchapterId}")
    public ResponseEntity<Void> deleteSubchapter(@PathVariable String subchapterId) {
        subchapterService.deleteSubchapter(subchapterId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
