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
    PersonalDetailDtoOut personalDetailDtoOut;

    public static UserDtoOut fromUser(User user){
        return UserDtoOut.builder()
                .uuid(user.getUuid())
                .username(user.getUsername())
                .personalDetailDtoOut(PersonalDetailDtoOut.fromPersonalDetail(user.getPersonalDetail()))
                .build();
    }
}
