package com.example.ShareTheBook.security.DetailsService;

import com.example.ShareTheBook.entity.UserEntity;
import com.example.ShareTheBook.repository.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserDetailsServiceImplement implements UserDetailsService {
    @Autowired
    UserRepo userRepo;

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserEntity userEntity = userRepo.findUserByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Nie można znaleźć użytkownika o podanej nazwie!"));

        return UserDetailsImplement.build(userEntity);
    }

}
