package com.kumar.crudapi.controllers;

import com.kumar.crudapi.service.TodoService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/todos")
@RequiredArgsConstructor
public class TodoController {
    public final Logger log = LoggerFactory.getLogger(TodoController.class);

    private final TodoService todoService;

    @GetMapping("/{id}")
    public Mono<String> getTodo(@PathVariable String id) {
        return todoService.getTodo(Integer.parseInt(id));
    }

}
