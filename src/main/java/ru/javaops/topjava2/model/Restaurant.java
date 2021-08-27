package ru.javaops.topjava2.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import ru.javaops.topjava2.util.validation.NoHtml;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDate;

@Entity
@Table(name = "restaurant")
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@ToString(callSuper = true)
public class Restaurant extends NamedEntity {

    @Size(min = 2, max = 100)
    @Column(name = "address")
    @NoHtml
    private String address;

    @Size(min = 2, max = 100)
    @Column(name = "phone")
    @NoHtml
    private String phone;

    @NotNull
    @Column(name = "last_vote_date", nullable = false)
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private LocalDate lastVoteDate;
}
