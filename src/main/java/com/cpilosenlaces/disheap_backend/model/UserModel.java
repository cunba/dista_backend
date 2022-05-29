package com.cpilosenlaces.disheap_backend.model;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.OneToMany;

import com.fasterxml.jackson.annotation.JsonBackReference;

import org.hibernate.annotations.GenericGenerator;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import io.swagger.v3.oas.annotations.Parameter;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity(name = "users")
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public class UserModel {
    @Id
    @GenericGenerator(name = "uuid2", strategy = "uuid2")
    @GeneratedValue(strategy = GenerationType.IDENTITY, generator = "uuid2")
    @Column(length = 36, nullable = false, updatable = false)
    private UUID id;
    @Column
    private String name;
    @Column
    private String surname;
    @Column
    private LocalDate birthday;
    @Column(name = "is_disorder")
    private Boolean isDisorder;
    @Column
    private String email;
    @Column
    private String password;
    @Column(name = "register_date")
    private LocalDateTime registerDate;
    @Column
    private String role;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "user")
    @JsonBackReference(value = "user-disbeacs")
    @Parameter(hidden = true)
    private List<Disbeac> disbeacs;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "user")
    @JsonBackReference(value = "user-disbands")
    @Parameter(hidden = true)
    private List<Disband> disbands;

    @Bean
    public static PasswordEncoder encoder() {
        return new BCryptPasswordEncoder();
    }
}
