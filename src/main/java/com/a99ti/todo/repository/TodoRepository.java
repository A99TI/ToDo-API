package com.a99ti.todo.repository;

import com.a99ti.todo.entity.Todo;
import com.a99ti.todo.entity.User;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TodoRepository extends CrudRepository<Todo, Long> {
    List<Todo> findByOwner(User owner);
}
