package edu.baylor.cs.reflection.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Details {
    Component controllers = new Component();
    Component entities = new Component();
    Component repositories = new Component();
    Component services = new Component();
    Component others = new Component();
}
