[Voting system for deciding where to have lunch](https://github.com/Tolka11/vote)
===============================

Spring/JPA application with authorization and role-based access rights without frontend. Using the  Java tools and technologies: Maven, Spring-Boot, Spring MVC, Security, JPA(Hibernate), REST(Jackson), H2 database.

![img_1.png](img_1.png)

### Application capabilities:
- 2 types of users: admin and regular users
  - Users can register and manage own profile
  - Admin can manage user profiles
- Admin can input a restaurant and it's lunch menu of the day (2-5 items usually, just a dish name and price)
  - Users can view information about restaurants
- Menu changes each day (admins do the updates)
  - Each restaurant provides a new menu each day
- Users can vote on which restaurant they want to have lunch at, and view the today rating
  - Admin can manage vote records 
- Only one vote counted per user, if user votes again the same day:
  - If it is before 11:00 we assume that he changed his mind
  - If it is after 11:00 then it is too late, vote can't be changed

-------------------

### REST API documentation: [Swagger on started application](localhost:8080/swagger-ui/index.html?configUrl=/v3/api-docs/swagger-config#/)
> - Run Application `RestaurantVotingApplication` 
> - Open API documentation by link: <a href="localhost:8080/swagger-ui/index.html?configUrl=/v3/api-docs/swagger-config#/">localhost:8080/swagger-ui/index.html?configUrl=/v3/api-docs/swagger-config#/</a>

-------------------

### Implementation features 

#### H2 Database in memory with tables:
- **USERS** - contains user records
  - corresponds to the `User.class`
- **USER_ROLES** - contains user roles
  - corresponds to the `Role.enum`
- **RESTAURANT** - contains restaurant records 
  - corresponds to the `Restaurant.class`
  - unique constraints by fields `name` and `address`
- **DISH** - contains dish records
  - corresponds to the `Dish.class`
  - using to create menu for vote
  - store the history of menu changing for each restaurant
  - unique constraints by fields `name`, `restaurant_id` and `date`
  - indexes by fields `restaurant_id` and `date`
- **VOTE** - contains vote records
  - corresponds to the `Vote.class`
  - using to create day voting list
  - store the history of day voting list
  - unique constraints by fields `restaurant_id` and `date`
  - indexes by field `date`
- **CHOICE** - contains choice records
  - corresponds to the `Choice.class`
  - using to create day rating list
  - store the history of votes for each user
  - indexes by fields `user_id` and `date`
  - no unique constraints, because user can change choice several times per day, and return to first choice. Sort user choices by ID we can get last choice  

#### 3 caches configured:
- users 
- restaurants 
- votes

#### RatingMaker utility
Used to create voting rating by selected date, minimize accessing the database, and has: 
- Method `public Map<Integer, Integer> calculateRating(LocalDate date)` , that return Map where: *key - "vote id"* and *value - "number of votes"* for selected date. Admin Vote Controller use it for create rating by date. This method:
  - creates `Map< userId , voteId >` for get last user choice of day
  - based on user choice create rating of votes `Map< key - Vote id , value - number of votes >`
- Background thread that once per minute calculate today rating and store it in `private Map<Integer, Integer> rating`
- Method `public Map<Integer, Integer> getRating()` that return stored rating. Vote Controller use it for create today rating 

#### Creating Vote process by Admin
- `Vote.class` object has a set of fields for minimize database access:
  - **id**
  - **name** - name of restaurant, show in voting list
  - **menu** - show in voting list
  - **restaurantId** - for make choice  
  - **date** - date of vote
- Admin get the restaurant with last menu from REST API, change dishes in menu and post restaurant with new menu to REST API 
- Method `createVote` in `AdminRestaurantController` get each dish from menu and stores them into DB table DISH with today date. Also each dish converts to string `"dish_name - price; "` and appends in `StringBuilder menu`. For business lunch can be used only one `Dish.class` object, for exaple "Soup, cutlet, compote" with price 
- Then creating `Vote.class` object with `StringBuilder menu`, save it in database
- List of `Vote.class` objects for selected date is a voting list

#### Voting process by Users
- User vote for the restaurant
- Method `makeChoice` in `VoteController` checks if it's the revote after 11-00, throw exception
- Else it creates new `Choice.class` object, and save it in database


-------------------

> 2021. Anatoliy Skrylnikov. <a href="mailto:anatoliy.skrylnikov@gmail.com">anatoliy.skrylnikov@gmail.com</a>

-------------------
