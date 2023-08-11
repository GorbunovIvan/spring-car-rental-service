package org.example.entity.car;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "models")
@NoArgsConstructor @AllArgsConstructor
@Getter @Setter
@ToString
@EqualsAndHashCode(of = { "carBrand", "modelName", "year", "horsePowers" })
public class Model {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "car_brand_id")
    private CarBrand carBrand;

    @Column(name = "model_name")
    private String modelName;

    @Column(name = "year")
    private Integer year;

    @Column(name = "horse_powers")
    private Integer horsePowers;

    @OneToMany(mappedBy = "model")
    @ToString.Exclude
    private List<Car> cars = new ArrayList<>();

    public String getFullName() {
        return String.format("%s %s, %d", getCarBrand(), getModelName(), getYear());
    }
}
