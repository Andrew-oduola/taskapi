package com.drew.taskapi;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/tasks")
public class TaskController {

    private final TaskService service;

    public TaskController(TaskService service) {
        this.service = service;
    }

    public record CreateTaskRequest(
            @NotBlank(message = "title is required")
            @Size(max = 200, message = "title must be at most 200 characters")
            String title) {}

    public record UpdateTaskRequest(
            @Size(min = 1, max = 200, message = "title must be 1-200 characters")
            String title,
            Boolean done) {}

    @GetMapping
    public List<Task> list() {
        return service.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Task> get(@PathVariable Long id) {
        return service.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Task create(@Valid @RequestBody CreateTaskRequest req) {
        return service.create(req.title());
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Task> update(@PathVariable Long id,
                                       @Valid @RequestBody UpdateTaskRequest req) {
        return service.update(id, req.title(), req.done())
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        return service.delete(id)
                ? ResponseEntity.noContent().build()
                : ResponseEntity.notFound().build();
    }
}
