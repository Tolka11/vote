package ru.javaops.topjava2.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;

@Schema(description = "Dish of menu")
@Entity
@Table(name = "dish",
        uniqueConstraints = {@UniqueConstraint(columnNames = {"name", "restaurant_id", "date"}, name = "dish_unique_name_restaurant_date_idx")},
        indexes = {@Index(name = "i_restaurant_date", columnList = "restaurant_id,date")})
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@ToString(callSuper = true)
public class Dish extends NamedEntity {

    @Schema(description = "Restaurant ID", example = "1")
    @NotNull
    @Column(name = "restaurant_id", nullable = false)
    private Integer restaurantId;


    @Schema(description = "Date")
    @Column(name = "date", nullable = false)
    @NotNull
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private LocalDate date;


    @Schema(description = "Price", example = "3.24")
    @Column(name = "price")
    private double price;

    public Dish(Integer id, String name, Integer restaurantId, LocalDate date, double price) {
        super(id, name);
        this.restaurantId = restaurantId;
        this.date = date;
        this.price = price;
    }
}
