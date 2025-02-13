package org.myapp.groovie.entity.user;


import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "users")
public class User {
    @Id
    @Column(name = "uuid")
    UUID uuid;

    @Column(name = "username")
    String username;

    @Column(name = "password")
    String password;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "user_group",
            joinColumns = @JoinColumn(name = "user_uuid"),
            inverseJoinColumns = @JoinColumn(name = "group_uuid"))
    @JsonManagedReference
    Set<Group> groups;

    @OneToOne
    @JoinColumn(name = "personal_detail_id", referencedColumnName = "uuid")
    PersonalDetail personalDetail;

    public void addToGroup(Group group){
        group.getUsers().add(this);
        if (this.groups != null){
            this.groups.add(group);
        }else{
            this.groups = new HashSet<>();
            this.groups.add(group);
        }
    }
}
