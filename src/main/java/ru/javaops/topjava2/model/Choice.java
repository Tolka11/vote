package ru.javaops.topjava2.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Index;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;

@Entity
@Table(name = "choice", indexes = {@Index(name = "i_user_date", columnList = "user_id,date")})
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@ToString(callSuper = true)
public class Choice extends BaseEntity {
    @NotNull
    @Column(name = "user_id", nullable = false)
    private Integer userId;

    @NotNull
    @Column(name = "vote_id", nullable = false)
    private Integer voteId;

    @Column(name = "date", nullable = false)
    @NotNull
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private LocalDate date;

    public Choice(Integer id, Integer userId, Integer voteId, LocalDate date) {
        super(id);
        this.userId = userId;
        this.voteId = voteId;
        this.date = date;
    }
}
