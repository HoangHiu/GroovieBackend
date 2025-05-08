package org.myapp.groovie.entity.user;


import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;
import org.myapp.groovie.entity.album.Album;
import org.myapp.groovie.entity.playlist.Playlist;

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
@ToString
public class User {
    @Id
    @Column(name = "uuid")
    UUID uuid;

    @Column(name = "username")
    String username;

    @Column(name = "password")
    String password;

    @Column(name = "email")
    String email;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "user_group",
            joinColumns = @JoinColumn(name = "user_uuid"),
            inverseJoinColumns = @JoinColumn(name = "group_uuid"))
    @JsonManagedReference
    Set<Group> groups;

    @OneToOne
    @JoinColumn(name = "personal_detail_id", referencedColumnName = "uuid")
    @JsonManagedReference
    PersonalDetail personalDetail;

    @OneToMany(mappedBy = "user")
    @JsonManagedReference
    Set<Album> albums;

    @OneToMany(mappedBy = "user")
    @JsonManagedReference
    Set<Playlist> playlists;

    public void addToGroups(Set<Group> groups){
        groups.stream().map(g -> g.getUsers().add(this));
        if (this.groups != null){
            this.groups.addAll(groups);
        }else{
            this.groups = new HashSet<>();
            this.groups.addAll(groups);
        }
    }
}
