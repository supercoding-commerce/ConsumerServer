package com.github.messageconsumer.repository;

import com.github.messageconsumer.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
    User findUserByEmail(String userEmail);
}
