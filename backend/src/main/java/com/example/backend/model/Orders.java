package com.example.backend.model;
import com.fasterxml.jackson.annotation.JsonBackReference;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import java.util.Set;

@Entity
@Getter
@Setter
@ToString
@RequiredArgsConstructor
@AllArgsConstructor
@Builder
public class Orders {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(example = "1", description = "Id of a order", accessMode = Schema.AccessMode.READ_ONLY)
    @Column(name = "id", updatable = false)
    private Integer id;


    @NotEmpty(message = "Please provide a description")
    @Schema( example = "Prosze dostarczyc jak najszybciej", description = "Description of order")
    @Column(name = "description")
    private String description;

    @NotEmpty(message = "Please provide a adress")
    @Schema( example = "Lukowa 35", description = "Adress of delivery")
    @Column(name = "adress")
    private String adress;

    @NotEmpty(message = "Please provide a postal code")
    @Schema( example = "23-330", description = "Postal code of delivery")
    @Column(name = "postal_code")
    private String postalCode;

    @NotEmpty(message = "Please provide a city of delivery")
    @Schema( example = "Lubon", description = "city of delivery")
    @Column(name = "city")
    private String city;

    @ManyToOne(fetch = FetchType.LAZY)
    @Schema( accessMode = Schema.AccessMode.READ_ONLY )
    @JoinColumn(name = "user_id", nullable = false)
    @JsonBackReference
    @ToString.Exclude
    private User user;

    @ManyToMany(cascade = { CascadeType.MERGE, CascadeType.PERSIST }, fetch = FetchType.EAGER)
    @JoinTable(
            name = "items_in_order",
            joinColumns = @JoinColumn(name = "order_id"),
            inverseJoinColumns = @JoinColumn(name = "item_id"))
    private Set<Item> items;

    @Enumerated(EnumType.ORDINAL)
    @Schema(accessMode = Schema.AccessMode.READ_ONLY )
    private Status status;
}
