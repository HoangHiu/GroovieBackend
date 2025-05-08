package org.myapp.groovie.dto.out;


import lombok.*;
import lombok.experimental.FieldDefaults;
import org.myapp.groovie.entity.user.User;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserDtoOut {
    UUID uuid;
    String username;
    String email;
    PersonalDetailDtoOut personalDetailDtoOut;
    String profilePic;

    public static UserDtoOut fromUser(User user){
        return UserDtoOut.builder()
                .uuid(user.getUuid())
                .username(user.getUsername())
                .email(user.getEmail())
                .personalDetailDtoOut(PersonalDetailDtoOut.fromPersonalDetail(user.getPersonalDetail()))
                .build();
    }

    public static UserDtoOut fromUser(User user, String profilePic){
        return UserDtoOut.builder()
                .uuid(user.getUuid())
                .username(user.getUsername())
                .email(user.getEmail())
                .personalDetailDtoOut(PersonalDetailDtoOut.fromPersonalDetail(user.getPersonalDetail()))
                .profilePic(profilePic)
                .build();
    }
}
