package org.example.entity.car;

import jakarta.persistence.*;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.example.entity.HasId;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "models")
@AllArgsConstructor
@Getter @Setter
@ToString
@EqualsAndHashCode(of = { "carBrand", "modelName", "year", "horsePowers" })
public class Model implements HasId<Long> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "car_brand_id")
    private CarBrand carBrand;

    @Column(name = "model_name")
    @Size(min = 1)
    private String modelName;

    @Column(name = "year")
    @Digits(integer = 4, fraction = 0)
    private Integer year;

    @Column(name = "horse_powers")
    @Digits(integer = 5, fraction = 0)
    private Integer horsePowers;

    @OneToMany(mappedBy = "model")
    @ToString.Exclude
    private List<Car> cars = new ArrayList<>();

    public Model() {
        setCarBrand(new CarBrand());
    }

    public String getFullName() {
        return getFullNamePattern(getCarBrand().getName(), getModelName(), getYear());
    }

    public static String getFullNamePattern(String carBrand, String modelName, Integer year) {
        return String.format("%s %s, %d", carBrand, modelName, year);
    }

    @PrePersist
    private void init() {
        setModelName(getModelName().replaceAll(" ", "-"));
    }
}
