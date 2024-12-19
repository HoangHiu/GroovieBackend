package org.myapp.groovie.entity.user;

import org.springframework.security.core.GrantedAuthority;

public enum Role {
    REGULAR,
    ARTIST,
    MODERATOR,
    ADMIN;
}
