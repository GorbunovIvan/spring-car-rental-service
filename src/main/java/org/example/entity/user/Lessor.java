package org.example.entity.user;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.example.entity.HasId;
import org.example.entity.car.Car;
import org.example.entity.deals.ProductCard;
import org.example.entity.deals.RentalRecord;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "lessors")
@NoArgsConstructor @AllArgsConstructor
@Getter @Setter
@EqualsAndHashCode(of = "user")
@ToString
public class Lessor implements HasId<Long> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "user_id", unique = true)
    @NotNull
    private User user;

    @OneToMany(mappedBy = "lessor")
    @OrderBy("id")
    @ToString.Exclude
    private List<Car> cars = new ArrayList<>();

    public Lessor(User user) {
        this.user = user;
    }

    public String getName() {
        return getUser().getName();
    }

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

    public List<Car> getCarsInUsage() {
        return getCars().stream()
                .filter(Car::isInUsage)
                .toList();
    }
}
