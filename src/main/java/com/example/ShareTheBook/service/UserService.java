package com.example.ShareTheBook.service;

import com.example.ShareTheBook.dto.StringInfo;
import com.example.ShareTheBook.dto.User.*;
import com.example.ShareTheBook.entity.RoleEntity;
import com.example.ShareTheBook.entity.Roles;
import com.example.ShareTheBook.entity.UserEntity;
import com.example.ShareTheBook.error.*;
import com.example.ShareTheBook.repository.RoleRepo;
import com.example.ShareTheBook.repository.UserRepo;
import com.example.ShareTheBook.security.DetailsService.UserDetailsImplement;
import com.example.ShareTheBook.security.TokenService.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.management.relation.Role;
import java.io.IOException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
public class UserService {

    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    UserRepo userRepo;

    @Autowired
    RoleRepo roleRepo;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    JwtUtils jwtUtils;

    public UserData loginUser(LoginData loginData) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginData.getUsername(), loginData.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);

        String newToken = jwtUtils.generateJwtToken(authentication);

        UserDetailsImplement userDetailsImplement = (UserDetailsImplement) authentication.getPrincipal();
        List<String> roles = userDetailsImplement.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());

        return UserData.builder()
                .token(newToken)
                .type("Bearer")
                .id(userDetailsImplement.getId())
                .username(userDetailsImplement.getUsername())
                .roles(roles)
                .build();
    }

    public StringInfo registrationUser(RegistrationData registrationData) {

        if(userRepo.existsByUsername(registrationData.getUsername())) {
            throw new UserExistsException("Nazwa użytkownika jest już zajęta!");
        }

        Pattern pattern = Pattern.compile("^[a-zA-Z0-9._-]{3,20}$");
        Matcher matcher = pattern.matcher(registrationData.getUsername());
        if (!matcher.matches()) {
            throw new PatternErrorException("Nazwa użytkownika jest nie poprawna!");
        }

        pattern = Pattern.compile("^(?=.*?[A-Z])(?=.*?[a-z])(?=.*?[0-9])(?=.*?[#?!@$%^&*-]).{8,}$");
        matcher = pattern.matcher(registrationData.getPassword());
        if (!matcher.matches()) {
            throw new PatternErrorException("Hasło jest nie poprawnę!");
        }

        Set<RoleEntity> newRolesSet = new HashSet<>();

        if (registrationData.getRoles() == null) newRolesSet.add(roleRepo.findRoleByName(Roles.ROLE_USER).get());
        else {
            registrationData.getRoles().forEach(role -> {
                if ("admin".equals(role)) newRolesSet.add(roleRepo.findRoleByName(Roles.ROLE_ADMIN).get());
                else newRolesSet.add(roleRepo.findRoleByName(Roles.ROLE_USER).get());
            });
        }

        UserEntity userEntity = UserEntity.builder()
                .username(registrationData.getUsername())
                .password(passwordEncoder.encode(registrationData.getPassword()))
                .image(null)
                .roles(newRolesSet)
                .build();

        userRepo.save(userEntity);
        return new StringInfo("Udało się zarejestrować nowego użytkownika!");
    }

    public UserData updateUser(Long id, NewUsername newUsername) {

        if(userRepo.existsByUsername(newUsername.getUsername())) {
            throw new UserExistsException("Nazwa użytkownika jest już zajęta!");
        }

        Pattern pattern = Pattern.compile("^[a-zA-Z0-9._-]{3,20}$");
        Matcher matcher = pattern.matcher(newUsername.getUsername());
        if (!matcher.matches()) {
            throw new PatternErrorException("Nazwa użytkownika jest nie poprawna!");
        }

        Optional<UserEntity> userEntity = userRepo.findUserById(id);
        if(userEntity.isEmpty()) {
            throw new UserNotExistsException("Podany użytkownik nie istnieje!");
        }

        userEntity.get().setUsername(newUsername.getUsername());
        userRepo.save(userEntity.get());

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(newUsername.getUsername(), newUsername.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);

        String newToken = jwtUtils.generateJwtToken(authentication);

        UserDetailsImplement userDetailsImplement = (UserDetailsImplement) authentication.getPrincipal();
        List<String> roles = userDetailsImplement.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());

        return UserData.builder()
                .token(newToken)
                .type("Bearer")
                .id(userDetailsImplement.getId())
                .username(userDetailsImplement.getUsername())
                .roles(roles)
                .build();
    }

    public StringInfo uploadProfileImage(Long id, MultipartFile multipartFile) {

        Optional<UserEntity> userEntity = userRepo.findUserById(id);
        if(userEntity.isEmpty()) {
            throw new UserNotExistsException("Podany użytkownik nie istnieje!");
        }

        if(multipartFile.isEmpty()) {
            throw new ProfileImageErrorException("Brak zdjęcia do zapisania!");
        }

        byte[] bytes = null;
        try { bytes = multipartFile.getBytes(); }
        catch(IOException e) {}

        userEntity.get().setImage(bytes);
        userRepo.save(userEntity.get());

        return new StringInfo("Zdjęcie zostało dodane!");
    }

    public ProfileImage getProfileImage(Long id) {

        Optional<UserEntity> userEntity = userRepo.findUserById(id);
        if(userEntity.isEmpty()) {
            throw new UserNotExistsException("Podany użytkownik nie istnieje!");
        }

        byte[] bytes = userEntity.get().getImage();
        return ProfileImage.builder()
                .profileImage(bytes)
                .build();
    }

    public List<UsersListData> getUsersList() {

        List<UserEntity> usersEntity = userRepo.findAll();
        if (usersEntity.isEmpty()) {
            throw new UserNotExistsException("Lista jest pusta!");
        }

        List<UsersListData> usersListData = new ArrayList<>();
        for(UserEntity userEntity: usersEntity) {
            List<String> roles = new ArrayList<>();
            for(RoleEntity roleEntity : userEntity.getRoles()) roles.add(roleEntity.getName().toString());

            usersListData.add(UsersListData.builder()
                    .id(userEntity.getId())
                    .username(userEntity.getUsername())
                    .roles(roles)
                    .build());
        }

        return usersListData;
    }

    public StringInfo changeUserRole(Long id, RoleType roleType) {

        Optional<UserEntity> userEntity = userRepo.findUserById(id);
        if(userEntity.isEmpty()) {
            throw new UserNotExistsException("Podany użytkownik nie istnieje!");
        }

        Set<RoleEntity> newRolesSet = new HashSet<>();
        if(roleType.getType().equals("ADD")) {
            newRolesSet.add(roleRepo.findRoleByName(Roles.ROLE_ADMIN).get());
            newRolesSet.add(roleRepo.findRoleByName(Roles.ROLE_USER).get());
        }
        else {
            newRolesSet.add(roleRepo.findRoleByName(Roles.ROLE_USER).get());
        }

        userEntity.get().setRoles(newRolesSet);
        userRepo.save(userEntity.get());
        return new StringInfo("Role zostały zmienione!");
    }
}
