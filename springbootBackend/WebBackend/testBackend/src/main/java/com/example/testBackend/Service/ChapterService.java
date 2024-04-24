package com.example.testBackend.Service;

import com.example.testBackend.Entity.Chapter;
import com.example.testBackend.Entity.Course;
import com.example.testBackend.Entity.Subchapter;
import com.example.testBackend.Repository.ChapterRepository;
import com.example.testBackend.Repository.CourseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ChapterService {

    @Autowired
    private ChapterRepository chapterRepository;

    @Autowired
    private CourseRepository courseRepository;

    public Chapter createChapter (String courseId, Chapter chapter) {
        Optional<Course> optionalCourse = courseRepository.findById (courseId);
        if (optionalCourse.isPresent ()) {
            Course course = optionalCourse.get ();
            chapter.setCourseId (courseId);
            int order = course.getChapters ().size () + 1;
            chapter.setOrder (order);
            Chapter savedChapter = chapterRepository.save (chapter);
            course.addChapter (savedChapter);
            courseRepository.save (course);
            return savedChapter;
        }
        return null;
    }

    public List<Chapter> getAllChaptersByCourseId (String courseId) {
        return chapterRepository.findByCourseId (courseId);
    }

    public Chapter getChapterById (String chapterId) {
        Optional<Chapter> chapterOptional = chapterRepository.findById (chapterId);
        return chapterOptional.orElse (null);
    }

    public Chapter updateChapter (String chapterId, Chapter updatedChapter) {
        Optional<Chapter> optionalChapter = chapterRepository.findById (chapterId);
        if (optionalChapter.isPresent ()) {
            Chapter existingChapter = optionalChapter.get ();
            existingChapter.setTitle (updatedChapter.getTitle ());
            existingChapter.setDescription (updatedChapter.getDescription ());
            return chapterRepository.save (existingChapter);
        } else {
            return null;
        }
    }

    public void deleteChapter (String chapterId) {
        Optional<Chapter> optionalChapter = chapterRepository.findById (chapterId);
        if (optionalChapter.isPresent ()) {
            Chapter chapter = optionalChapter.get ();
            String courseId = chapter.getCourseId ();
            Optional<Course> optionalCourse = courseRepository.findById (courseId);
            if (optionalCourse.isPresent ()) {
                Course course = optionalCourse.get ();
                course.getChapters ().removeIf (c -> c.getId ().equals (chapterId));
                courseRepository.save (course);
            }
            chapterRepository.deleteById (chapterId);
        }
    }

    public Chapter addSubchapterToChapter (String chapterId, Subchapter subchapter) {
        Optional<Chapter> optionalChapter = chapterRepository.findById (chapterId);
        if (optionalChapter.isPresent ()) {
            Chapter chapter = optionalChapter.get ();
            chapter.addSubchapter (subchapter);
            return chapterRepository.save (chapter);
        }
        return null;
    }
    }

