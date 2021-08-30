package ru.javaops.topjava2.web.vote;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.javaops.topjava2.repository.DishRepository;
import ru.javaops.topjava2.repository.RestaurantRepository;
import ru.javaops.topjava2.repository.VoiceRepository;
import ru.javaops.topjava2.repository.VoteRepository;

@RestController
@RequestMapping(value = AdminVoteController.REST_URL, produces = MediaType.APPLICATION_JSON_VALUE)
@Slf4j
@AllArgsConstructor
public class AdminVoteController {
    static final String REST_URL = "/api/admin/votes";

    private final VoteRepository voteRepository;
    private final RestaurantRepository restaurantRepository;
    private final VoiceRepository voiceRepository;
    private final DishRepository dishRepository;


}
