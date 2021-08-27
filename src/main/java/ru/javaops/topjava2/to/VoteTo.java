package ru.javaops.topjava2.to;

import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.Value;
import ru.javaops.topjava2.util.validation.NoHtml;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serial;

@Value
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class VoteTo extends NamedTo {
    @Serial
    private static final long serialVersionUID = 1L;

    @NotNull
    private Integer restaurantId;

    @NotBlank
    @Size(min = 2, max = 1000)
    @NoHtml
    private String menu;

    @NotNull
    private Integer votes;

    public VoteTo(Integer id, String name, Integer restaurantId, String menu, Integer votes) {
        super(id, name);
        this.restaurantId = restaurantId;
        this.menu = menu;
        this.votes = votes;
    }
}
