package COSN.service;
import java.util.List;
import java.util.UUID;

import COSN.CarMapper;
import COSN.config.JwtTokenProvider;
import COSN.dto.*;
import COSN.entities.Car;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import COSN.UserMapper;
import COSN.entities.User;
import COSN.entities.UserRole;
import COSN.entities.UserStatus;
import COSN.repository.UserRepository;

@Service
public class UserService{
    private final UserRepository repository;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Autowired
    private AccountVerificationService verificationService;


    public UserService(UserRepository repository){
        this.repository = repository;
    }

    public LoginResponseDTO authenticateUser(LoginRequestDTO loginRequest) {
        User user = repository.findByEmail(loginRequest.getEmail())
                .orElseThrow(() -> new RuntimeException("Invalid email or password"));

        if (!passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
            throw new RuntimeException("Invalid email or password");
        }

        if (user.getStatus() == UserStatus.PENDING_VERIFICATION) {
            throw new RuntimeException("Account not verified");
        }

        if (user.getStatus() == UserStatus.BLOCKED) {
            throw new RuntimeException("Account is blocked");
        }

        String token = jwtTokenProvider.generateToken(
                user.getId().toString(),
                user.getRole().name()
        );

        return new LoginResponseDTO(token, user.getId(), user.getEmail(), user.getName());
    }

    public RegisterResponseDTO createUser(RegisterRequestDTO userData){
        if (repository.findByEmail(userData.getEmail()).isPresent()){
            throw new RuntimeException("Email is already in use");
        }
        User user = new User();
        user.setName(userData.getFirstName());
        user.setSurname(userData.getLastName());
        user.setEmail(userData.getEmail());
        user.setPassword(passwordEncoder.encode(userData.getPassword()));
        user.setPhoneNumber(userData.getPhoneNumber());
        user.setStatus(UserStatus.PENDING_VERIFICATION);
        user.setRole(UserRole.USER);
        User savedUser = repository.save(user);

        //add sending the email for account verification
        verificationService.sendVerificationEmail(savedUser);

        return UserMapper.toRegisterResponseDTO(savedUser);

    }

    public UserDTO updateUser(UUID id, UpdateUserRequestDTO dto) {
        User user = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (dto.getFirstName() != null) user.setName(dto.getFirstName());
        if (dto.getLastName() != null) user.setSurname(dto.getLastName());
        if (dto.getPhoneNumber() != null) user.setPhoneNumber(dto.getPhoneNumber());
        //what about cars update?
        return UserMapper.toDTO(repository.save(user));
    }

    public void deleteUser(UUID id) {
        if (!repository.existsById(id)) {
            throw new RuntimeException("User with id " + id + " not found");
        }
        repository.deleteById(id);
    }

    public UserDTO getUser(UUID id){
        if (!repository.existsById(id)) {
            throw new RuntimeException("User with id " + id + " not found");
        }
        var optional = repository.findById(id);
        return UserMapper.toDTO(optional.orElse(null));
    }

    public List<UserDTO> getAllUsers(){
        return repository.findAll().stream().map((k) -> UserMapper.toDTO(k)).toList();
    }

    public List<CarDTO> getCarByUserId(UUID id){
        if (!repository.existsById(id)) {
            throw new RuntimeException("User with id " + id + " not found");
        }
        var optional = repository.findById(id);
        User user = optional.get();
        List<Car> cars = user.getCars();
        return cars.stream().map((CarMapper::CarToDTO)).toList();
    }

    public CarDTO addCarToUser(UUID id, CarRequestDTO carDTO){
        if (!repository.existsById(id)) {
            throw new RuntimeException("User with id " + id + " not found");
        }
        var optional = repository.findById(id);
        if (optional.isPresent()) {
            User user = optional.get();
            Car car = CarMapper.CarReqDTOToEntity(carDTO, user);
            user.getCars().add(car);
            if(user.getStatus().equals(UserStatus.BLOCKED)){
                user.setStatus(UserStatus.ACTIVE);
            }
            repository.save(user);
            return CarMapper.CarToDTO(user.getCars().get(user.getCars().size() - 1));
        }
        else return null;
    }

    public void deleteCarByUser(UUID id, Long carId){
        if (!repository.existsById(id)) {
            throw new RuntimeException("User with id " + id + " not found");
        }
        var optional = repository.findById(id);
        if (optional.isPresent()) {
            User user = optional.get();
            Car car = user.getCars().stream()
                    .filter(c -> c.getId().equals(carId))
                    .findFirst()
                    .orElseThrow(() -> new RuntimeException("Car not found"));
            user.getCars().remove(car);
            if(user.getCars().isEmpty()){ //user has no car!
                user.setStatus(UserStatus.BLOCKED);
            }
            repository.save(user);
        }
    }
}