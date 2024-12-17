package org.myapp.groovie.entity.user;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Set;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name="groups")
public class Group {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "uuid")
    UUID uuid;

    @Column(name = "name")
    String name;

    @Column(name = "description")
    String description;

    @Column(name = "slug", length = 15)
    @Enumerated(EnumType.STRING)
    Role role;

    @ManyToMany(mappedBy = "groups", fetch = FetchType.LAZY)
    @JsonBackReference
    Set<User> users;
}
