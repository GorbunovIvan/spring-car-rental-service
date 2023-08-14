package org.example.entity.deals;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.example.entity.HasId;

@Entity
@Table(name = "addresses",
        uniqueConstraints = @UniqueConstraint(columnNames = { "country", "town", "street", "building_number" }))
@NoArgsConstructor @AllArgsConstructor
@Getter @Setter
@EqualsAndHashCode
public class Address implements HasId<Long> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Exclude
    private Long id;

    @Column(name = "country")
    @NotNull(message = "country is empty")
    @Size(min = 1, max = 99, message = "country is wrong")
    private String country;

    @Column(name = "town")
    @NotNull(message = "town is empty")
    @Size(min = 1, max = 99, message = "town is wrong")
    private String town;

    @Column(name = "street")
    @NotNull(message = "street is empty")
    @Size(min = 1, max = 99, message = "street is wrong")
    private String street;

    @Column(name = "building_number")
    @NotNull(message = "building number is empty")
    @Size(min = 1, max = 99, message = "building number is wrong")
    private String buildingNumber;

    public Address(String country, String town, String street, String buildingNumber) {
        this.country = country;
        this.town = town;
        this.street = street;
        this.buildingNumber = buildingNumber;
    }

    @PrePersist
    @PreUpdate
    private void init() {
        setCountry(getCountry().replaceAll(" ", "-"));
        setTown(getTown().replaceAll(" ", "-"));
        setStreet(getStreet().replaceAll(" ", "-"));
    }

    @Override
    public String toString() {
        return String.format("%s, %s, %s, %s",
                getCountry(),
                getTown(),
                getStreet(),
                getBuildingNumber());
    }
}
