package edu.baylor.cs.reflection.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ClassStat {
    String name;
    String packageName;
    int methods;
    int fields;
}
