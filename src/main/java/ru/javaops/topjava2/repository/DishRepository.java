package ru.javaops.topjava2.repository;

import ru.javaops.topjava2.model.Dish;

import java.util.List;

public interface DishRepository extends BaseRepository<Dish> {
    public List<Dish> findAllByRestaurantIdOrderByIdDesc(int restaurantId);
}
