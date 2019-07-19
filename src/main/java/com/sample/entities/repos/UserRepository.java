package com.sample.entities.repos;

import com.sample.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Репозиторий для сущности User.
 * Интерфейс взаимодействия с БД.
 */
public interface UserRepository extends JpaRepository<User, String> {
    User findByUsername(String username);
}
