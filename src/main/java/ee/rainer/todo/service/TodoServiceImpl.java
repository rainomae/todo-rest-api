package ee.rainer.todo.service;

import ee.rainer.todo.dto.TodoRequest;
import ee.rainer.todo.dto.TodoResponse;
import ee.rainer.todo.exception.TodoNotFoundException;
import ee.rainer.todo.model.Todo;
import ee.rainer.todo.repository.TodoRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class TodoServiceImpl implements TodoService {

    private static final Logger log = LoggerFactory.getLogger(TodoServiceImpl.class);

    private final TodoRepository repository;

    public TodoServiceImpl(TodoRepository repository) {
        this.repository = repository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<TodoResponse> findAll() {
        return repository.findAll().stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public TodoResponse findById(Long id) {
        return repository.findById(id)
                .map(this::toResponse)
                .orElseThrow(() -> new TodoNotFoundException(id));
    }

    @Override
    public TodoResponse create(TodoRequest request) {
        Todo todo = new Todo();
        todo.setTitle(request.title());
        todo.setDescription(request.description());
        todo.setCompleted(request.completed());

        Todo saved = repository.save(todo);
        log.info("Created todo with id={}", saved.getId());
        return toResponse(saved);
    }

    @Override
    public TodoResponse update(Long id, TodoRequest request) {
        Todo todo = repository.findById(id)
                .orElseThrow(() -> new TodoNotFoundException(id));

        todo.setTitle(request.title());
        todo.setDescription(request.description());
        todo.setCompleted(request.completed());

        Todo saved = repository.save(todo);
        log.info("Updated todo with id={}", saved.getId());
        return toResponse(saved);
    }

    @Override
    public void delete(Long id) {
        if (!repository.existsById(id)) {
            throw new TodoNotFoundException(id);
        }
        repository.deleteById(id);
        log.info("Deleted todo with id={}", id);
    }

    private TodoResponse toResponse(Todo todo) {
        return new TodoResponse(
                todo.getId(),
                todo.getTitle(),
                todo.getDescription(),
                todo.isCompleted(),
                todo.getCreatedAt(),
                todo.getUpdatedAt()
        );
    }
}
