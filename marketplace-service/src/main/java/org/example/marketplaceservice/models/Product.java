package org.example.marketplaceservice.models;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Table;
import jakarta.persistence.Id;
import jakarta.persistence.Column;


//Сущность Product, хранит о себе информацию о товаре на складе и его наличии
@Entity
@Table(name = "Product")
public class Product {

    //Id товара
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    //Имя товара
    @Column(name = "name")
    private String name;

    //Цена товара
    @Column(name = "price")
    private int price;

    //Количество товара
    @Column(name = "count")
    private int count;

    //Наличие товара на складе
    @Column(name = "iscount")
    private boolean isCount;

    public Product() {
    }

    public Product(int id, String name, int price, int count, boolean isCount) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.count = count;
        this.isCount = isCount;
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

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public boolean getIsCount() {
        return isCount;
    }

    public void setIsCount(boolean count) {
        isCount = count;
    }

}
