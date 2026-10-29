package com.drew.taskapi;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
class TaskControllerTest {

    @Autowired
    private WebApplicationContext context;

    @Autowired
    private TaskRepository repo;

    private MockMvc mvc;

    @BeforeEach
    void setUp() {
        mvc = MockMvcBuilders.webAppContextSetup(context).build();
        repo.deleteAll();   // each test starts with an empty table
    }

    @Test
    void createTask_returns201WithBody() throws Exception {
        mvc.perform(post("/api/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"title\":\"Write tests\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.title").value("Write tests"))
                .andExpect(jsonPath("$.done").value(false));
    }

    @Test
    void createTask_blankTitle_returns400() throws Exception {
        mvc.perform(post("/api/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"title\":\"\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors.title").value("title is required"));
    }

    @Test
    void createTask_malformedJson_returns400() throws Exception {
        mvc.perform(post("/api/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"title\":\"oops"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Malformed JSON request body"));
    }

    @Test
    void getTask_missing_returns404() throws Exception {
        mvc.perform(get("/api/tasks/999"))
                .andExpect(status().isNotFound());
    }

    @Test
    void patchTask_updatesDoneAndKeepsTitle() throws Exception {
        Task task = repo.save(new Task("Original"));

        mvc.perform(patch("/api/tasks/" + task.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"done\":true}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Original"))
                .andExpect(jsonPath("$.done").value(true));
    }

    @Test
    void deleteTask_returns204ThenGone() throws Exception {
        Task task = repo.save(new Task("Delete me"));

        mvc.perform(delete("/api/tasks/" + task.getId()))
                .andExpect(status().isNoContent());

        mvc.perform(get("/api/tasks/" + task.getId()))
                .andExpect(status().isNotFound());
    }
}
