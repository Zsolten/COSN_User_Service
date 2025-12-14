package COSN.dto;

import lombok.Data;

@Data
public class CarDTO {
    private String carId;
    private String make;
    private String model;
    private Integer year;
    private String licensePlate;
    private String color;
    private String ownerId;    
}
