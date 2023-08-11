package org.example.entity.user;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.example.entity.deals.ProductCard;
import org.example.entity.deals.RentalRecord;
import org.example.entity.car.Car;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "renters")
@NoArgsConstructor @AllArgsConstructor
@Getter @Setter
@ToString
@EqualsAndHashCode(of = "user")
public class Renter implements Partaker<Renter> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @NotNull
    private User user;

    @OneToMany(mappedBy = "renter")
    @OrderBy("rentedAt")
    private List<RentalRecord> rentalRecords = new ArrayList<>();

    public List<Car> getCarsInUsage() {
        return rentalRecords.stream()
                .filter(RentalRecord::isInLeasing)
                .map(RentalRecord::getProductCard)
                .map(ProductCard::getCar)
                .toList();
    }

    @Override
    public Renter get() {
        return this;
    }
}
