package ru.javaops.topjava2.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import ru.javaops.topjava2.util.validation.NoHtml;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDate;

@Entity
@Table(name = "vote")
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@ToString(callSuper = true)
public class Vote extends NamedEntity {

    @NotNull
    @Column(name = "restaurant_id", nullable = false)
    private Integer restaurantId;

    @Column(name = "date", nullable = false)
    @NotNull
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private LocalDate date;

    @NotBlank
    @Size(min = 2, max = 1000)
    @Column(name = "menu", nullable = false)
    @NoHtml
    private String menu;

    @NotNull
    @Column(name = "votes", nullable = false)
    private Integer votes;

}
