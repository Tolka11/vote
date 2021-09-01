package ru.javaops.topjava2.web.restaurant;

import ru.javaops.topjava2.model.Dish;
import ru.javaops.topjava2.model.Restaurant;
import ru.javaops.topjava2.to.RestaurantTo;
import ru.javaops.topjava2.web.MatcherFactory;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class RestaurantTestData {
    public static final MatcherFactory.Matcher<Restaurant> MATCHER = MatcherFactory.usingIgnoringFieldsComparator(Restaurant.class);
    public static MatcherFactory.Matcher<RestaurantTo> TO_MATCHER = MatcherFactory.usingEqualsComparator(RestaurantTo.class);

    public static final int RESTAURANT1_ID = 1;

    public static final Restaurant restaurant1 = new Restaurant(RESTAURANT1_ID, "Bigrest", "Bolshaya ave. 12", "888-88-88");
    public static final Restaurant restaurant2 = new Restaurant(RESTAURANT1_ID + 1, "Middlecafe", "Middle str. 5", "555-55-55");
    public static final Restaurant restaurant3 = new Restaurant(RESTAURANT1_ID + 2, "Littleponchik", "Small str. 3", "333-33-33");
    public static final Restaurant restaurant4 = new Restaurant(RESTAURANT1_ID + 3, "Voting cafe", "Vote str. 77", "777-77-77");

    public static final Dish dish3 = new Dish(3, "Donut", 3, LocalDate.now(), 1.0);
    public static final Dish dish4 = new Dish(4, "Tea", 3, LocalDate.now(), 0.5);
    public static final Dish dish5 = new Dish(null, "Mamalyga", 4, LocalDate.now(), 2.0);
    public static final Dish dish6 = new Dish(null, "Shaslyk", 4, LocalDate.now(), 1.5);
    public static final Dish dish7 = new Dish(null, "Beer", 4, LocalDate.now(), 2.33);

    public static final List<Dish> dishes = new ArrayList<>(Arrays.asList(dish3, dish4));
    public static final List<Dish> dishesForCreateVote = new ArrayList<>(Arrays.asList(dish5, dish6, dish7));

    public static final RestaurantTo restaurantTo3 = new RestaurantTo(RESTAURANT1_ID + 2, "Littleponchik", "Small str. 3", "333-33-33", LocalDate.now(), dishes);
    public static final RestaurantTo restaurantTo4 = new RestaurantTo(RESTAURANT1_ID + 3, "Voting cafe", "Vote str. 77", "777-77-77", LocalDate.now(), dishesForCreateVote);

    public static final List<Restaurant> restaurants = List.of(restaurant1, restaurant2, restaurant3, restaurant4);

    public static Restaurant getNew() {
        return new Restaurant(null, "Novichok", "Novaya str. 1", "111-11-11");
    }

    public static Restaurant getUpdated() {
        return new Restaurant(RESTAURANT1_ID, "Rebranded", "Very big ave. 12", "909-00-00");
    }
}