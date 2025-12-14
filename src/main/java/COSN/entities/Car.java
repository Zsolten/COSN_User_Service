package COSN.entities;
import jakarta.persistence.*;
import lombok.*;


@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
@Table(name = "CAR")
public class Car {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column
    private Long id;
    @Column(unique=true)
    @NonNull
    private String license;
    @Column
    private String brand;
    @Column
    private String model;
    @Column
    private Integer year;
    @Column
    private String color;
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User owner;




}
