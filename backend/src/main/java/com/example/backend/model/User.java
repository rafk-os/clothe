package com.example.backend.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import org.hibernate.Hibernate;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import java.util.Objects;
import java.util.Set;


@Entity
@Getter
@Setter
@ToString
@RequiredArgsConstructor
@AllArgsConstructor
@Builder
public class User {
    @Id
    @Schema(example = "1", description = "Id of a user", accessMode = Schema.AccessMode.READ_ONLY)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, updatable = false)
    private Integer id;

    @NotEmpty(message = "Please provide a username")
    @Column(name = "username", nullable = false, unique = true)
    @Schema(example = "Martin")
    private String username;

    @NotEmpty(message = "Please provide a first name")
    @Column(name = "first_name", nullable = false)
    @Schema(example = "Johny")
    private String firstName;

    @NotEmpty(message = "Please provide a last name")
    @Column(name = "last_name", nullable = false)
    @Schema(example = "Bravo")
    private String lastName;

    @NotEmpty(message = "Please provide an email")
    @Column(name = "email", nullable = false, unique = true)
    @Schema(example = "martin@gmail.com")
    private String email;

    @NotEmpty(message = "Please provide a password")
    @Column(name = "password", nullable = false)
    @Schema(example = "rekin", description = "User password", accessMode = Schema.AccessMode.WRITE_ONLY)
    private String password;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    @Schema( description = "Items added by user", accessMode = Schema.AccessMode.READ_ONLY)
    @ToString.Exclude
    private Set<Item> items;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    @Schema( description = "Orders of user", accessMode = Schema.AccessMode.READ_ONLY)
    @ToString.Exclude
    @JsonManagedReference
    private Set<Orders> orders;

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL)
    @Schema( description = "Cart of user", accessMode = Schema.AccessMode.READ_ONLY)
    @JsonIgnore
    @ToString.Exclude
    private Cart cart;

    @ManyToMany(cascade = { CascadeType.MERGE, CascadeType.PERSIST }, fetch = FetchType.EAGER)
    @Schema(description = "Roles of user", accessMode = Schema.AccessMode.READ_ONLY)
    @JoinTable(
            name = "user_roles",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id"))
    private Set<Role> roles;


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        User user = (User) o;
        return id != null && Objects.equals(id, user.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}

