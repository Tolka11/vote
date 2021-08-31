package ru.javaops.topjava2.to;

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

@Value
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class RestaurantTo extends NamedTo {
    @Serial
    private static final long serialVersionUID = 1L;

    @Size(min = 2, max = 100)
    @NoHtml
    private String address;

    @Size(min = 2, max = 100)
    @NoHtml
    private String phone;

    @NotNull
    private LocalDate lastVoteDate;

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
