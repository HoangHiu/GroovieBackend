package org.myapp.groovie.dto.out;

import jakarta.persistence.Column;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.myapp.groovie.entity.user.PersonalDetail;

import java.sql.Timestamp;

@Getter
@Setter
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PersonalDetailDtoOut {
    String name;
    String email;
    Timestamp dateOfBirth;
    String gender;
    String phoneNumber;
    String bio;
    String location;

    public static PersonalDetailDtoOut fromPersonalDetail(PersonalDetail personalDetail){
        return PersonalDetailDtoOut.builder()
                .name(personalDetail.getName())
                .email(personalDetail.getEmail())
                .dateOfBirth(personalDetail.getDateOfBirth())
                .gender(personalDetail.getGender())
                .phoneNumber(personalDetail.getPhoneNumber())
                .bio(personalDetail.getBio())
                .location(personalDetail.getLocation())
                .build();
    }
}
