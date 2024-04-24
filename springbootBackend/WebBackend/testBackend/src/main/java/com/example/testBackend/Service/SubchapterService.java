package com.example.testBackend.Service;

import com.example.testBackend.Entity.Chapter;
import com.example.testBackend.Entity.Course;
import com.example.testBackend.Entity.Subchapter;
import com.example.testBackend.Repository.ChapterRepository;
import com.example.testBackend.Repository.SubchapterRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class SubchapterService {

    @Autowired
    private SubchapterRepository subchapterRepository;

    @Autowired
    private AmazonS3Client amazonS3Client;

    @Autowired
    private ChapterService chapterService;

    @Autowired
    private ChapterRepository chapterRepository;

    public Subchapter createSubchapter (String courseId, String chapterId, Subchapter subchapter, MultipartFile content) throws IOException {
        Subchapter createdSubchapter = subchapterRepository.save(subchapter);
        String contentUrl = amazonS3Client.uploadContent (content, courseId, chapterId, createdSubchapter.getId());
        Subchapter subchapterr = subchapterRepository.findById(createdSubchapter.getId())
                .orElseThrow(() -> new IllegalArgumentException("Subchapter not found with ID: " + createdSubchapter.getId ()));

        subchapterr.setContentUrl (contentUrl);
        Subchapter updateSubchapter = subchapterRepository.save(subchapterr);
        chapterService.addSubchapterToChapter(chapterId, updateSubchapter);
        return updateSubchapter;
    }


    public List<Subchapter> getAllSubchaptersByChapterId (String chapterId) {
        return subchapterRepository.findByChapterId (chapterId);
    }

    public Subchapter getSubchapterById (String subchapterId) {
        Optional<Subchapter> optionalSubchapter = subchapterRepository.findById (subchapterId);
        if (optionalSubchapter.isPresent ()) {
            return optionalSubchapter.get ();
        } else {
            throw new ResponseStatusException (HttpStatus.NOT_FOUND, "Subchapter not found");
        }
    }

    public Subchapter updateSubchapter (String subchapterId, String newTitle, String newDescription, MultipartFile newContent) throws IOException {
        Subchapter subchapter = subchapterRepository.findById (subchapterId)
                .orElseThrow (() -> new IllegalArgumentException ("Subchapter not found with ID: " + subchapterId));

        subchapter.setTitle (newTitle);
        subchapter.setDescription (newDescription);
        String chapterId = subchapter.getChapterId ();
        Optional<Chapter> chapterOptional = chapterRepository.findById (chapterId);
        Chapter chapter = chapterOptional.get ();
        String courseId = chapter.getCourseId ();
        String oldContentUrl = subchapter.getContentUrl ();
        System.out.println ("dattttttt");
        System.out.println (oldContentUrl);
        return subchapterRepository.save (subchapter);
    }
    
    public void deleteSubchapter (String subchapterId) {
        Optional<Subchapter> optionalSubchapter = subchapterRepository.findById(subchapterId);
        if (optionalSubchapter.isPresent()) {
            Subchapter subchapter = optionalSubchapter.get();
            subchapterRepository.deleteById(subchapterId);
        } else {
            throw new IllegalArgumentException("Subchapter not found with ID: " + subchapterId);
        }
    }
}