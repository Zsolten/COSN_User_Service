package COSN;

import org.springframework.stereotype.Component;

import COSN.dto.RegisterResponseDTO;
import COSN.dto.UserDTO;
import COSN.entities.User;

@Component
public class UserMapper {
    public static UserDTO toDTO(User user){
        var userdto = new UserDTO();
        userdto.setId(Long.toString(user.getId()));
        userdto.setFirstName(user.getName());
        userdto.setLastName(user.getSurname());
        userdto.setEmail(user.getEmail());
        userdto.setPhoneNumber(user.getPhoneNumber());
        return userdto;
    }

    public static User toUser(UserDTO userdto){
        User user = new User();
        user.setName(userdto.getFirstName());
        user.setSurname(userdto.getLastName());
        user.setEmail(userdto.getEmail());
        user.setPhoneNumber(userdto.getPhoneNumber());
        return user;
    }

      public static RegisterResponseDTO toRegisterResponseDTO(User user) {
        RegisterResponseDTO dto = new RegisterResponseDTO();
        dto.setId(Long.toString(user.getId()));
        dto.setEmail(user.getEmail());
        dto.setStatus(user.getStatus().name());
        return dto;
    }
}