package edu.baylor.miniapp.model;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIdentityReference;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.HashSet;
import java.util.Set;

/**
 * Midterm 01
 *
 * @author Dipta Das
 */
@Entity
@Getter
@Setter
// @EqualsAndHashCode // bidirectional stack overflow, needs to add manually
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "team")
@NamedQueries({
        @NamedQuery(name = "Team.findAll", query = "SELECT c from Team c")
})
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
public class Team {

    @Id
    @SequenceGenerator(
            name = "team_sequence",
            allocationSize = 1,
            initialValue = 100
    )
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "team_sequence")
    @Column(name = "id", updatable = false, nullable = false)
    private Integer id;

    @NotNull
    @Size(min = 3, max = 50)
    @Column(name = "name", length = 50, nullable = false)
    private String name;

    @NotNull
    @Size(min = 3, max = 50)
    @Column(name = "skill", length = 50, nullable = false)
    private String skill;

    @OneToOne(mappedBy = "leaderOf", fetch = FetchType.EAGER)
    @JsonIdentityInfo(
            generator = ObjectIdGenerators.PropertyGenerator.class,
            property = "id",
            scope = Person.class)
    @JsonIdentityReference(alwaysAsId = true)
    private Person leader;

    @OneToMany(mappedBy = "memberOf", fetch = FetchType.EAGER)
    @JsonIdentityInfo(
            generator = ObjectIdGenerators.PropertyGenerator.class,
            property = "id",
            scope = Person.class)
    @JsonIdentityReference(alwaysAsId = true)
    private Set<Person> members = new HashSet<>();

    @ManyToMany(fetch = FetchType.EAGER)
    @JsonIdentityInfo(
            generator = ObjectIdGenerators.PropertyGenerator.class,
            property = "id",
            scope = Team.class)
    @JsonIdentityReference(alwaysAsId = true)
    private Set<Team> preferredTeams = new HashSet<>();

    @Version
    private Integer version = 0;
}
