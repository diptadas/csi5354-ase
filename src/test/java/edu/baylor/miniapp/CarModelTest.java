package edu.baylor.miniapp;

import edu.baylor.miniapp.model.Car;
import edu.baylor.miniapp.model.Person;
import org.fest.assertions.Assertions;
import org.junit.Test;

import static edu.baylor.miniapp.Helpers.newCar;
import static edu.baylor.miniapp.Helpers.newPerson;
import static org.fest.assertions.Assertions.assertThat;

/**
 * Assignment 02
 *
 * @author Dipta Das
 */
public class CarModelTest {

    @Test
    public void carsAreEqual() {
        Car car1 = newCar(101);
        Car car2 = newCar(101);

        Assertions.assertThat(car1).isEqualTo(car2);
        assertThat(car1.equals(car2)).isTrue();
        assertThat(car1.hashCode()).isEqualTo(car2.hashCode());
    }

    @Test
    public void carsAreNotEqual() {
        Car car1 = newCar(101);
        Car car2 = newCar(102);

        Assertions.assertThat(car1).isNotEqualTo(car2);
        assertThat(car1.equals(car2)).isFalse();
        assertThat(car1.hashCode()).isNotEqualTo(car2.hashCode());
    }

    @Test
    public void carModification() {
        Car car1 = newCar(101);
        Car car2 = newCar(101);

        Assertions.assertThat(car1).isEqualTo(car2);
        assertThat(car1.equals(car2)).isTrue();
        assertThat(car1.hashCode()).isEqualTo(car2.hashCode());

        car1.setBrand("newBrand");

        Assertions.assertThat(car1).isNotEqualTo(car2);
        assertThat(car1.equals(car2)).isFalse();
        assertThat(car1.hashCode()).isNotEqualTo(car2.hashCode());
    }

    @Test
    public void carsWithIdenticalOwnerAreEqual() {
        Person owner = newPerson(1);

        Car car1 = newCar(101);
        Car car2 = newCar(101);

        car1.setOwner(owner);
        car2.setOwner(owner);

        Assertions.assertThat(car1).isEqualTo(car2);
        assertThat(car1.equals(car2)).isTrue();
        assertThat(car1.hashCode()).isEqualTo(car2.hashCode());
    }

    @Test
    public void carsWithDifferentOwnerNotEqual() {
        Person owner1 = newPerson(1);
        Person owner2 = newPerson(2);

        Car car1 = newCar(101);
        Car car2 = newCar(101);

        car1.setOwner(owner1);
        car2.setOwner(owner2);

        Assertions.assertThat(car1).isNotEqualTo(car2);
        assertThat(car1.equals(car2)).isFalse();
        assertThat(car1.hashCode()).isNotEqualTo(car2.hashCode());
    }
}
