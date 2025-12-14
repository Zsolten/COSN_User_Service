package COSN.dto;

import lombok.Data;

@Data
public class UpdateUserRequestDTO {
    private String firstName;
    private String lastName;
    private String phoneNumber;
}
