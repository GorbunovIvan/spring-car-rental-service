package org.example.entity.user;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.example.entity.HasId;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "users")
@NoArgsConstructor @AllArgsConstructor
@Getter @Setter
@EqualsAndHashCode(of = "username")
@ToString
public class User implements HasId<Long>, UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "username", unique = true)
    @NotNull(message = "username is empty")
    @Size(min = 5, max = 50, message = "username should be in range from 5 to 99 characters long")
    private String username;

    @Column(name = "password")
    @NotNull(message = "password is empty")
    @Size(min = 6, message = "password should not be less than 6 characters long")
    private String password;

    @Column(name = "name")
    @NotNull(message = "name is empty")
    @Size(min = 1, max = 256, message = "name should be in range from 1 to 256 characters long")
    private String name;

    @Enumerated(EnumType.STRING)
//    @NotNull(message = "type is empty")
    private UserType type;

    @Column(name = "created_at")
    private LocalDate createdAt;

    @ElementCollection(targetClass = Role.class)
    @CollectionTable(name = "user_role", joinColumns = @JoinColumn(name = "user_id"))
    @Enumerated(EnumType.STRING)
    @ToString.Exclude
    private Set<Role> roles = new HashSet<>();

    @Column(name = "is_active")
    private Boolean isActive;

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL)
    @ToString.Exclude
    private Lessor lessor;

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL)
    @ToString.Exclude
    private Renter renter;

    @PrePersist
    @PreUpdate
    private void init() {
        if (createdAt == null) {
            createdAt = LocalDate.now();
        }
        checkAndConfigureType();
    }

    private void checkAndConfigureType() {

        // both lessor and renter - error
        if (lessor != null && renter != null) {
            throw new RuntimeException("User (id=" + getId() + ") has references to lessor and to renter at the same time");
        }

        // type does not correspond with references
        if (type == UserType.LESSOR) {
            if (renter != null) {
                throw new RuntimeException("User (id=" + getId() + ") is lessor by its type but has reference to renter");
            }
        } else if (type == UserType.RENTER) {
            if (lessor != null) {
                throw new RuntimeException("User (id=" + getId() + ") is renter by its type but has reference to lessor");
            }
        }
        // type is null, so choose it based on reference
        else if (type == null) {
            if (lessor != null) {
                type = UserType.LESSOR;
            } else if (renter != null) {
                type = UserType.RENTER;
            }
        }

        // type is specified, so we can add reference
        if (type == UserType.LESSOR) {
            if (lessor == null) {
                setLessor(new Lessor(this));
            }
        } else if (type == UserType.RENTER) {
            if (renter == null) {
                setRenter(new Renter(this));
            }
        }
    }

    public void addRole(Role role) {
        roles.add(role);
    }

    // Security
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return getRoles();
    }

    @Override
    public boolean isAccountNonExpired() {
        return getIsActive();
    }

    @Override
    public boolean isAccountNonLocked() {
        return getIsActive();
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return getIsActive();
    }

    @Override
    public boolean isEnabled() {
        return getIsActive();
    }
}
