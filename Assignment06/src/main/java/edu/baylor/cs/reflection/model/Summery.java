package edu.baylor.cs.reflection.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Summery {
    private int total;
    private int controllers;
    private int entities;
    private int repositories;
    private int services;
    private int others;
}
