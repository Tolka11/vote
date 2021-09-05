package ru.javaops.topjava2.to;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.Value;
import ru.javaops.topjava2.util.validation.NoHtml;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serial;

@Schema(description = "Voting position with number of votes")
@Value
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class VoteTo extends NamedTo {
    @Serial
    private static final long serialVersionUID = 1L;

    // Field "name" is used for name of restaurant

    @Schema(description = "Restaurant ID", example = "1")
    @NotNull
    private Integer restaurantId;

    @Schema(description = "Menu", example = "Soup - 2.11; Cutlet - 3.54; Tea - 0.24; ")
    @NotBlank
    @Size(min = 2, max = 1000)
    @NoHtml
    private String menu;

    @Schema(description = "Number of votes", example = "23")
    @NotNull
    private Integer votes;

    public VoteTo(Integer id, String name, Integer restaurantId, String menu, Integer votes) {
        super(id, name);
        this.restaurantId = restaurantId;
        this.menu = menu;
        this.votes = votes;
    }
}
