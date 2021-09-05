package ru.javaops.topjava2.web.user;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import ru.javaops.topjava2.model.User;

import javax.validation.Valid;
import java.net.URI;
import java.util.List;

import static ru.javaops.topjava2.util.validation.ValidationUtil.assureIdConsistent;
import static ru.javaops.topjava2.util.validation.ValidationUtil.checkNew;

@Tag(name = "Admin User Controller", description = "The User API for Admin")
@RestController
@RequestMapping(value = AdminUserController.REST_URL, produces = MediaType.APPLICATION_JSON_VALUE)
@Slf4j
@CacheConfig(cacheNames = "users")
public class AdminUserController extends AbstractUserController {

    static final String REST_URL = "/api/admin/users";

    @Operation(summary = "Get user by ID", description = "Get user by ID")
    @ApiResponse(responseCode = "200", description = "Successful operation")
    @Override
    @GetMapping("/{id}")
    public ResponseEntity<User> get(@Parameter(description = "User ID", schema = @Schema(type = "integer", defaultValue = "1"))
                                    @PathVariable int id) {
        return super.get(id);
    }

    @Operation(summary = "Delete restaurant by ID", description = "Delete restaurant by ID")
    @ApiResponse(responseCode = "204", description = "Done")
    @Override
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@Parameter(description = "User ID", schema = @Schema(type = "integer", defaultValue = "1"))
                       @PathVariable int id) {
        super.delete(id);
    }

    @Operation(summary = "Get all users", description = "Get all users for Admin")
    @ApiResponse(responseCode = "200", description = "Successful operation")
    @GetMapping
    @Cacheable
    public List<User> getAll() {
        log.info("getAll");
        return repository.findAll(Sort.by(Sort.Direction.ASC, "name", "email"));
    }

    @Operation(summary = "Create user", description = "Create user")
    @ApiResponse(responseCode = "201", description = "Created")
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    @CacheEvict(allEntries = true)
    public ResponseEntity<User> createWithLocation(@Parameter(description = "User object", schema = @Schema(implementation = User.class))
                                                   @Valid @RequestBody User user) {
        log.info("create {}", user);
        checkNew(user);
        User created = prepareAndSave(user);
        URI uriOfNewResource = ServletUriComponentsBuilder.fromCurrentContextPath()
                .path(REST_URL + "/{id}")
                .buildAndExpand(created.getId()).toUri();
        return ResponseEntity.created(uriOfNewResource).body(created);
    }

    @Operation(summary = "Update user by ID", description = "Update user by ID")
    @ApiResponse(responseCode = "204", description = "Done")
    @PutMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @CacheEvict(allEntries = true)
    public void update(@Parameter(description = "User object", schema = @Schema(implementation = User.class))
                       @Valid @RequestBody User user,
                       @Parameter(description = "User ID", schema = @Schema(type = "integer", defaultValue = "1"))
                       @PathVariable int id) {
        log.info("update {} with id={}", user, id);
        assureIdConsistent(user, id);
        prepareAndSave(user);
    }

    @Operation(summary = "Get user by e-mail", description = "Get user by e-mail")
    @ApiResponse(responseCode = "200", description = "Successful operation")
    @GetMapping("/by")
    public ResponseEntity<User> getByEmail(@Parameter(description = "E-mail", schema = @Schema(type = "String", defaultValue = "user@yandex.ru"))
                                           @RequestParam String email) {
        log.info("getByEmail {}", email);
        return ResponseEntity.of(repository.getByEmail(email));
    }

    @Operation(summary = "Enable user by ID", description = "Enable user by ID")
    @ApiResponse(responseCode = "204", description = "Done")
    @PatchMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Transactional
    @CacheEvict(allEntries = true)
    public void enable(@Parameter(description = "User ID", schema = @Schema(type = "integer", defaultValue = "1"))
                       @PathVariable int id,
                       @Parameter(description = "Enable user", schema = @Schema(type = "boolean", defaultValue = "true"))
                       @RequestParam boolean enabled) {
        log.info(enabled ? "enable {}" : "disable {}", id);
        User user = repository.getById(id);
        user.setEnabled(enabled);
    }
}