package ru.javaops.topjava2.web.restaurant;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import ru.javaops.topjava2.error.NotFoundException;
import ru.javaops.topjava2.model.Dish;
import ru.javaops.topjava2.model.Restaurant;
import ru.javaops.topjava2.model.Vote;
import ru.javaops.topjava2.repository.DishRepository;
import ru.javaops.topjava2.repository.RestaurantRepository;
import ru.javaops.topjava2.repository.VoteRepository;
import ru.javaops.topjava2.to.RestaurantTo;

import javax.validation.Valid;
import java.net.URI;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static ru.javaops.topjava2.util.validation.ValidationUtil.assureIdConsistent;
import static ru.javaops.topjava2.util.validation.ValidationUtil.checkNew;

@RestController
@RequestMapping(value = AdminRestaurantController.REST_URL, produces = MediaType.APPLICATION_JSON_VALUE)
@Slf4j
@AllArgsConstructor
@CacheConfig(cacheNames = {"restaurants", "votes"})
public class AdminRestaurantController {
    static final String REST_URL = "/api/admin/restaurants";

    private final RestaurantRepository restaurantRepository;
    private final DishRepository dishRepository;
    private final VoteRepository voteRepository;

    @GetMapping
    @Cacheable(cacheNames = "restaurants")
    public List<Restaurant> getAll() {
        log.info("find all restaurants");
        return restaurantRepository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Restaurant> get(@PathVariable int id) {
        log.info("find restaurant by id: {}", id);
        return ResponseEntity.of(restaurantRepository.findById(id));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @CacheEvict(cacheNames = "restaurants", allEntries = true)
    public void delete(@PathVariable int id) {
        restaurantRepository.delete(id);
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    @CacheEvict(cacheNames = "restaurants", allEntries = true)
    public ResponseEntity<Restaurant> create(@Valid @RequestBody Restaurant restaurant) {
        log.info("create {}", restaurant);
        checkNew(restaurant);
        Restaurant created = restaurantRepository.save(restaurant);
        URI uriOfNewResource = ServletUriComponentsBuilder.fromCurrentContextPath()
                .path(REST_URL + "/{id}")
                .buildAndExpand(created.getId()).toUri();
        return ResponseEntity.created(uriOfNewResource).body(created);
    }

    @PutMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @CacheEvict(cacheNames = "restaurants", allEntries = true)
    public void update(@Valid @RequestBody Restaurant restaurant, @PathVariable int id) {
        log.info("update {} with id={}", restaurant, id);
        assureIdConsistent(restaurant, id);
        restaurantRepository.save(restaurant);
    }

    @GetMapping("/{id}/menu")
    public ResponseEntity<RestaurantTo> getRestaurantWithLastMenu(@PathVariable int id) {
        log.info("find restaurant with last menu by id: {}", id);
        Restaurant restaurant = restaurantRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Restaurant with id " + id + " not found."));
        List<Dish> allDishes = dishRepository.findAllByRestaurantId(id);
        // Get dishes from last menu
        LocalDate lastMenuDate = allDishes.stream()
                .map(Dish::getDate)
                .max(LocalDate::compareTo)
                .get();
        List<Dish> dishes = allDishes.stream()
                .filter(dish -> dish.getDate().equals(lastMenuDate))
                .collect(Collectors.toList());
        // Return restaurant with last menu
        RestaurantTo restaurantTo = new RestaurantTo(restaurant.getId(), restaurant.getName(),
                restaurant.getAddress(), restaurant.getPhone(), lastMenuDate, dishes);
        return ResponseEntity.of(Optional.of(restaurantTo));
    }

    @PostMapping(value = "/{id}/vote", consumes = MediaType.APPLICATION_JSON_VALUE)
    @CacheEvict(cacheNames = "votes", allEntries = true)
    public ResponseEntity<Vote> createVote(@Valid @RequestBody RestaurantTo restaurantTo, @PathVariable int id) {
        log.info("create vote for restaurant {} with menu {}", id, restaurantTo.getDishes());
        StringBuilder menu = new StringBuilder();
        for (Dish dish : restaurantTo.getDishes()) {
            menu.append(dish.getName() + " - " + String.format("%.2f", dish.getPrice()).replace(",", ".") + "; ");
            // Save each today dish, even it's not first save this dish per day
            if (dish.getDate() == null || dish.getDate().isBefore(LocalDate.now())) {
                dish.setId(null);
            }
            dish.setDate(LocalDate.now());
            dish.setRestaurantId(id);
            dishRepository.save(dish);
        }
        // Save today vote, even it's not first save this vote per day
        Vote vote = new Vote(null, restaurantTo.getName(), menu.toString(), id, LocalDate.now());
        if (restaurantTo.getLastVoteDate().equals(LocalDate.now())) {
            Vote old = voteRepository.findByRestaurantIdAndDate(id, LocalDate.now());
            vote.setId(old.getId());
        }
        Vote created = voteRepository.save(vote);
        URI uriOfNewResource = ServletUriComponentsBuilder.fromCurrentContextPath()
                .path("/api/votes/{id}")
                .buildAndExpand(created.getId()).toUri();
        return ResponseEntity.created(uriOfNewResource).body(created);
    }
}