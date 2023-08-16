package org.example.entity.car;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.example.entity.HasId;

@Entity
@Table(name = "car_brands")
@AllArgsConstructor
@Getter @Setter
@ToString
@EqualsAndHashCode(of = "name")
public class CarBrand implements HasId<Integer> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "name", unique = true)
    @NotNull(message = "name is empty")
    @Size(min = 1, max = 256)
    private String name;

    public CarBrand() {
        setName("");
    }

    @PrePersist
    @PreUpdate
    private void init() {
        setName(getName().replaceAll(" ", "-"));
    }
}
