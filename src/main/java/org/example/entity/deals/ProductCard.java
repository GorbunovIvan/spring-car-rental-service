package org.example.entity.deals;

import jakarta.persistence.*;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.example.entity.HasId;
import org.example.entity.car.Car;
import org.example.entity.user.Lessor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Entity
@Table(name = "product_cards")
@NoArgsConstructor @AllArgsConstructor
@Getter @Setter
@ToString
@EqualsAndHashCode(of = { "car", "createdAt"} )
public class ProductCard implements HasId<Long> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "car")
    @NotNull
    private Car car;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "price")
    @NotNull
    @Digits(integer = 10, fraction = 2)
    private BigDecimal price;

    @OneToMany(mappedBy = "productCard")
    @OrderBy("rentedAt")
    private List<RentalRecord> rentalRecords = new ArrayList<>();

    @PrePersist
    private void init() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
    }

    public Lessor getLessor() {
        return getCar().getLessor();
    }

    public RentalRecord getLastRentalRecord() {
        return rentalRecords.stream()
                .max(Comparator.comparing(RentalRecord::getRentedAt))
                .orElse(null);
    }

    public boolean isInLeasing() {
        var rentalRecord = getLastRentalRecord();
        if (rentalRecord == null) {
            return false;
        }
        return rentalRecord.isInLeasing();
    }
}
