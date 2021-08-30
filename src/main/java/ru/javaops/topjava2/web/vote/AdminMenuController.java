package ru.javaops.topjava2.web.vote;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.javaops.topjava2.error.NotFoundException;
import ru.javaops.topjava2.model.Dish;
import ru.javaops.topjava2.model.Restaurant;
import ru.javaops.topjava2.repository.DishRepository;
import ru.javaops.topjava2.repository.RestaurantRepository;
import ru.javaops.topjava2.repository.VoiceRepository;
import ru.javaops.topjava2.repository.VoteRepository;
import ru.javaops.topjava2.to.RestaurantTo;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping(value = AdminMenuController.REST_URL, produces = MediaType.APPLICATION_JSON_VALUE)
@Slf4j
@AllArgsConstructor
public class AdminMenuController {
    static final String REST_URL = "/api/admin/menus";

    private final VoteRepository voteRepository;
    private final RestaurantRepository restaurantRepository;
    private final VoiceRepository voiceRepository;
    private final DishRepository dishRepository;

    @GetMapping("/restaurant/{id}")
    @Cacheable
    public ResponseEntity<RestaurantTo> getRestaurantWithLastMenu(@PathVariable int id) {
        log.info("find restaurant with last menu by id: {}", id);
        Restaurant restaurant = restaurantRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Restaurant with id " + id + " not found."));
        List<Dish> dishes = getLastMenu(id);
        LocalDate lastMenuDate = null;
        if (dishes.size() == 0) {
            dishes = null;
        } else {
            lastMenuDate = dishes.get(0).getDate();
        }
        RestaurantTo restaurantTo = new RestaurantTo(restaurant.getId(), restaurant.getName(),
                restaurant.getAddress(), restaurant.getPhone(), lastMenuDate, dishes);
        return ResponseEntity.of(Optional.of(restaurantTo));
    }

    public List<Dish> getLastMenu(int id) {
        List<Dish> dishes = dishRepository.findAllByRestaurantIdOrderByIdDesc(id);
        LocalDate lastMenuDate = dishes.get(0).getDate();
        return dishes.stream()
                .filter(dish -> dish.getDate().equals(lastMenuDate))
                .collect(Collectors.toList());
    }
}
