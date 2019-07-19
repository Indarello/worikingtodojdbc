package com.sample.services;

import com.sample.entities.User;

/**
 * Сервис для выполнения операций с данными пользователя.
 * Интерфейс
 */
public interface UserService {

    User findByUsername(String username);
    void save(User user);
}
