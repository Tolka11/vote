package ru.javaops.topjava2.web.vote;

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

@RestController
@RequestMapping(value = AdminVoteController.REST_URL, produces = MediaType.APPLICATION_JSON_VALUE)
@Slf4j
@AllArgsConstructor
@CacheConfig(cacheNames = "votes")
public class AdminVoteController {
    static final String REST_URL = "/api/admin/votes";

    private final VoteRepository voteRepository;

    private RatingMaker ratingMaker;

    @GetMapping
    @Cacheable
    public List<Vote> getAllByDate(@AuthenticationPrincipal AuthUser authUser,
                                   @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        log.info("get all votes for date {} by {}", date, authUser.getUser());
        return voteRepository.getAllByDateEquals(date);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Vote> get(@AuthenticationPrincipal AuthUser authUser,
                                    @PathVariable int id) {
        log.info("get vote by id: {} by {}", id, authUser.getUser());
        return ResponseEntity.of(voteRepository.findById(id));
    }

    @GetMapping(value = "/rating")
    public List<VoteTo> getRatingByDate(@AuthenticationPrincipal AuthUser authUser,
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

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @CacheEvict(cacheNames = "votes", allEntries = true)
    public void delete(@PathVariable int id) {
        voteRepository.delete(id);
    }

    @PutMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @CacheEvict(cacheNames = "votes", allEntries = true)
    public void update(@Valid @RequestBody Vote vote, @PathVariable int id) {
        log.info("update {} with id={}", vote, id);
        assureIdConsistent(vote, id);
        voteRepository.save(vote);
    }
}
