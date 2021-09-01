package ru.javaops.topjava2.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;

@Entity
@Table(name = "dish",
        uniqueConstraints = {@UniqueConstraint(columnNames = {"name", "restaurant_id", "date"}, name = "dish_unique_name_restaurant_date_idx")},
        indexes = {@Index(name = "i_restaurant_date", columnList = "restaurant_id,date")})
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@ToString(callSuper = true)
public class Dish extends NamedEntity {

    @NotNull
    @Column(name = "restaurant_id", nullable = false)
    private Integer restaurantId;

    @Column(name = "date", nullable = false)
    @NotNull
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private LocalDate date;

    @Column(name = "price")
    private double price;

    public Dish(Integer id, String name, Integer restaurantId, LocalDate date, double price) {
        super(id, name);
        this.restaurantId = restaurantId;
        this.date = date;
        this.price = price;
    }
}
