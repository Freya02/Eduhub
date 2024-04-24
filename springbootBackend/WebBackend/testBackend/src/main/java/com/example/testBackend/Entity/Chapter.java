package com.example.testBackend.Entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.List;

@Document(collection = "chapters")
public class Chapter {

    @Id
    private String id;
    private String courseId;
    private String title;
    private String description;

    private int order; // Order of the chapter within the course
    // Getters and setters

    private List<Subchapter> subchapters = new ArrayList<> ();

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCourseId() {
        return courseId;
    }

    public void setCourseId(String courseId) {
        this.courseId = courseId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
    public int getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
    }

    public List<Subchapter> getSubchapters() {
        return subchapters;
    }

    public void setSubchapters(List<Subchapter> subchapters) {
        this.subchapters = subchapters;
    }

    public void addSubchapter(Subchapter subchapter) {
        this.subchapters.add(subchapter);
    }




    // Other methods if needed
}
