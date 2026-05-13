package ee.rainer.todo.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import ee.rainer.todo.dto.TodoRequest;
import ee.rainer.todo.dto.TodoResponse;
import ee.rainer.todo.exception.TodoNotFoundException;
import ee.rainer.todo.service.TodoService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(TodoController.class)
class TodoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private TodoService service;

    private TodoResponse sampleResponse() {
        return new TodoResponse(1L, "Test todo", "Description", false, LocalDateTime.now());
    }

    // --- GET /api/todos ---

    @Test
    void shouldReturnAllTodosWithStatus200() throws Exception {
        when(service.findAll()).thenReturn(List.of(sampleResponse()));

        mockMvc.perform(get("/api/todos"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[0].title", is("Test todo")));
    }

    @Test
    void shouldReturnEmptyListWhenNoTodosExist() throws Exception {
        when(service.findAll()).thenReturn(List.of());

        mockMvc.perform(get("/api/todos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    // --- GET /api/todos/{id} ---

    @Test
    void shouldReturnTodoByIdWithStatus200() throws Exception {
        when(service.findById(1L)).thenReturn(sampleResponse());

        mockMvc.perform(get("/api/todos/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.title", is("Test todo")))
                .andExpect(jsonPath("$.completed", is(false)));
    }

    @Test
    void shouldReturn404WhenTodoIdDoesNotExist() throws Exception {
        when(service.findById(99L)).thenThrow(new TodoNotFoundException(99L));

        mockMvc.perform(get("/api/todos/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status", is(404)))
                .andExpect(jsonPath("$.message", containsString("99")));
    }

    // --- POST /api/todos ---

    @Test
    void shouldCreateTodoAndReturn201WithLocationHeader() throws Exception {
        TodoRequest request = new TodoRequest("New todo", "Desc", false);
        when(service.create(any())).thenReturn(sampleResponse());

        mockMvc.perform(post("/api/todos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"))
                .andExpect(header().string("Location", containsString("/api/todos/1")))
                .andExpect(jsonPath("$.id", is(1)));
    }

    @Test
    void shouldReturn400WhenTitleIsBlank() throws Exception {
        TodoRequest request = new TodoRequest("", "Desc", false);

        mockMvc.perform(post("/api/todos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status", is(400)));
    }

    @Test
    void shouldReturn400WhenBodyIsMissing() throws Exception {
        mockMvc.perform(post("/api/todos")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status", is(400)));
    }

    @Test
    void shouldReturn415WhenContentTypeIsNotJson() throws Exception {
        mockMvc.perform(post("/api/todos")
                        .contentType(MediaType.TEXT_PLAIN)
                        .content("plain text"))
                .andExpect(status().isUnsupportedMediaType());
    }

    // --- PUT /api/todos/{id} ---

    @Test
    void shouldUpdateTodoAndReturn200() throws Exception {
        TodoRequest request = new TodoRequest("Updated", "New desc", true);
        TodoResponse updated = new TodoResponse(1L, "Updated", "New desc", true, LocalDateTime.now());
        when(service.update(eq(1L), any())).thenReturn(updated);

        mockMvc.perform(put("/api/todos/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title", is("Updated")))
                .andExpect(jsonPath("$.completed", is(true)));
    }

    @Test
    void shouldReturn404WhenUpdatingNonExistentTodo() throws Exception {
        TodoRequest request = new TodoRequest("Title", null, false);
        when(service.update(eq(99L), any())).thenThrow(new TodoNotFoundException(99L));

        mockMvc.perform(put("/api/todos/99")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status", is(404)));
    }

    @Test
    void shouldReturn400WhenUpdatingWithBlankTitle() throws Exception {
        TodoRequest request = new TodoRequest("", null, false);

        mockMvc.perform(put("/api/todos/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    // --- DELETE /api/todos/{id} ---

    @Test
    void shouldDeleteTodoAndReturn204() throws Exception {
        doNothing().when(service).delete(1L);

        mockMvc.perform(delete("/api/todos/1"))
                .andExpect(status().isNoContent());

        verify(service, times(1)).delete(1L);
    }

    @Test
    void shouldReturn404WhenDeletingNonExistentTodo() throws Exception {
        doThrow(new TodoNotFoundException(99L)).when(service).delete(99L);

        mockMvc.perform(delete("/api/todos/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status", is(404)));
    }

    // --- Method not allowed ---

    @Test
    void shouldReturn405WhenUsingPatchMethod() throws Exception {
        mockMvc.perform(patch("/api/todos/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isMethodNotAllowed())
                .andExpect(jsonPath("$.status", is(405)));
    }
}
