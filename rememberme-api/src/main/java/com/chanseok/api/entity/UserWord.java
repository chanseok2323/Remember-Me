package com.chanseok.api.entity;

import jakarta.persistence.*;

@Entity
public class UserWord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "word_id", referencedColumnName = "id")
    private Word word;
    private Character status;
    private String description;

}
