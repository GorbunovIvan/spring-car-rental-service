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
@Getter @Setter
@EqualsAndHashCode(of = { "carBrand", "modelName", "year", "horsePowers" })
@ToString
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

    public Model(Long id, CarBrand carBrand, String modelName, Integer year, Integer horsePowers, List<Car> cars) {
        this(id, carBrand, modelName, year, horsePowers);
        this.cars = cars;
    }

    public Model(Long id, CarBrand carBrand, String modelName, Integer year, Integer horsePowers) {
        this(carBrand, modelName, year, horsePowers);
        this.id = id;
    }

    public Model(CarBrand carBrand, String modelName, Integer year, Integer horsePowers) {
        setCarBrand(carBrand);
        setModelName(modelName);
        setYear(year);
        setHorsePowers(horsePowers);
    }

    public void setModelName(String modelName) {
        this.modelName = modelName.replaceAll(" ", "-");
    }

    public String getFullName() {
        return getFullNamePattern(getCarBrand().getName(), getModelName(), getYear());
    }

    public static String getFullNamePattern(String carBrand, String modelName, Integer year) {
        return String.format("%s %s, %d", carBrand, modelName, year);
    }

    @PrePersist
    private void init() {
        remasterModel();
    }

    private void remasterModel() {
        setModelName(getModelName());
    }
}
