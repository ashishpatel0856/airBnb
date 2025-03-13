package com.ashish.projects.VrboApp.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;


import javax.management.relation.Role;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;



@Entity
@Getter
@Setter
@Table(name = "app_user")
public class User implements UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String email;
    @Column(nullable = false)
    private String password;
    private String name;


    @ElementCollection(fetch = FetchType.EAGER)
    @Enumerated(EnumType.STRING)
    private Set<Role> roles;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return roles.stream()
                .map(role ->new SimpleGrantedAuthority("ROLE_"+role.getRoleName()))
                .collect(Collectors.toList());
    }

    @Override
    public String getUsername() {
        return "";
    }



//    @OneToMany(mappedBy ="user" )
//    private Set<Guest> guests;


}
