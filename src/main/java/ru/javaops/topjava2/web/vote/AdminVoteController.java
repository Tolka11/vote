package ru.javaops.topjava2.web.vote;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import ru.javaops.topjava2.model.Vote;
import ru.javaops.topjava2.repository.VoteRepository;
import ru.javaops.topjava2.to.VoteTo;
import ru.javaops.topjava2.util.RatingMaker;
import ru.javaops.topjava2.web.AuthUser;

import javax.validation.Valid;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static ru.javaops.topjava2.util.validation.ValidationUtil.assureIdConsistent;

@Tag(name = "Admin Vote Controller", description = "The Vote API for Admin")
@RestController
@RequestMapping(value = AdminVoteController.REST_URL, produces = MediaType.APPLICATION_JSON_VALUE)
@Slf4j
@AllArgsConstructor
@CacheConfig(cacheNames = "votes")
public class AdminVoteController {
    static final String REST_URL = "/api/admin/votes";

    private final VoteRepository voteRepository;

    private RatingMaker ratingMaker;

    @Operation(summary = "Get all voting positions by date", description = "Get all voting positions by date")
    @ApiResponse(responseCode = "200", description = "Successful operation")
    @GetMapping
    @Cacheable
    public List<Vote> getAllByDate(@AuthenticationPrincipal AuthUser authUser,
                                   @Parameter(description = "Date", schema = @Schema(defaultValue = "2021-09-05"))
                                   @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        log.info("get all votes for date {} by {}", date, authUser.getUser());
        return voteRepository.getAllByDateEquals(date);
    }

    @Operation(summary = "Get vote by id", description = "Get vote by id")
    @ApiResponse(responseCode = "200", description = "Successful operation")
    @GetMapping("/{id}")
    public ResponseEntity<Vote> get(@AuthenticationPrincipal AuthUser authUser,
                                    @Parameter(description = "ID of vote item", schema = @Schema(type = "integer", defaultValue = "2"))
                                    @PathVariable int id) {
        log.info("get vote by id: {} by {}", id, authUser.getUser());
        return ResponseEntity.of(voteRepository.findById(id));
    }

    @Operation(summary = "Get vote rating by date", description = "Get rating of voting positions by date")
    @ApiResponse(responseCode = "200", description = "Successful operation")
    @GetMapping(value = "/rating")
    public List<VoteTo> getRatingByDate(@AuthenticationPrincipal AuthUser authUser,
                                        @Parameter(description = "Date", schema = @Schema(defaultValue = "2021-09-05"))
                                        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        log.info("get rating by date {} by user {}", date, authUser.getUser());
        List<VoteTo> voteRating = new ArrayList<>();
        Map<Integer, Integer> rating = ratingMaker.calculateRating(date);
        for (Vote vote : voteRepository.getAllByDateEquals(date)) {
            int voteNumber = (rating.get(vote.getId()) == null ? 0 : rating.get(vote.getId()));
            voteRating.add(new VoteTo(vote.getId(), vote.getName(), vote.getRestaurantId(), vote.getMenu(), voteNumber));
        }
        return voteRating.stream()
                .sorted((o1, o2) -> o2.getVotes().compareTo(o1.getVotes()))
                .collect(Collectors.toList());
    }

    @Operation(summary = "Delete vote position by id", description = "Delete vote position by id")
    @ApiResponse(responseCode = "204", description = "Done")
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @CacheEvict(cacheNames = "votes", allEntries = true)
    public void delete(@Parameter(description = "ID of vote item", schema = @Schema(type = "integer", defaultValue = "2")) @PathVariable int id) {
        voteRepository.delete(id);
    }

    @Operation(summary = "Update vote position by id", description = "Update vote position by id")
    @ApiResponse(responseCode = "204", description = "Done")
    @PutMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @CacheEvict(cacheNames = "votes", allEntries = true)
    public void update(@Parameter(description = "Vote object", schema = @Schema(implementation = Vote.class))
                       @Valid @RequestBody Vote vote,
                       @Parameter(description = "ID of vote item", schema = @Schema(type = "integer", defaultValue = "1"))
                       @PathVariable int id) {
        log.info("update {} with id={}", vote, id);
        assureIdConsistent(vote, id);
        voteRepository.save(vote);
    }
}