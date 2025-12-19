package org.example.cruddemo;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Data
@AllArgsConstructor
class Student {
    String name;
    int score;
}

public class StudentSocreTest {
    public static void main(String[] args) {
        List<Student> students = Arrays.asList(
                new Student("Alice", 80),
                new Student("Bob", 90),
                new Student("Umr", 90)
        );
        List<Student> result = students.stream()
            .sorted(
                Comparator.comparingInt(Student::getScore).reversed()
                .thenComparing(Student::getName)
            )
            .toList();
        for (Student student : result) {
            System.out.println(student);
        }
    }
}
