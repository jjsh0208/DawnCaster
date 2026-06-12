package com.github.jjsh0208.dawncasterbackend.domain.users.service;

import com.github.jjsh0208.dawncasterbackend.domain.users.entity.User;
import com.github.jjsh0208.dawncasterbackend.domain.users.repository.UsersRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final UsersRepository usersRepository;

    @Transactional
    public User findOrCreateUser(String email) {
        return usersRepository.findByEmail(email)
                .orElseGet(() -> usersRepository.save(new User(email)));
    }
}
