package org.example.marketplaceservice.models;


import jakarta.persistence.*;

import java.util.List;

//Сущность Person, которая хранит в себе информацию о человеке и всех его заказах
@Entity
@Table(name = "Person")
public class Person {

    //Id человека
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name ="id")
    private int id;

    //Имя человека
    @Column(name = "name")
    private String name;

    //Почта аккаунта
    @Column(name = "email")
    private String email;

    //Логин аккаунта
    @Column(name = "login")
    private String login;

    //Пароль аккаунта
    @Column(name = "password")
    private String password;

    //Роль аккаунта
    @Column(name = "role")
    private String role;

    //Список заказов человека
    @OneToMany(mappedBy = "person")
    private List<Order> orders;

    public Person() {
    }

    public Person(int id, String name, String email, String login, String password, String role, List<Order> orders) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.login = login;
        this.password = password;
        this.role = role;
        this.orders = orders;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public List<Order> getOrders() {
        return orders;
    }

    public void setOrders(List<Order> orders) {
        this.orders = orders;
    }
}
