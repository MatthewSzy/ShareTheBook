package com.example.ShareTheBook.controller;

import com.example.ShareTheBook.dto.StringInfo;
import com.example.ShareTheBook.dto.User.*;
import com.example.ShareTheBook.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@CrossOrigin(origins = "*", maxAge = 3600)
@RequestMapping("/users")
public class UserControl {

    private final UserService userService;

    public UserControl(UserService userService) { this.userService = userService; }

    @PostMapping("/login")
    public ResponseEntity<UserData> loginUser(@RequestBody LoginData loginData) {

        UserData userData = userService.loginUser(loginData);
        return ResponseEntity.ok(userData);
    }

    @PostMapping("/registration")
    public ResponseEntity<StringInfo> registrationUser(@RequestBody RegistrationData registrationData) {

        StringInfo stringInfo = userService.registrationUser(registrationData);
        return ResponseEntity.ok(stringInfo);
    }

    @PutMapping("/update/{id}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<UserData> updateUser(@PathVariable(name = "id") Long id, @RequestBody NewUsername newUsername) {

        UserData userData = userService.updateUser(id, newUsername);
        return ResponseEntity.ok(userData);
    }

    @PutMapping("/upload/{id}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<StringInfo> uploadProfileImage(@PathVariable(name = "id") Long id, @RequestParam("image") MultipartFile multiPartFile) {

        StringInfo stringInfo = userService.uploadProfileImage(id, multiPartFile);
        return ResponseEntity.ok(stringInfo);
    }

    @GetMapping("/profileImage/{id}")
    @PreAuthorize(("hasRole('USER')"))
    public ResponseEntity<ProfileImage> getProfileImage(@PathVariable(name = "id") Long id) {

        ProfileImage profileImage = userService.getProfileImage(id);
        return ResponseEntity.ok(profileImage);
    }

    @GetMapping("/usersList")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<UsersListData>> getUsersList() {

        List<UsersListData> usersListData = userService.getUsersList();
        return ResponseEntity.ok(usersListData);
    }

    @PutMapping("/changeRole/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<StringInfo> changeUserRole(@PathVariable(name = "id") Long id, @RequestBody RoleType roleType) {

        StringInfo stringInfo = userService.changeUserRole(id, roleType);
        return ResponseEntity.ok(stringInfo);
    }
}
