package com.spring.bloggerclone.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Entity
@Table(name = "users")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class User
{
    private static final int MIN_USERNAME_LENGTH = 3;
    private static final int MAX_USERNAME_LENGTH = 16;
    private static final int MIN_PASSWORD_LENGTH = 6;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "username", unique = true)
    @Length(min = MIN_USERNAME_LENGTH, message = "Username must be at least " + MIN_USERNAME_LENGTH + " characters long",
            max = MAX_USERNAME_LENGTH)
    @NotEmpty
    private String username;

    @Column(name = "email", unique = true)
    @NotEmpty
    @Email
    private String email;

    @NotEmpty
    @Column(name = "password")
    @Length(min = MIN_PASSWORD_LENGTH, message = "Password must be at least " + MIN_PASSWORD_LENGTH + " characters long")
    private String password;

    @OneToMany(mappedBy = "user")
    private Map<Long,Post> posts;

    @OneToMany(mappedBy = "user")
    @JsonIgnore
    private Map<Long,Comment> comments;

    @Column(name = "user_create_time")
    private LocalDateTime userCreateTime;

}
