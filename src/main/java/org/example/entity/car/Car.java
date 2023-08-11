package org.example.entity.car;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.example.entity.HasId;
import org.example.entity.deals.ProductCard;
import org.example.entity.deals.RentalRecord;
import org.example.entity.user.Lessor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "cars",
        uniqueConstraints = @UniqueConstraint(columnNames = { "model_id", "lessor_id" }))
@NoArgsConstructor @AllArgsConstructor
@Getter @Setter
@ToString
@EqualsAndHashCode(of = { "model", "lessor" })
public class Car implements HasId<Long> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(cascade = { CascadeType.PERSIST, CascadeType.MERGE })
    @JoinColumn(name = "model_id")
    @NotNull
    private Model model;

    @ManyToOne
    @JoinColumn(name = "lessor_id")
    private Lessor lessor;

    @OneToMany(mappedBy = "car")
    @OrderBy("createdAt")
    @ToString.Exclude
    private List<ProductCard> productCards = new ArrayList<>();

    public List<RentalRecord> getRentalRecords() {
        return productCards.stream()
                .map(ProductCard::getRentalRecords)
                .flatMap(List::stream)
                .toList();
    }
}
