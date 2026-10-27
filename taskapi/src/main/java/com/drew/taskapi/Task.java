package com.drew.taskapi;

import jakarta.persistence.*;

@Entity
@Table(name = "tasks")
public class Task {
    @Id 
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    private boolean done = false;

    protected Task() {}

    public Task(String title) {
        this.title = title;
    }

    public Long getId() { return id; }
    public String getTitle() { return title; }
    public boolean isDone() { return done; }
    public void setDone(boolean done) { this.done = done; }
    public void setTitle(String title) { this.title = title; }

}
