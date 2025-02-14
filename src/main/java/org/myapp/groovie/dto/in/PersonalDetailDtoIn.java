package org.myapp.groovie.dto.in;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.Column;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import lombok.*;
import org.myapp.groovie.entity.user.User;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PersonalDetailDtoIn {
    String name;
    String email;
    Long dateOfBirth;
    String gender;
    String phoneNumber;
    String bio;
    String location;
}
