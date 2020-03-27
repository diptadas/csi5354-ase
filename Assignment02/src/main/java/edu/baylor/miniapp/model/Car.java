package edu.baylor.miniapp.model;

import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * Assignment 02
 *
 * @author Dipta Das
 */
@Entity
@Getter
@Setter
@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "car")
@NamedQueries({
        @NamedQuery(name = "Car.findAll", query = "SELECT c from Car c")
})
// @JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
public class Car {

    @Id
    @SequenceGenerator(
            name = "car_sequence",
            allocationSize = 1,
            initialValue = 4
    )
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "car_sequence")
    @Column(name = "id", updatable = false, nullable = false)
    private Integer id;

    @NotNull
    @Size(min = 3, max = 50)
    @Column(name = "brand", length = 50, nullable = false)
    private String brand;

    @NotNull
    @Size(min = 3, max = 50)
    @Column(name = "type", length = 50, nullable = false)
    private String type;

    @NotNull
    @Size(min = 3, max = 50)
    @Column(name = "license", length = 50, nullable = false)
    private String license;

    @ManyToOne
    @JoinColumn(name = "owner_id")
//    @JsonIdentityInfo(
//            generator = ObjectIdGenerators.PropertyGenerator.class,
//            property = "id",
//            scope = Person.class)
//    @JsonIdentityReference(alwaysAsId = true)
    private Person owner; // using unidirectional relation for simplicity

    @Version
    private Integer version = 0;
}
