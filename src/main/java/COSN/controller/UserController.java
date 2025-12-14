package COSN.controller;
import java.util.List;
import java.util.Map;

import COSN.config.JwtTokenProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import COSN.dto.*;
import COSN.service.UserService;

@RestController
@RequestMapping("/api")
public class UserController {
    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/register")
    public RegisterResponseDTO createUser(@RequestBody RegisterRequestDTO req) {
        return userService.createUser(req);
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> login(@RequestBody LoginRequestDTO loginRequest) {
        LoginResponseDTO response = userService.authenticateUser(loginRequest);
        return ResponseEntity.ok(response);
    }


    @PostMapping("/validate")
    public ResponseEntity<?> validateToken(@RequestHeader("Authorization") String authHeader) {
        try {
            String token = authHeader.replace("Bearer ", "");

            if (!jwtTokenProvider.validateToken(token)) {
                return ResponseEntity.status(401).body(Map.of("valid", false, "error", "Invalid token"));
            }

            Long userId = jwtTokenProvider.getUserIdFromToken(token);
            String role = jwtTokenProvider.getRoleFromToken(token);

            return ResponseEntity.ok(Map.of(
                    "valid", true,
                    "userId", userId,
                    "role", role
            ));
        } catch (Exception e) {
            return ResponseEntity.status(401).body(Map.of("valid", false, "error", e.getMessage()));
        }
    }


    @PreAuthorize("hasRole('ADMIN') or #userId == authentication.principal")
    @DeleteMapping("/{userId}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long userId) {
        userService.deleteUser(userId);
        return ResponseEntity.noContent().build();
    }


    @PatchMapping("/user/{id}")
    public UserDTO updateUser(@RequestBody UpdateUserRequestDTO req, @PathVariable Long id) {
        return userService.updateUser(id, req);
    }

    @GetMapping("/user/{id}")
    public UserDTO getUserById(@PathVariable Long id){
        return userService.getUser(id);
    }
    @GetMapping("/users")
    public List<UserDTO> getAllUsers() {
        return userService.getAllUsers();
    }

    @GetMapping("/user/{id}/car")
    public List<CarDTO> getCarByUserId(@PathVariable Long id) {
        return userService.getCarByUserId(id);
    }

    @PutMapping("/user/{id}/car")
    public CarDTO addCar(@PathVariable Long id, @RequestBody CarRequestDTO car) {
        return userService.addCarToUser(id, car);
    }

    @DeleteMapping("/user/{id}/{carId}")
    public void deleteCar(@PathVariable Long id, @PathVariable Long carId) {
        userService.deleteCarByUser(id, carId);
    }

}