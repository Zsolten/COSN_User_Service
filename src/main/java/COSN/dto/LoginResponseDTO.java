package COSN.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.UUID;

@Data
@AllArgsConstructor
public class LoginResponseDTO {
    private String token;
    private UUID userId;
    private String email;
    private String firstName;
    private String lastName;
}
