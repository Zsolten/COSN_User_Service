package COSN.service;

import COSN.entities.User;
import COSN.entities.UserStatus;
import COSN.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class AccountVerificationService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JavaMailSender mailSender;

    @Value("${app.base-url}")
    private String baseUrl;

    @Value("${spring.mail.username}")
    private String fromEmail;

    public void sendVerificationEmail(User user) {
        String token = UUID.randomUUID().toString();
        user.setVerificationToken(token);
        user.setTokenExpiryDate(LocalDateTime.now().plusHours(24));
        userRepository.save(user);

        String verificationLink = baseUrl + "/api/verify?token=" + token;

        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("tfaculdadeoverleaf@gmail.com");
        message.setTo(user.getEmail());
        message.setSubject("Activate your account");
        message.setText("Click on the link to activate your account: " + verificationLink);

        mailSender.send(message);
    }

    public boolean verifyAccount(String token) {
        User user = userRepository.findByVerificationToken(token)
                .orElseThrow(() -> new RuntimeException("Invalid token"));

        if (user.getTokenExpiryDate().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("Expired token");
        }

        user.setStatus(UserStatus.ACTIVE);
        user.setVerificationToken(null);
        user.setTokenExpiryDate(null);
        userRepository.save(user);

        return true;
    }
}
