package ru.javaops.topjava2.to;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.Value;
import ru.javaops.topjava2.model.Dish;
import ru.javaops.topjava2.util.validation.NoHtml;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serial;
import java.time.LocalDate;
import java.util.List;

@Schema(description = "RestaurantTo object")
@Value
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class RestaurantTo extends NamedTo {
    @Serial
    private static final long serialVersionUID = 1L;

    @Schema(description = "Address", example = "New City, Clean str., 22")
    @Size(min = 2, max = 100)
    @NoHtml
    private String address;

    @Schema(description = "Phone", example = "8(000)-333-22-11")
    @Size(min = 2, max = 100)
    @NoHtml
    private String phone;

    @Schema(description = "Last date of vote for this restaurant")
    @NotNull
    private LocalDate lastVoteDate;

    @Schema(description = "List of dishes")
    @NotNull
    List<Dish> dishes;

    public RestaurantTo(Integer id, String name, String address, String phone, LocalDate lastVoteDate, List<Dish> dishes) {
        super(id, name);
        this.address = address;
        this.phone = phone;
        this.lastVoteDate = lastVoteDate;
        this.dishes = dishes;
    }
}
