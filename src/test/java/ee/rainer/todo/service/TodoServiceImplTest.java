package ee.rainer.todo.service;

import ee.rainer.todo.dto.TodoRequest;
import ee.rainer.todo.dto.TodoResponse;
import ee.rainer.todo.exception.TodoNotFoundException;
import ee.rainer.todo.model.Todo;
import ee.rainer.todo.repository.TodoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TodoServiceImplTest {

    @Mock
    private TodoRepository repository;

    @InjectMocks
    private TodoServiceImpl service;

    private Todo sampleTodo;

    @BeforeEach
    void setUp() {
        sampleTodo = new Todo();
        setField(sampleTodo, "id", 1L);
        sampleTodo.setTitle("Test todo");
        sampleTodo.setDescription("Test description");
        sampleTodo.setCompleted(false);
        setField(sampleTodo, "createdAt", LocalDateTime.now());
    }

    // --- findAll ---

    @Test
    void shouldReturnAllTodos() {
        // Arrange
        when(repository.findAll()).thenReturn(List.of(sampleTodo));

        // Act
        List<TodoResponse> result = service.findAll();

        // Assert
        assertThat(result).hasSize(1);
        assertThat(result.get(0).title()).isEqualTo("Test todo");
    }

    @Test
    void shouldReturnEmptyListWhenNoTodosExist() {
        // Arrange
        when(repository.findAll()).thenReturn(List.of());

        // Act
        List<TodoResponse> result = service.findAll();

        // Assert
        assertThat(result).isEmpty();
    }

    // --- findById ---

    @Test
    void shouldReturnTodoWhenIdExists() {
        // Arrange
        when(repository.findById(1L)).thenReturn(Optional.of(sampleTodo));

        // Act
        TodoResponse result = service.findById(1L);

        // Assert
        assertThat(result.id()).isEqualTo(1L);
        assertThat(result.title()).isEqualTo("Test todo");
        assertThat(result.completed()).isFalse();
    }

    @Test
    void shouldThrowTodoNotFoundExceptionWhenIdDoesNotExist() {
        // Arrange
        when(repository.findById(99L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> service.findById(99L))
                .isInstanceOf(TodoNotFoundException.class)
                .hasMessageContaining("99");
    }

    // --- create ---

    @Test
    void shouldCreateAndReturnTodoWithGivenData() {
        // Arrange
        TodoRequest request = new TodoRequest("New todo", "Some description", false);
        when(repository.save(any(Todo.class))).thenReturn(sampleTodo);

        // Act
        TodoResponse result = service.create(request);

        // Assert
        assertThat(result.title()).isEqualTo("Test todo");
        verify(repository, times(1)).save(any(Todo.class));
    }

    @Test
    void shouldCreateTodoWithCompletedTrue() {
        // Arrange
        Todo completedTodo = new Todo();
        setField(completedTodo, "id", 2L);
        completedTodo.setTitle("Done");
        completedTodo.setCompleted(true);
        setField(completedTodo, "createdAt", LocalDateTime.now());

        TodoRequest request = new TodoRequest("Done", null, true);
        when(repository.save(any(Todo.class))).thenReturn(completedTodo);

        // Act
        TodoResponse result = service.create(request);

        // Assert
        assertThat(result.completed()).isTrue();
    }

    // --- update ---

    @Test
    void shouldUpdateTodoWhenIdExists() {
        // Arrange
        Todo updated = new Todo();
        setField(updated, "id", 1L);
        updated.setTitle("Updated title");
        updated.setDescription("Updated desc");
        updated.setCompleted(true);
        setField(updated, "createdAt", LocalDateTime.now());

        TodoRequest request = new TodoRequest("Updated title", "Updated desc", true);
        when(repository.findById(1L)).thenReturn(Optional.of(sampleTodo));
        when(repository.save(any(Todo.class))).thenReturn(updated);

        // Act
        TodoResponse result = service.update(1L, request);

        // Assert
        assertThat(result.title()).isEqualTo("Updated title");
        assertThat(result.completed()).isTrue();
    }

    @Test
    void shouldThrowTodoNotFoundExceptionWhenUpdatingNonExistentId() {
        // Arrange
        TodoRequest request = new TodoRequest("Title", null, false);
        when(repository.findById(99L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> service.update(99L, request))
                .isInstanceOf(TodoNotFoundException.class)
                .hasMessageContaining("99");

        verify(repository, never()).save(any());
    }

    // --- delete ---

    @Test
    void shouldDeleteTodoWhenIdExists() {
        // Arrange
        when(repository.existsById(1L)).thenReturn(true);

        // Act
        service.delete(1L);

        // Assert
        verify(repository, times(1)).deleteById(1L);
    }

    @Test
    void shouldThrowTodoNotFoundExceptionWhenDeletingNonExistentId() {
        // Arrange
        when(repository.existsById(99L)).thenReturn(false);

        // Act & Assert
        assertThatThrownBy(() -> service.delete(99L))
                .isInstanceOf(TodoNotFoundException.class)
                .hasMessageContaining("99");

        verify(repository, never()).deleteById(any());
    }

    // Helper: set private/package-private fields via reflection (for @CreationTimestamp fields)
    private void setField(Object target, String fieldName, Object value) {
        try {
            var field = target.getClass().getDeclaredField(fieldName);
            field.setAccessible(true);
            field.set(target, value);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
