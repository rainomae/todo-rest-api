package ee.rainer.todo.service;

import ee.rainer.todo.dto.TodoRequest;
import ee.rainer.todo.dto.TodoResponse;

import java.util.List;

public interface TodoService {

    List<TodoResponse> findAll();

    TodoResponse findById(Long id);

    TodoResponse create(TodoRequest request);

    TodoResponse update(Long id, TodoRequest request);

    void delete(Long id);
}
