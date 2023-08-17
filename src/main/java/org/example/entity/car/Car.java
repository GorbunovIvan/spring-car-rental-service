package org.example.entity.car;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.example.entity.HasId;
import org.example.entity.deals.ProductCard;
import org.example.entity.deals.RentalRecord;
import org.example.entity.user.Lessor;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "cars",
        uniqueConstraints = @UniqueConstraint(columnNames = { "model_id", "lessor_id" }))
@AllArgsConstructor
@Getter @Setter
@EqualsAndHashCode(of = { "model", "lessor" })
@ToString
public class Car implements HasId<Long> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(cascade = { CascadeType.PERSIST, CascadeType.MERGE })
    @JoinColumn(name = "model_id")
    @NotNull(message = "model is empty")
    private Model model;

    @ManyToOne
    @JoinColumn(name = "lessor_id")
    @NotNull(message = "lessor is null")
    private Lessor lessor;

    @OneToMany(mappedBy = "car", cascade = CascadeType.ALL)
    @ToString.Exclude
    private List<Image> images = new ArrayList<>();

    @OneToMany(mappedBy = "car")
    @OrderBy("createdAt")
    @ToString.Exclude
    private List<ProductCard> productCards = new ArrayList<>();

    public Car() {
        setModel(new Model());
    }

    public List<RentalRecord> getRentalRecords() {
        return productCards.stream()
                .map(ProductCard::getRentalRecord)
                .filter(Objects::nonNull)
                .toList();
    }

    public RentalRecord getLastRentalRecord() {
        return getRentalRecords().stream()
                .max(Comparator.comparing(RentalRecord::getRentedAt))
                .orElse(null);
    }

    public boolean isInUsage() {
        var rentalRecord = getLastRentalRecord();
        if (rentalRecord == null) {
            return false;
        }
        return !rentalRecord.isReturned();
    }

    public String getFullName() {
        // TEMPORARILY !!!
        if (getLessor() == null) {
            return getModel().getFullName() + " by null";
        }
        return getModel().getFullName() + " by " + getLessor().getName();
    }
}
