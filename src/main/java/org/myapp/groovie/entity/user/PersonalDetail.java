package org.myapp.groovie.entity.user;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "personal_details")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PersonalDetail {
    @Id
    @Column(name = "uuid")
    UUID uuid;

    @Column(name = "name")
    String name;

    @Column(name = "email")
    String email;

    @Column(name = "date_of_birth")
    LocalDate dateOfBirth;

    @Column(name = "gender")
    String gender;

    @Column(name = "phone_number")
    String phoneNumber;

    @Column(name = "bio")
    String bio;

    @Column(name = "location")
    String location;

    @OneToOne(mappedBy = "personalDetail")
    @JsonBackReference
    User user;

    @Column(name = "created_at")
    Timestamp createdAt;

    @Column(name = "updated_at")
    Timestamp updatedAt;
}
