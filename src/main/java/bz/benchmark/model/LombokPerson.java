package bz.benchmark.model;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@Getter
@AllArgsConstructor
@EqualsAndHashCode
public class LombokPerson {
    private final String firstName;
    private final String lastName;
    private final int age;
    private final String email;
    private final String city;
}
