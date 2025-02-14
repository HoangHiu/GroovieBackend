package org.myapp.groovie.repository;

import org.myapp.groovie.entity.user.PersonalDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface PersonalDetailRepository extends JpaRepository<PersonalDetail, UUID> {
}
