package org.example.entity.car;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

@Entity
@Table(name = "car_brands")
@NoArgsConstructor @AllArgsConstructor
@Getter @Setter
@ToString
@EqualsAndHashCode(of = "name")
public class CarBrand {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", unique = true)
    @NotNull
    @Size(min = 1, max = 256)
    private String name;
}
