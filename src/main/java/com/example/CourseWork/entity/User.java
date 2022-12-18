package com.example.CourseWork.entity;

import jakarta.persistence.*;
import lombok.*;
import nonapi.io.github.classgraph.json.Id;

import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class User {
    @jakarta.persistence.Id
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name ="firstName", length = 45,nullable = false)
    private String firstName;
    @Column(name ="lastName", length = 45,nullable = false)
    private String lastName;
    @Column(name ="patronymic", length = 45,nullable = false)
    private String patronymic;
    @Column(length = 64,nullable = false)
    private String password;
    @Column(length = 128,nullable = false,unique = true)
    private String email;

    private Boolean enabled;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "user_roles",
            joinColumns=@JoinColumn(name="user_id"),
            inverseJoinColumns = @JoinColumn(name="role_id")
    )

    private Set<Role> roles= new HashSet<>();

    public User(String firstName, String lastName, String patronymic, String password, String email) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.patronymic = patronymic;
        this.password = password;
        this.email = email;
    }

    public void addRole(Role role){
        this.roles.add(role);
    }

}
