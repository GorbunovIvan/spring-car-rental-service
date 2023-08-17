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
@Table(name = "renters")
@NoArgsConstructor @AllArgsConstructor
@Getter @Setter
@EqualsAndHashCode(of = "user")
@ToString
public class Renter implements HasId<Long> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "user_id", unique = true)
    @NotNull
    private User user;

    @OneToMany(mappedBy = "renter")
    @OrderBy("rentedAt")
    @ToString.Exclude
    private List<RentalRecord> rentalRecords = new ArrayList<>();

    public Renter(User user) {
        this.user = user;
    }

    public List<Car> getCarsInUsage() {
        return rentalRecords.stream()
                .filter(r -> !r.isReturned())
                .map(RentalRecord::getProductCard)
                .map(ProductCard::getCar)
                .toList();
    }

    public String getName() {
        return getUser().getName();
    }
}
