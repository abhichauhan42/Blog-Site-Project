package com.abhi.BloggingSite_Backend.Services;

import com.abhi.BloggingSite_Backend.Model.UserPrinciple;
import com.abhi.BloggingSite_Backend.Model.Users;
import com.abhi.BloggingSite_Backend.Repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class MyUserDetailsService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Users user = userRepository.findByUsername(username);

        if (user == null) {
            throw new UsernameNotFoundException("User not found with username: " + username);
        }

        // Return the UserPrinciple (which implements UserDetails)
        return new UserPrinciple(user);
    }
}
