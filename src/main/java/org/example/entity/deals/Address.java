package org.example.entity.deals;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.example.entity.HasId;

@Entity
@Table(name = "addresses",
        uniqueConstraints = @UniqueConstraint(columnNames = { "country", "town", "street", "building_number" }))
@NoArgsConstructor
@Getter
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

    public Address(Long id, String country, String town, String street, String buildingNumber) {
        this(country, town, street, buildingNumber);
        this.id = id;
    }

    public Address(String country, String town, String street, String buildingNumber) {
        setCountry(country);
        setTown(town);
        setStreet(street);
        setBuildingNumber(buildingNumber);
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    public void setCountry(String country) {
        this.country = country.replaceAll(" ", "-");
    }

    public void setTown(String town) {
        this.town = town.replaceAll(" ", "-");
    }

    public void setStreet(String street) {
        this.street = street.replaceAll(" ", "-");
    }

    public void setBuildingNumber(String buildingNumber) {
        this.buildingNumber = buildingNumber.replaceAll(" ", "-");
    }

    @PrePersist
    @PreUpdate
    private void init() {
        remasterAddress();
    }

    private void remasterAddress() {
        setCountry(getCountry());
        setTown(getTown());
        setStreet(getStreet());
        setBuildingNumber(getBuildingNumber());
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
