package org.example.entity.deals;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.example.entity.HasId;
import org.example.entity.user.Lessor;
import org.example.entity.user.Renter;

import java.time.LocalDateTime;

@Entity
@Table(name = "rental_records")
@NoArgsConstructor @AllArgsConstructor
@Getter @Setter
@EqualsAndHashCode(of = { "productCard" })
@ToString
public class RentalRecord implements HasId<Long> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "product_card")
    @NotNull
    private ProductCard productCard;

    @ManyToOne
    @JoinColumn(name = "renter_id")
    @NotNull
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

    public Lessor getLessor() {
        return getProductCard().getLessor();
    }

    public boolean isReturned() {
        return returnedAt != null;
    }
}
