package ru.javaops.topjava2.web.vote;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.error.ErrorAttributeOptions;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import ru.javaops.topjava2.error.AppException;
import ru.javaops.topjava2.model.Choice;
import ru.javaops.topjava2.model.Vote;
import ru.javaops.topjava2.repository.ChoiceRepository;
import ru.javaops.topjava2.repository.VoteRepository;
import ru.javaops.topjava2.to.VoteTo;
import ru.javaops.topjava2.util.RatingMaker;
import ru.javaops.topjava2.web.AuthUser;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Tag(name = "Vote Controller", description = "The Vote API")
@RestController
@RequestMapping(value = VoteController.REST_URL, produces = MediaType.APPLICATION_JSON_VALUE)
@Slf4j
@AllArgsConstructor
@CacheConfig(cacheNames = "votes")
public class VoteController {
    static final String REST_URL = "/api/profile/votes";
    static final LocalTime REVOTE_DEADLINE = LocalTime.of(11, 0, 0);

    private final VoteRepository voteRepository;
    private final ChoiceRepository choiceRepository;

    private RatingMaker ratingMaker;

    @Operation(summary = "Get today vote positions", description = "Get all today voting positions")
    @ApiResponse(responseCode = "200", description = "Successful operation")
    @GetMapping
    @Cacheable
    public List<Vote> getAllToday(@AuthenticationPrincipal AuthUser authUser) {
        log.info("getAllToday for votes with date {} by user {}", LocalDate.now(), authUser.getUser());
        return voteRepository.getAllByDateEquals(LocalDate.now());
    }

    @Operation(summary = "Get today vote rating", description = "Get today rating of voting positions depending vote numbers")
    @ApiResponse(responseCode = "200", description = "Successful operation")
    @GetMapping(value = "/rating")
    public List<VoteTo> getTodayRating(@AuthenticationPrincipal AuthUser authUser) {
        log.info("getAllTodayRating for votes with date {} by user {}", LocalDate.now(), authUser.getUser());
        List<VoteTo> voteRating = new ArrayList<>();
        Map<Integer, Integer> rating = ratingMaker.getRating();
        for (Vote vote : voteRepository.getAllByDateEquals(LocalDate.now())) {
            int voteNumber = (rating.get(vote.getId()) == null ? 0 : rating.get(vote.getId()));
            voteRating.add(new VoteTo(vote.getId(), vote.getName(), vote.getRestaurantId(), vote.getMenu(), voteNumber));
        }
        return voteRating.stream()
                .sorted((o1, o2) -> o2.getVotes().compareTo(o1.getVotes()))
                .collect(Collectors.toList());
    }

    @Operation(summary = "Make choice", description = "Make choice from today vote positions, creating new record in \"CHOISE\" table of database for current user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Done"),
            @ApiResponse(responseCode = "406", description = "Not allowed to change your vote after 11-00")})
    @PutMapping(value = "/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void makeChoice(@AuthenticationPrincipal AuthUser authUser,
                           @Parameter(description = "ID of vote position", schema = @Schema(type = "integer", defaultValue = "2"))
                           @PathVariable int id) {
        log.info("make choice for vote with id={}", id);
        if (LocalTime.now().isAfter(REVOTE_DEADLINE) &&
                choiceRepository.findAllByUserIdAndDate(authUser.getUser().id(), LocalDate.now()).size() > 0) {
            throw new AppException(HttpStatus.NOT_ACCEPTABLE,
                    "Not allowed to change your vote after " + REVOTE_DEADLINE,
                    ErrorAttributeOptions.defaults());
        }
        choiceRepository.save(new Choice(null, authUser.id(), id, LocalDate.now()));
    }
}
