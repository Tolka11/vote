package ru.javaops.topjava2.web.restaurant;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
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

@Tag(name = "Admin Restaurant Controller", description = "The Restaurant API for Admin")
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

    @Operation(summary = "Get all restaurants", description = "Get all restaurants for Admin")
    @ApiResponse(responseCode = "200", description = "Successful operation")
    @GetMapping
    @Cacheable(cacheNames = "restaurants")
    public List<Restaurant> getAll() {
        log.info("find all restaurants");
        return restaurantRepository.findAll();
    }

    @Operation(summary = "Get restaurant by ID", description = "Get restaurant by ID")
    @ApiResponse(responseCode = "200", description = "Successful operation")
    @GetMapping("/{id}")
    public ResponseEntity<Restaurant> get(@Parameter(description = "Restaurant ID", schema = @Schema(type = "integer", defaultValue = "3"))
                                          @PathVariable int id) {
        log.info("find restaurant by id: {}", id);
        return ResponseEntity.of(restaurantRepository.findById(id));
    }

    @Operation(summary = "Delete restaurant by ID", description = "Delete restaurant by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Done"),
            @ApiResponse(responseCode = "401", description = "This user is unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden for this user")})
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @CacheEvict(cacheNames = "restaurants", allEntries = true)
    public void delete(@Parameter(description = "Restaurant ID", schema = @Schema(type = "integer", defaultValue = "4"))
                       @PathVariable int id) {
        restaurantRepository.delete(id);
    }

    @Operation(summary = "Create restaurant", description = "Create restaurant")
    @ApiResponse(responseCode = "201", description = "Created")
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    @CacheEvict(cacheNames = "restaurants", allEntries = true)
    public ResponseEntity<Restaurant> create(@Parameter(description = "Restaurant object", schema = @Schema(implementation = Restaurant.class))
                                             @Valid @RequestBody Restaurant restaurant) {
        log.info("create {}", restaurant);
        checkNew(restaurant);
        Restaurant created = restaurantRepository.save(restaurant);
        URI uriOfNewResource = ServletUriComponentsBuilder.fromCurrentContextPath()
                .path(REST_URL + "/{id}")
                .buildAndExpand(created.getId()).toUri();
        return ResponseEntity.created(uriOfNewResource).body(created);
    }

    @Operation(summary = "Update restaurant by ID", description = "Update restaurant by ID")
    @ApiResponse(responseCode = "204", description = "Done")
    @PutMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @CacheEvict(cacheNames = "restaurants", allEntries = true)
    public void update(@Parameter(description = "Restaurant object", schema = @Schema(implementation = Restaurant.class))
                       @Valid @RequestBody Restaurant restaurant,
                       @Parameter(description = "Restaurant ID", schema = @Schema(type = "integer", defaultValue = "1"))
                       @PathVariable int id) {
        log.info("update {} with id={}", restaurant, id);
        assureIdConsistent(restaurant, id);
        restaurantRepository.save(restaurant);
    }

    @Operation(summary = "Get restaurant with last menu by ID", description = "Get restaurant with last menu by ID")
    @ApiResponse(responseCode = "200", description = "Successful operation")
    @GetMapping("/{id}/menu")
    public ResponseEntity<RestaurantTo> getRestaurantWithLastMenu(@Parameter(description = "Restaurant ID", schema = @Schema(type = "integer", defaultValue = "3"))
                                                                  @PathVariable int id) {
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

    @Operation(summary = "Create vote by restaurant ID",
            description = "Create vote by restaurant ID. If lastVoteDate of restaurant is today, get today vote of this restaurant from database and update it")
    @ApiResponse(responseCode = "201", description = "Created")
    @PostMapping(value = "/{id}/vote", consumes = MediaType.APPLICATION_JSON_VALUE)
    @CacheEvict(cacheNames = "votes", allEntries = true)
    public ResponseEntity<Vote> createVote(@Parameter(description = "Restaurant object", schema = @Schema(implementation = RestaurantTo.class))
                                           @Valid @RequestBody RestaurantTo restaurantTo,
                                           @Parameter(description = "Restaurant ID", schema = @Schema(type = "integer", defaultValue = "1"))
                                           @PathVariable int id) {
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
            vote.setId(old == null ? null : old.getId());
        }
        Vote created = voteRepository.save(vote);
        URI uriOfNewResource = ServletUriComponentsBuilder.fromCurrentContextPath()
                .path("/api/votes/{id}")
                .buildAndExpand(created.getId()).toUri();
        return ResponseEntity.created(uriOfNewResource).body(created);
    }
}