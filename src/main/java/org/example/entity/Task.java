package org.example.entity;


import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
public class Task {

    @Id
    @GeneratedValue
    private Long id;

    @Column
    private String title;

    @Column
    private String description;

    @Column
    private long userId;


}
