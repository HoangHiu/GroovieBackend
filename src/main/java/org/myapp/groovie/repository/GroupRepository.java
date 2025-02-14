package org.myapp.groovie.repository;

import org.myapp.groovie.entity.user.Group;
import org.myapp.groovie.entity.user.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Set;
import java.util.UUID;

@Repository
public interface GroupRepository extends JpaRepository<Group, UUID> {
    Group findByRole(Role role);
}
