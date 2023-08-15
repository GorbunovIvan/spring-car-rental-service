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

@Entity
@Table(name = "product_cards")
@AllArgsConstructor
@Getter @Setter
@EqualsAndHashCode(of = { "car", "createdAt"} )
@ToString
public class ProductCard implements HasId<Long> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "car")
    @NotNull
    private Car car;

    @Column(name = "price")
    @NotNull(message = "price is empty")
    @Digits(integer = 10, fraction = 2, message = "price is wrong")
    private BigDecimal price;

    @ManyToOne(cascade = { CascadeType.PERSIST, CascadeType.MERGE })
    @JoinColumn(name = "address_id")
    @NotNull(message = "address is empty")
    private Address address;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @OneToOne(mappedBy = "productCard")
    @ToString.Exclude
    private RentalRecord rentalRecord;

    public ProductCard() {
        setCar(new Car());
    }

    @PrePersist
    private void init() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
    }

    public Lessor getLessor() {
        return getCar().getLessor();
    }

    public String getModelName() {
        return getCar().getFullName();
    }

    public boolean isLeased() {
        return getRentalRecord() != null;
    }

    public String getFullName() {
        return String.format("Product-card: %s for %s at %s",
                getCar().getModel().getFullName(),
                getPrice(),
                getAddress());

    }
}
