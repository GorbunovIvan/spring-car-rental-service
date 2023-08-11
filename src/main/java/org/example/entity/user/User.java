package org.example.entity.user;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

@Entity
@Table(name = "users")
@NoArgsConstructor @AllArgsConstructor
@Getter @Setter
@ToString
@EqualsAndHashCode(of = "username")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "username", unique = true)
    @NotNull
    @Size(min = 5, max = 50)
    private String username;

    @Column(name = "name")
    @NotNull
    @Size(min = 1, max = 256)
    private String name;

    @Enumerated(EnumType.STRING)
    private UserType type;
}
