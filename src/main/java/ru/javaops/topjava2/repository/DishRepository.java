package ru.javaops.topjava2.repository;

import ru.javaops.topjava2.model.Dish;

import java.time.LocalDate;
import java.util.List;

public interface DishRepository extends BaseRepository<Dish> {
    public List<Dish> findAllByRestaurantId(int restaurantId);

    public Dish findByNameAndRestaurantIdAndDate(String name, Integer restaurantId, LocalDate date);

    public List<Dish> findAllByRestaurantIdOrderByIdDesc(int restaurantId);
}
