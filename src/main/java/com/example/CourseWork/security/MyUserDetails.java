package com.example.CourseWork.security;

import com.example.CourseWork.entity.Role;
import com.example.CourseWork.entity.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

public class MyUserDetails implements UserDetails
{
    private User user;

    public MyUserDetails(User user) {
        this.user = user;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        Set<Role> roles =user.getRoles();
        List<SimpleGrantedAuthority> authority =new ArrayList<>();

        for(Role role: roles){
            authority.add(new SimpleGrantedAuthority(role.getName()));
        }

        return authority;
    }

    @Override
    public String getPassword() {
        return user.getPassword();
    }

    @Override
    public String getUsername() {
        return user.getEmail();
    }


    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return user.getEnabled();
    }

//    public String getFullname(){
//        return this.user.getLastName() +" "+ this.user.getFirstName()+ " " + this.user.getPatronymic();
//    }
}
