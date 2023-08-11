package org.example.entity.car;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

@Entity
@Table(name = "addresses")
@NoArgsConstructor @AllArgsConstructor
@Getter @Setter
@ToString
@EqualsAndHashCode
public class Address {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Exclude
    private Long id;

    @Column(name = "country")
    @NotNull
    @Size(min = 1, max = 99)
    private String country;

    @Column(name = "town")
    @NotNull
    @Size(min = 1, max = 99)
    private String town;

    @Column(name = "street")
    @NotNull
    @Size(min = 1, max = 99)
    private String street;

    @Column(name = "building_number")
    @NotNull
    @Size(min = 1, max = 99)
    private String buildingNumber;

    public String getFullAddress() {
        return String.format("%s, %s, %s, %s",
                getCountry(),
                getTown(),
                getStreet(),
                getBuildingNumber());
    }
}
