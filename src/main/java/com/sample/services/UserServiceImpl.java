package com.sample.services;

import com.sample.entities.User;
import com.sample.entities.repos.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
/**
 * Сервис для выполнения операций с данными пользователя.
 *  Реализация интерфейса
 */
@Service
public class UserServiceImpl implements UserService {

    @Autowired
    UserRepository userRepository;

    @Override
    public User findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    @Override
    public void save(User user) {

        user.setPassword(user.getPassword());
        userRepository.save(user);
    }
}
