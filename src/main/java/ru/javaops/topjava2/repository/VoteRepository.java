package ru.javaops.topjava2.repository;

import org.springframework.transaction.annotation.Transactional;
import ru.javaops.topjava2.model.Vote;

import java.time.LocalDate;
import java.util.List;

@Transactional(readOnly = true)
public interface VoteRepository extends BaseRepository<Vote> {
    public List<Vote> getAllByDateEquals(LocalDate date);

    public Vote findByRestaurantIdAndDate(Integer restaurantId, LocalDate date);
}
