package com.example.testBackend.Controller;

import com.example.testBackend.Entity.Chapter;
import com.example.testBackend.Service.ChapterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/courses/{courseId}/chapters")
public class ChapterController {

    @Autowired
    private ChapterService chapterService;

    @PostMapping
    public ResponseEntity<Chapter> createChapter(@PathVariable String courseId, @RequestBody Chapter chapter) {
        Chapter createdChapter = chapterService.createChapter(courseId, chapter);
        return new ResponseEntity<>(createdChapter, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<Chapter>> getAllChaptersByCourseId(@PathVariable String courseId) {
        List<Chapter> chapters = chapterService.getAllChaptersByCourseId(courseId);
        return new ResponseEntity<>(chapters, HttpStatus.OK);
    }

    @GetMapping("/{chapterId}")
    public ResponseEntity<Chapter> getChapterById(@PathVariable String chapterId) {
        Chapter chapter = chapterService.getChapterById(chapterId);
        if (chapter != null) {
            return new ResponseEntity<>(chapter, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @PutMapping("/{chapterId}")
    public ResponseEntity<Chapter> updateChapter(@PathVariable String chapterId, @RequestBody Chapter updatedChapter) {
        Chapter chapter = chapterService.updateChapter(chapterId, updatedChapter);
        if (chapter != null) {
            return new ResponseEntity<>(chapter, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping("/{chapterId}")
    public ResponseEntity<Void> deleteChapter(@PathVariable String chapterId) {
        chapterService.deleteChapter(chapterId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
