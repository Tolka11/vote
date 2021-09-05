package ru.javaops.topjava2.web.restaurant;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.javaops.topjava2.model.Restaurant;
import ru.javaops.topjava2.repository.RestaurantRepository;

import java.util.List;

@Tag(name = "Restaurant Controller", description = "The Restaurant API")
@RestController
@RequestMapping(value = RestaurantController.REST_URL, produces = MediaType.APPLICATION_JSON_VALUE)
@Slf4j
@AllArgsConstructor
@CacheConfig(cacheNames = "restaurants")
public class RestaurantController {
    static final String REST_URL = "/api/restaurants";

    private final RestaurantRepository restaurantRepository;

    @Operation(summary = "Get all restaurants", description = "Get all restaurants")
    @ApiResponse(responseCode = "200", description = "Successful operation")
    @GetMapping
    @Cacheable
    public List<Restaurant> getAll() {
        log.info("find all restaurants");
        return restaurantRepository.findAll();
    }

    @Operation(summary = "Get restaurant by ID", description = "Get restaurant by ID")
    @ApiResponse(responseCode = "200", description = "Successful operation")
    @GetMapping("/{id}")
    @Cacheable
    public ResponseEntity<Restaurant> get(@Parameter(description = "Restaurant ID", schema = @Schema(type = "integer", defaultValue = "2"))
                                          @PathVariable int id) {
        log.info("find restaurant by id: {}", id);
        return ResponseEntity.of(restaurantRepository.findById(id));
    }
}