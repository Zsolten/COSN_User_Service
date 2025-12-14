package COSN.dto;

import lombok.Data;

import java.util.List;

@Data
public class RegisterRequestDTO {
    private String email;
    private String password;
    private String firstName;
    private String lastName;
    private String phoneNumber;

}
