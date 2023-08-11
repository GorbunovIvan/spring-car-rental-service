package org.example.entity;

import jakarta.persistence.*;
import lombok.*;
import org.example.entity.user.Lessor;
import org.example.entity.user.Renter;

import java.time.LocalDateTime;

@Entity
@Table(name = "rental_records")
@NoArgsConstructor @AllArgsConstructor
@Getter @Setter
@ToString
@EqualsAndHashCode(of = { "productCard" })
public class RentalRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "product_card")
    private ProductCard productCard;

    @ManyToOne
    @JoinColumn(name = "renter_id")
    private Renter renter;

    @Column(name = "rented_at")
    private LocalDateTime rentedAt;

    @Column(name = "returned_at")
    private LocalDateTime returnedAt;

    @PrePersist
    private void init() {
        if (rentedAt == null) {
            rentedAt = LocalDateTime.now();
        }
    }

    public boolean isInLeasing() {
        return returnedAt == null;
    }

    public Lessor getLessor() {
        return getProductCard().getLessor();
    }
}
