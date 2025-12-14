package COSN;

import COSN.dto.CarDTO;
import COSN.dto.CarRequestDTO;
import COSN.entities.Car;
import COSN.entities.User;

import java.util.List;
import java.util.stream.Collectors;

public class CarMapper {

    public static Car CarReqDTOToEntity(CarRequestDTO dto, User owner) {
        Car car = new Car();
        car.setModel(dto.getModel());
        car.setBrand(dto.getMake());
        car.setColor(dto.getColor());
        car.setYear(dto.getYear());
        car.setLicense(dto.getLicensePlate());
        car.setOwner(owner);
        return car;
    }

    public static CarDTO CarToDTO(Car car) {
        CarDTO dto = new CarDTO();
        dto.setModel(car.getModel());
        dto.setMake(car.getBrand());
        dto.setColor(car.getColor());
        dto.setYear(car.getYear());
        dto.setLicensePlate(car.getLicense());
        dto.setCarId(String.valueOf(car.getId()));
        dto.setOwnerId(String.valueOf(car.getOwner().getId()));
        return dto;
    }
}
