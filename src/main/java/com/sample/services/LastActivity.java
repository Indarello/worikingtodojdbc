package com.sample.services;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Collection;
import java.util.HashSet;

public class LastActivity
{
    private String username;

    private Long lastactivity;

    public LastActivity(String username, Long time)
    {
        this.username = username;
        this.lastactivity = time;
    }

    public void setUsername(String username)
    {
        this.username = username;
    }

    public String getUsername()
    {
        return username;
    }

    public void setactivity(Long activity) {this.lastactivity = activity; }

    public Long getactivity() { return lastactivity; }
}
