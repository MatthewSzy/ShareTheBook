package com.example.ShareTheBook.dto.User;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Date;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserData {

    private String token;

    private String type;

    private Long id;

    private String username;

    private List<String> roles;
}
