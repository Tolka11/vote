package ru.javaops.topjava2.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDate;

@Schema(description = "Voting position for a certain date")
@Entity
@Table(name = "vote",
        uniqueConstraints = {@UniqueConstraint(columnNames = {"restaurant_id", "date"}, name = "vote_unique_restaurant_date_idx")},
        indexes = {@Index(name = "i_date", columnList = "date")})
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@ToString(callSuper = true)
public class Vote extends NamedEntity {

    // Field "name" is used for name of restaurant

    @Schema(description = "Menu", example = "Soup - 2.11; Cutlet - 3.54; Tea - 0.24; ")
    @NotBlank
    @Size(min = 2, max = 1000)
    @Column(name = "menu", nullable = false)
    private String menu;

    @Schema(description = "Restaurant ID", example = "1")
    @NotNull
    @Column(name = "restaurant_id", nullable = false)
    private Integer restaurantId;

    @Schema(description = "Date")
    @Column(name = "date", nullable = false)
    @NotNull
    @JsonProperty(access = JsonProperty.Access.READ_WRITE)
    private LocalDate date;

    public Vote(Integer id, String name, String menu, Integer restaurantId, LocalDate date) {
        super(id, name);
        this.menu = menu;
        this.restaurantId = restaurantId;
        this.date = date;
    }
}
