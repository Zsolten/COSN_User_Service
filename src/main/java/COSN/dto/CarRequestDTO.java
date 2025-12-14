package COSN.dto;

import lombok.Data;

@Data
public class CarRequestDTO {
    private String make;
    private String model;
    private Integer year;
    private String licensePlate;
    private String color;
}
