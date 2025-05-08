package org.myapp.groovie.repository;

import org.myapp.groovie.entity.song.Song;
import org.myapp.groovie.entity.user.PersonalDetail;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface PersonalDetailRepository extends JpaRepository<PersonalDetail, UUID>, JpaSpecificationExecutor<PersonalDetail> {
    @Query(
            value = """
    SELECT DISTINCT ON (pd.uuid) pd.*
    FROM personal_details pd
    JOIN users u ON pd.uuid = u.personal_detail_id
    JOIN user_group ug ON u.uuid = ug.user_uuid
    JOIN groups g ON ug.group_uuid = g.uuid
    WHERE (:displayName IS NULL OR LOWER(TRIM(pd.name)) LIKE CONCAT('%', LOWER(TRIM(:displayName)), '%'))
      AND (:role IS NULL OR g.slug = :role)
    """,
            countQuery = """
    SELECT COUNT(DISTINCT pd.uuid)
    FROM personal_details pd
    JOIN users u ON pd.uuid = u.personal_detail_id
    JOIN user_group ug ON u.uuid = ug.user_uuid
    JOIN groups g ON ug.group_uuid = g.uuid
    WHERE (:displayName IS NULL OR LOWER(TRIM(pd.name)) LIKE CONCAT('%', LOWER(TRIM(:displayName)), '%'))
      AND (:role IS NULL OR g.slug = :role)
    """,
            nativeQuery = true
    )
    Page<PersonalDetail> findByDisplayNameAndRole(
            @Param("displayName") String displayName,
            @Param("role") String role,
            Pageable pageable
    );
}
