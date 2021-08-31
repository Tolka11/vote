### Curl:

#### get all restaurants
`curl -s http://localhost:8080/api/restaurants --user user@yandex.ru:password`

#### get restaurant 2
`curl -s http://localhost:8080/api/restaurants/2 --user admin@gmail.com:admin`

#### delete restaurant 2
`curl -s -X DELETE http://localhost:8080/api/admin/restaurants/2 --user admin@gmail.com:admin`

#### create restaurant
`curl -s -X POST -d "{\"name\":\"Pirozhok\",\"address\":\"Letnyaa str. 11\",\"phone\":\"111-11-11\"}" -H "Content-Type:application/json;charset=UTF-8" http://localhost:8080/api/admin/restaurants --user admin@gmail.com:admin`

#### update restaurant
`curl -s -X PUT -d "{\"id\":1,\"name\":\"Biggresst\",\"address\":\"Big ave. 1\",\"phone\":\"111-11-11\"}" -H "Content-Type:application/json" http://localhost:8080/api/admin/restaurants/1 --user admin@gmail.com:admin`

#### get restaurant 3 WithLastMenu
`curl -s http://localhost:8080/api/admin/restaurants/3/menu --user admin@gmail.com:admin`

#### create vote
`curl -s -X POST -d "{\"name\":\"NewRestaurant\",\"lastVoteDate\":\"2001-01-20\",\"dishes\":[{\"name\":\"NewDonut\",\"date\":\"2021-08-01\",\"price\":1.11},{\"name\":\"NewTea\",\"date\":\"2021-08-01\",\"price\":0.43}]}" -H "Content-Type:application/json;charset=UTF-8" http://localhost:8080/api/admin/restaurants/4/vote --user admin@gmail.com:admin`

#### get vote 
`curl -s http://localhost:8080/api/profile/votes --user admin@gmail.com:admin`

#### get vote rating
`curl -s http://localhost:8080/api/profile/votes/rating --user admin@gmail.com:admin`

#### voting by user for vote 3
`curl -s -X PUT -H "Content-Type:application/json" http://localhost:8080/api/profile/votes/3 --user user@yandex.ru:password`

---------------
#### register User
`curl -s -i -X POST -d '{"name":"New User","email":"test@mail.ru","password":"test-password"}' -H 'Content-Type:application/json;charset=UTF-8' http://localhost:8080/topjava/rest/profile`
#### filter Meals
`curl -s "http://localhost:8080/topjava/rest/profile/meals/filter?startDate=2020-01-30&startTime=07:00:00&endDate=2020-01-31&endTime=11:00:00" --user user@yandex.ru:password`
#### delete Meals
`curl -s -X DELETE http://localhost:8080/topjava/rest/profile/meals/100002 --user user@yandex.ru:password`

#### create Meals
`curl -s -X POST -d '{"dateTime":"2020-02-01T12:00","description":"Created lunch","calories":300}' -H 'Content-Type:application/json;charset=UTF-8' http://localhost:8080/topjava/rest/profile/meals --user user@yandex.ru:password`

#### update Meals
`curl -s -X PUT -d '{"dateTime":"2020-01-30T07:00", "description":"Updated breakfast", "calories":200}' -H 'Content-Type: application/json' http://localhost:8080/topjava/rest/profile/meals/100003 --user user@yandex.ru:password`

#### validate with Error
`curl -s -X POST -d '{}' -H 'Content-Type: application/json' http://localhost:8080/topjava/rest/admin/users --user admin@gmail.com:admin`
`curl -s -X PUT -d '{"dateTime":"2015-05-30T07:00"}' -H 'Content-Type: application/json' http://localhost:8080/topjava/rest/profile/meals/100003 --user user@yandex.ru:password`




[Проект TopJava-2](https://javaops.ru/view/topjava2)
===============================

#### Разбор решения [выпускного проекта TopJava](https://github.com/JavaOPs/topjava/blob/master/graduation.md)
- Spring Boot 2.5, Lombok, H2, Swagger/OpenAPI 3.0, Caffeine Cache
- Исходный код взят из миграции TopJava на Spring Boot (без еды)
- На основе этого репозитория на курсе будет выполняться выпускной проект "Голосование за рестораны"

#### Рефакторинг кода TopJava:
- в нашем приложении теперь только REST контроллеры, не надо добавлять `Rest` в имя
- заменил префикс `/rest` в URLs на `/api` 
- каждый контроллер занимается своими CRUD, переименовал `Admin[Rest]Controller` в `AdminUserController`
- исключил `AppConfig.h2Server` из тестов, он там не нужен
- удалил проверки `ValidationUtil.checkNotFound`. Есть готовый метод `JpaRepository.getById`, который бросает `EntityNotFoundException`. 
Добавил его обработку в `GlobalExceptionHandler`.
- сделал общий метод `BaseRepository.deleteExisted`
- TODO: кэшируйте только наиболее часто запрашиваемые данные

