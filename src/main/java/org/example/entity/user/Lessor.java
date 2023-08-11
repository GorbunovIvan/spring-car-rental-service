package org.example.entity.user;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.example.entity.ProductCard;
import org.example.entity.car.Car;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "lessors")
@NoArgsConstructor @AllArgsConstructor
@Getter @Setter
@ToString
@EqualsAndHashCode(of = "user")
public class Lessor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @NotNull
    private User user;

    @OneToMany(mappedBy = "lessor")
    @ToString.Exclude
    private List<Car> cars = new ArrayList<>();

    public List<ProductCard> getProductCards() {
        return cars.stream()
                .map(Car::getProductCards)
                .flatMap(List::stream)
                .toList();
    }
}
