package edu.baylor.miniapp.model;

import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.Email;
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
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
@Table(name = "person")
@NamedQueries({
        @NamedQuery(name = "Person.findAll", query = "SELECT p from Person p"),
        @NamedQuery(name = "Person.findCars", query = "SELECT c from Car c WHERE c.owner.id = :id")
})
// @JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
public class Person {

    @Id
    @SequenceGenerator(
            name = "person_sequence",
            allocationSize = 1,
            initialValue = 3
    )
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "person_sequence")
    @Column(name = "id", updatable = false, nullable = false)
    private Integer id;

    @NotNull
    @Size(min = 3, max = 50)
    @Column(name = "name", length = 50, nullable = false)
    private String name;

    @NotNull
    @Column(name = "age", nullable = false)
    private int age;

    @NotNull
    @Email
    @Column(name = "email", length = 50, nullable = false)
    private String email;

//    @OneToMany(fetch = FetchType.EAGER, mappedBy = "owner")
//    private Set<Car> children = new HashSet<>(); // using unidirectional relation for simplicity

    @Version
    private Integer version = 0;
}
