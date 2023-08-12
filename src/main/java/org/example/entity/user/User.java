package org.example.entity.user;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.example.entity.HasId;

@Entity
@Table(name = "users")
@NoArgsConstructor @AllArgsConstructor
@Getter @Setter
@ToString
@EqualsAndHashCode(of = "username")
public class User implements HasId<Long> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "username", unique = true)
    @NotNull
    @Size(min = 5, max = 50, message = "username should be in range from 5 to 99 characters long")
    private String username;

    @Column(name = "name")
    @NotNull(message = "name is empty")
    @Size(min = 1, max = 256, message = "name is wrong")
    private String name;

    @Enumerated(EnumType.STRING)
    private UserType type;

    @OneToOne(mappedBy = "user")
    private Lessor lessor;

    @OneToOne(mappedBy = "user")
    private Renter renter;

    public Partaker<?> getPartaker() {

        if (id == null && type == null) {
            if (lessor != null) {
                type = UserType.LESSOR;
            } else if (renter != null) {
                type = UserType.RENTER;
            }
        }

        if (type == UserType.LESSOR) {
            return getLessor();
        } else if (type == UserType.RENTER) {
            return getRenter();
        }

        return null;
    }

    @PrePersist
    private void init() {
        if (lessor != null && renter != null) {
            throw new RuntimeException("User (id=" + getId() + ") has references to lessor and to renter at the same time");
        }
        if (type == UserType.LESSOR) {
            if (renter != null) {
                throw new RuntimeException("User (id=" + getId() + ") is lessor by its type but has reference to renter");
            }
        } else if (type == UserType.RENTER) {
            if (lessor != null) {
                throw new RuntimeException("User (id=" + getId() + ") is renter by its type but has reference to lessor");
            }
        } else if (type == null) {
            if (lessor != null) {
                type = UserType.LESSOR;
            } else if (renter != null) {
                type = UserType.RENTER;
            }
        }
    }
}
