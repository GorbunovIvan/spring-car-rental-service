package org.example.entity.user;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.example.entity.deals.ProductCard;
import org.example.entity.car.Car;
import org.example.entity.deals.RentalRecord;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "lessors")
@NoArgsConstructor @AllArgsConstructor
@Getter @Setter
@ToString
@EqualsAndHashCode(of = "user")
public class Lessor implements Partaker<Lessor> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @NotNull
    private User user;

    @OneToMany(mappedBy = "lessor")
    @OrderBy("id")
    @ToString.Exclude
    private List<Car> cars = new ArrayList<>();

    public List<ProductCard> getProductCards() {
        return getCars().stream()
                .map(Car::getProductCards)
                .flatMap(List::stream)
                .toList();
    }

    public List<RentalRecord> getRentalRecords() {
        return getCars().stream()
                .map(Car::getRentalRecords)
                .flatMap(List::stream)
                .toList();
    }

    @Override
    public Lessor get() {
        return null;
    }
}
