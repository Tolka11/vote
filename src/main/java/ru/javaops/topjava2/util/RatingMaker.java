package ru.javaops.topjava2.util;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.concurrent.BasicThreadFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.javaops.topjava2.model.Choice;
import ru.javaops.topjava2.repository.ChoiceRepository;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Slf4j
@Component
// https://programtalk.com/java/start-background-thread-using-spring-on/
public class RatingMaker {

    @Autowired
    private ChoiceRepository choiceRepository;

    // Map< key - Vote id , value - number of votes >
    private Map<Integer, Integer> rating = new HashMap<>();

    private ExecutorService executorService;

    public Map<Integer, Integer> getRating() {
        return rating;
    }

    @PostConstruct
    public void init() {
        BasicThreadFactory factory = new BasicThreadFactory.Builder()
                .namingPattern("ratingmaker-thread-%d").build();
        executorService = Executors.newSingleThreadExecutor(factory);
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                log.info("update vote rating");
                while (true) {
                    rating = calculateRating(LocalDate.now());
                    try {
                        Thread.currentThread().sleep(60000);
                    } catch (InterruptedException e) {
                        log.info("rating maker stopped");
                    }
                }
            }
        });
        executorService.shutdown();
    }

    @PreDestroy
    public void beandestroy() {
        if (executorService != null) {
            executorService.shutdownNow();
        }
    }

    public Map<Integer, Integer> calculateRating(LocalDate date) {
        // exclude repeated voting (changing choice) Map< userId , voteId >
        Map<Integer, Integer> userChoice = new HashMap<>();
        for (Choice choice : choiceRepository.findAllByDateOrderByIdAsc(date)) {
            userChoice.put(choice.getUserId(), choice.getVoteId());
        }
        // make rating
        Map<Integer, Integer> rating = new HashMap<>();
        for (Integer voteId : userChoice.values()) {
            rating.put(voteId, rating.getOrDefault(voteId, 0) + 1);
        }
        return rating;
    }
}
