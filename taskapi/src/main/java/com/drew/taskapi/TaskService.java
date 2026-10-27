package com.drew.taskapi;

import org.springframework.stereotype.Service;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import org.springframework.transaction.annotation.Transactional;

@Service
public class TaskService {
   
    private final TaskRepository repo;

    public TaskService(TaskRepository repo) {
        this.repo = repo;
    }

    public List<Task> findAll() {
        return repo.findAll();
    }

    public Optional<Task> findById(Long id) {
        return repo.findById(id);
    }

    public Task create(String title) {
        return repo.save(new Task(title));
    }

    public boolean delete(Long id) {
        if (!repo.existsById(id)) return false;
        repo.deleteById(id);
        return true;
    }

    @Transactional
    public Optional<Task> update(Long id, String title, Boolean done) {
        return repo.findById(id).map(task -> {
            if (title != null) task.setTitle(title);
            if (done != null) task.setDone(done);
            return task;
        });
    }
}
