package org.example.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.example.entity.car.Car;
import org.example.entity.user.Lessor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "product_cards")
@NoArgsConstructor @AllArgsConstructor
@Getter @Setter
@ToString
@EqualsAndHashCode(of = { "car", "createdAt"} )
public class ProductCard {

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

    @PrePersist
    private void init() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
    }

    public Lessor getLessor() {
        return getCar().getLessor();
    }
}
