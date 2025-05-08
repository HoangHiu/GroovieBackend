package org.myapp.groovie.dto.in;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class AccountDtoIn {
    String username;
    String password;
    String email;

    public AccountDtoIn(String username, String password) {
        this.username = username;
        this.password = password;
    }
}
