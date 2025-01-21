package org.myapp.groovie.repository;

import org.myapp.groovie.entity.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {
    User getUserByUsername(String username);
    User getUserByUsernameAndPassword(String username, String password);
}
