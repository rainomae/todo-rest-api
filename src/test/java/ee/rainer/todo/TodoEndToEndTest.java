package ee.rainer.todo;

import com.fasterxml.jackson.databind.ObjectMapper;
import ee.rainer.todo.dto.TodoRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class TodoEndToEndTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void fullCrudFlow() throws Exception {
        // 1. Initially empty
        mockMvc.perform(get("/api/todos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));

        // 2. Create a todo
        TodoRequest createRequest = new TodoRequest("Buy milk", "From the store", false);
        MvcResult createResult = mockMvc.perform(post("/api/todos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"))
                .andExpect(jsonPath("$.id", notNullValue()))
                .andExpect(jsonPath("$.title", is("Buy milk")))
                .andExpect(jsonPath("$.completed", is(false)))
                .andReturn();

        String responseBody = createResult.getResponse().getContentAsString();
        Long id = objectMapper.readTree(responseBody).get("id").asLong();

        // 3. Fetch by id
        mockMvc.perform(get("/api/todos/" + id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title", is("Buy milk")));

        // 4. List now has one item
        mockMvc.perform(get("/api/todos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)));

        // 5. Update
        TodoRequest updateRequest = new TodoRequest("Buy milk and eggs", "From the store", true);
        mockMvc.perform(put("/api/todos/" + id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title", is("Buy milk and eggs")))
                .andExpect(jsonPath("$.completed", is(true)));

        // 6. Delete
        mockMvc.perform(delete("/api/todos/" + id))
                .andExpect(status().isNoContent());

        // 7. Gone after delete
        mockMvc.perform(get("/api/todos/" + id))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status", is(404)));

        // 8. List empty again
        mockMvc.perform(get("/api/todos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    void shouldReturn400ForBlankTitle() throws Exception {
        TodoRequest request = new TodoRequest("", null, false);

        mockMvc.perform(post("/api/todos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status", is(400)))
                .andExpect(jsonPath("$.message", notNullValue()))
                .andExpect(jsonPath("$.path", is("/api/todos")));
    }

    @Test
    void shouldReturn404ForNonExistentId() throws Exception {
        mockMvc.perform(get("/api/todos/999999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status", is(404)))
                .andExpect(jsonPath("$.error", is("Not Found")));
    }
}
