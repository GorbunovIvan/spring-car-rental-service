package org.example.entity.car;

import jakarta.persistence.*;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.example.entity.HasId;

import java.util.Objects;

@Entity
@Table(name = "images")
@NoArgsConstructor @AllArgsConstructor @Builder
@Getter @Setter
@EqualsAndHashCode(of = { "car", "name", "originalFileName" })
@ToString
public class Image implements HasId<Long> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name")
    @NotNull(message = "name is empty")
    @NotEmpty(message = "name is empty")
    private String name;

    @Column(name = "original_file_name")
    @NotNull(message = "original-file-name is empty")
    @NotEmpty(message = "original-file-name is empty")
    private String originalFileName;

    @Column(name = "content_type")
    @NotNull(message = "content-type is empty")
    @NotEmpty(message = "content-type is empty")
    private String contentType;

    @Column(name = "size")
    @NotNull(message = "size is empty")
    @Digits(integer = 15, fraction = 0, message = "size must be integer number")
    private Long size;

    @Lob
    @Column(columnDefinition = "bytea")
    @NotNull(message = "image is empty")
    @ToString.Exclude
    private byte[] bytes;

    @ManyToOne(cascade = CascadeType.REFRESH)
    @JoinColumn(name = "car_id")
    @NotNull(message = "car is empty")
    private Car car;

    @PrePersist
    private void init() {
        if (Objects.requireNonNullElse(name, "").isBlank()) {
            name = getOriginalFileName();
        }
    }
}
