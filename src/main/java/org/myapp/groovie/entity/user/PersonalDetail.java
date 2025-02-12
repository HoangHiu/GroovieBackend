package org.myapp.groovie.entity.user;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Table(name = "personal_details")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PersonalDetail {
    @Id
    @Column(name = "uuid")
    UUID uuid;

    @OneToOne(mappedBy = "personalDetail")
    User user;
}
