package org.myapp.groovie.dto.in;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Set;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class UserDtoIn extends AccountDtoIn{
    Set<String> groupIds;

    public UserDtoIn(String username, String password, Set<String> groupIds){
        super(username, password);
        this.groupIds = groupIds;
    }
}
