package edu.baylor.miniapp;

import edu.baylor.miniapp.model.Person;
import org.fest.assertions.Assertions;
import org.junit.Test;

import java.time.LocalDateTime;

import static edu.baylor.miniapp.Helpers.newPerson;
import static org.fest.assertions.Assertions.assertThat;

/**
 * Assignment 02
 *
 * @author Dipta Das
 */
public class PersonModelTest {

    @Test
    public void personsAreEqual() {
        Person person1 = newPerson(101);
        Person person2 = newPerson(101);

        Assertions.assertThat(person1).isEqualTo(person2);
        assertThat(person1.equals(person2)).isTrue();
        assertThat(person1.hashCode()).isEqualTo(person2.hashCode());
    }

    @Test
    public void personsAreNotEqual() {
        LocalDateTime now = LocalDateTime.now();
        Person person1 = newPerson(101);
        Person person2 = newPerson(102);

        Assertions.assertThat(person1).isNotEqualTo(person2);
        assertThat(person1.equals(person2)).isFalse();
        assertThat(person1.hashCode()).isNotEqualTo(person2.hashCode());
    }

    @Test
    public void personModification() {
        LocalDateTime now = LocalDateTime.now();
        Person person1 = newPerson(101);
        Person person2 = newPerson(101);

        Assertions.assertThat(person1).isEqualTo(person2);
        assertThat(person1.equals(person2)).isTrue();
        assertThat(person1.hashCode()).isEqualTo(person2.hashCode());

        person1.setName("newName");

        Assertions.assertThat(person1).isNotEqualTo(person2);
        assertThat(person1.equals(person2)).isFalse();
        assertThat(person1.hashCode()).isNotEqualTo(person2.hashCode());
    }
}
