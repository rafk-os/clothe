package com.example.backend.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.Set;

@Entity
@Getter
@Setter
@ToString
@RequiredArgsConstructor
@AllArgsConstructor
@Builder
public class Item {

    @Id
    @Schema(example = "1", description = "Id of a item", accessMode = Schema.AccessMode.READ_ONLY)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", updatable = false)
    private Integer id;


    @NotEmpty(message = "Please provide a name")
    @Schema(example= "Blue jeans", description = "Name of item")
    @Column(name = "name", nullable = false, unique = true)
    private String name;

    @NotEmpty(message = "Please provide a type of item")
    @Schema(example= "Pants", description = "Type of item")
    @Column(name = "type", nullable = false)
    private String type;

    @NotEmpty(message = "Please provide a description")
    @Schema(example= "Wide comfy pants", description = "Description of item")
    @Column(name = "description")
    private String description;

    @NotNull(message = "Please provide a price")
    @Schema(example= "15.00", description = "Price of item")
    @Column(name = "price")
    private Float price;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    @JsonIgnore
    @ToString.Exclude
    private User user;

    @Schema(description = "Shopping cart in which items could be stored", accessMode = Schema.AccessMode.READ_ONLY)
    @ManyToMany(mappedBy = "items")
    @JsonIgnore
    @ToString.Exclude
    private Set<Cart> carts;

    @Schema(description = "Orders in which item could be stored", accessMode = Schema.AccessMode.READ_ONLY)
    @ManyToMany(mappedBy = "items")
    @JsonIgnore
    @ToString.Exclude
    private Set<Orders> orders;
}
