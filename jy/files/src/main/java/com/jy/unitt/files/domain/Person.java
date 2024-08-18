package com.jy.unitt.files.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Person {

    private String name;

    public static void main(String[] args) {
        System.out.println(Person.builder().name("hello-name").build());
    }
}
