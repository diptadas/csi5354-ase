package edu.baylor.miniapp.model;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * Midterm 01
 *
 * @author Dipta Das
 */
@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
// @EqualsAndHashCode // bidirectional stack overflow, needs to add manually
@Table(name = "person")
@NamedQueries({
        @NamedQuery(name = "Person.findAll", query = "SELECT p from Person p")
})
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
public class Person {

    @Id
    @SequenceGenerator(
            name = "person_sequence",
            allocationSize = 1,
            initialValue = 100
    )
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "person_sequence")
    @Column(name = "id", updatable = false, nullable = false)
    private Integer id;

    @NotNull
    @Size(min = 3, max = 50)
    @Column(name = "name", length = 50, nullable = false)
    private String name;

    @NotNull
    @Column(name = "dob", nullable = false)
    private String dob;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "team_id_leader")
    private Team leaderOf;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "team_id_member")
    private Team memberOf;

    @Version
    private Integer version = 0;
}
