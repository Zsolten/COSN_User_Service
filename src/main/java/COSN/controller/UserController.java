package COSN.controller;
import java.util.List;
import java.util.UUID;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import COSN.dto.*;
import COSN.service.UserService;

@RestController
@RequestMapping("/api")
public class UserController {
    private final UserService userService;


    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/register")
    public RegisterResponseDTO createUser(@Valid @RequestBody RegisterRequestDTO req) {
        return userService.createUser(req);
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> login(@RequestBody LoginRequestDTO loginRequest) {
        LoginResponseDTO response = userService.authenticateUser(loginRequest);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/verify")
    public ResponseEntity<String> verifyAccount(@RequestParam String token) {
        return ResponseEntity.ok("Account successfully verified!");
    }


    @PreAuthorize("(hasRole('ADMIN') or #userId ==  T(java.util.UUID).fromString(authentication.name))")
    @DeleteMapping("/{userId}")
    public ResponseEntity<Void> deleteUser(@PathVariable UUID userId) {
        userService.deleteUser(userId);
        return ResponseEntity.noContent().build();
    }

    @PreAuthorize("(hasRole('ADMIN') or #userId ==  T(java.util.UUID).fromString(authentication.name))")
    @PatchMapping("/user/{userId}")
    public UserDTO updateUser(@RequestBody UpdateUserRequestDTO req, @PathVariable UUID userId) {
        return userService.updateUser(userId, req);
    }

    @GetMapping("/user/{id}")
    public UserDTO getUserById(@PathVariable UUID id){
        return userService.getUser(id);
    }
    @GetMapping("/users")
    public List<UserDTO> getAllUsers() {
        return userService.getAllUsers();
    }

    @GetMapping("/user/{id}/car")
    public List<CarDTO> getCarByUserId(@PathVariable UUID id) {
        return userService.getCarByUserId(id);
    }

    @PreAuthorize("(hasRole('ADMIN') or #id ==  T(java.util.UUID).fromString(authentication.name))")
    @PutMapping("/user/{id}/car")
    public CarDTO addCar(@PathVariable UUID id, @RequestBody CarRequestDTO car) {
        return userService.addCarToUser(id, car);
    }

    @PreAuthorize("(hasRole('ADMIN') or #id ==  T(java.util.UUID).fromString(authentication.name))")
    @DeleteMapping("/user/{id}/{carId}")
    public void deleteCar(@PathVariable UUID id, @PathVariable Long carId) {
        userService.deleteCarByUser(id, carId);
    }

}