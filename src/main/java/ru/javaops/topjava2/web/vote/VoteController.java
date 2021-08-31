package ru.javaops.topjava2.web.vote;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import ru.javaops.topjava2.model.Choice;
import ru.javaops.topjava2.model.Vote;
import ru.javaops.topjava2.repository.ChoiceRepository;
import ru.javaops.topjava2.repository.VoteRepository;
import ru.javaops.topjava2.web.AuthUser;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping(value = VoteController.REST_URL, produces = MediaType.APPLICATION_JSON_VALUE)
@Slf4j
@AllArgsConstructor
@CacheConfig(cacheNames = "votes")
public class VoteController {
    static final String REST_URL = "/api/profile/votes";

    private final VoteRepository voteRepository;
    private final ChoiceRepository choiceRepository;

    @GetMapping
    @Cacheable
    public List<Vote> getAllToday(@AuthenticationPrincipal AuthUser authUser) {
        log.info("getAllToday for votes with date {} by user {}", LocalDate.now(), authUser.getUser());
        return voteRepository.getAllByDateEquals(LocalDate.now());
    }

    @PutMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void makeChoice(@AuthenticationPrincipal AuthUser authUser, @PathVariable int id) {
        log.info("make choice for vote with id={}", id);
        choiceRepository.save(new Choice(null, authUser.id(), id, LocalDate.now()));
    }
}
