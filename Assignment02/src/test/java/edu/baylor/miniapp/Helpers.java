package edu.baylor.miniapp;

import edu.baylor.miniapp.model.Car;
import edu.baylor.miniapp.model.Person;

import static org.fest.assertions.Assertions.assertThat;

public class Helpers {
    public static Car newCar(Integer carId) {
        return new Car(carId, "brand", "type", "license", null, 1);
    }

    public static Car newCar(Integer carId, Integer personId) {
        return new Car(carId, "brand", "type", "license", newPerson(personId), 1);
    }

    public static Person newPerson(Integer personId) {
        return new Person(personId, "name", 20, "name@email.com", 1);
    }

    public static void assertForCars(Car found, Car expected) {
        assertThat(found).isNotNull();

        // rely on equals method instead of manually checking all fields
        assertThat(found).isEqualTo(expected);

//        assertThat(found.getId()).isEqualTo(expected.getId());
//        assertThat(found.getBrand()).isEqualTo(expected.getBrand());
//        assertThat(found.getType()).isEqualTo(expected.getType());
//        assertThat(found.getLicense()).isEqualTo(expected.getLicense());
//
//        if (expected.getOwner() == null) {
//            assertThat(found.getOwner()).isNull();
//        } else {
//            assertThat(found.getOwner()).isNotNull();
//            assertThat(found.getOwner().getId()).isEqualTo(expected.getOwner().getId());
//        }
    }

    public static void assertForPersons(Person found, Person expected) {
        assertThat(found).isNotNull();

        // rely on equals method instead of manually checking all fields
        assertThat(found).isEqualTo(expected);
    }
}
