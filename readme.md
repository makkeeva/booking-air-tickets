# Атоматизированная информационная система бронирования авиабилетов
![Java](https://img.shields.io/badge/java-%23ED8B00.svg?style=for-the-badge&logo=java&logoColor=white)
![Spring](https://img.shields.io/badge/spring-%236DB33F.svg?style=for-the-badge&logo=spring&logoColor=white)

Данное приложение предназначено для автоматизации процесса бронирования билетов, что позволяет клиентам упростить использованием услугами аэропорта, а также позволит работникам упростить и ускорить процесс управления аэропортом.

## Запуск приложения
Для запуска системы броинрования авиабилетов необходимо:
1. Создать базу данных в MySQL с названием **booking-air-tickets**.
2. В **application.properties** установить **create** для параметра **spring.jpa.hibernate.ddl-auto**
3. Добавить в параметрах запуска проекта переменные среды, которые представлены ниже
```
DATASOURCE_URL=jdbc:mysql://localhost:3306/booking-air-tickets;
DATASOURCE_USERNAME=root;
DATASOURCE_PASSWORD=1234
```
4. В **application.properties** вернуть значение параметра **spring.jpa.hibernate.ddl-auto** в **none**

Данное приложение будет доступно по адресу http://localhost:8080/ticket/all

## Backend технологии

1. Java
2. Lombok
3. MySQL
4. Hibernate
5. Spring
    1. Spring Core
    2. Spring MVC
    3. Spring Security
    4. Spring Boot
    5. Spring Data
6. Thymeleaf

## Frontend технологии
1. HTML
2. CSS
3. Bootstrap
4. JS
5. jQuery (+DataTables)

## Реализованный функционал

В системе существует 2 типо вользователей:
1. **Администратор**</br>
    1. Управление билетами
       1. Добавление
       2. Просмотр с возможностью сортировки и поиска
    2. Авторизация
    3. Регистрация 
2. **Пользователь**</br>
   1. Просмотр доступных билетов с возможностью сортировки и поиска
   2. Бронирование билетов
   3. Просмотр забронированных билитов (необходима доработка)
   4. Авторизация
   5. Регистрация 