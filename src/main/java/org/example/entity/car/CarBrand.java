package org.example.entity.car;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.example.entity.HasId;

@Entity
@Table(name = "car_brands")
@Getter @Setter
@EqualsAndHashCode(of = "name")
@ToString
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

    public CarBrand(Integer id, String name) {
        this(name);
        this.id = id;
    }

    public CarBrand(String name) {
        setName(name);
    }

    public void setName(String name) {
        this.name = name.replaceAll(" ", "-");
    }

    @PrePersist
    @PreUpdate
    private void init() {
        remasterName();
    }

    private void remasterName() {
        setName(getName());
    }
}
