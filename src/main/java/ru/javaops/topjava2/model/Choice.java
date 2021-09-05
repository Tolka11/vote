package ru.javaops.topjava2.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Index;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;

@Schema(name = "choice", description = "Choice of vote position")
@Entity
@Table(name = "choice", indexes = {@Index(name = "i_user_date", columnList = "user_id,date")})
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@ToString(callSuper = true)
public class Choice extends BaseEntity {

    @Schema(description = "User ID", example = "1")
    @NotNull
    @Column(name = "user_id", nullable = false)
    private Integer userId;


    @Schema(description = "Vote ID", example = "33")
    @NotNull
    @Column(name = "vote_id", nullable = false)
    private Integer voteId;


    @Schema(description = "Date")
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
