package org.example.marketplaceservice.models;


import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Column;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import jakarta.persistence.CascadeType;


import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

//Сущность Order, которая хранит в себе информацию о заказе
@Entity
@Table(name = "orders")
public class Order {

    //Id заказа
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    //Сумма заказа
    @Column(name = "cost")
    private int cost;

    //Человек оформивший заказ
    @ManyToOne
    @JoinColumn(referencedColumnName = "id",name = "person_id")
    private Person person;

    //Продукты в заказе
    @OneToMany(mappedBy = "order",cascade = CascadeType.ALL)
    private List<ProductInOrder> products;

    //Дата создания заказа
    @Column(name = "date_of_create")
    @Temporal(TemporalType.TIMESTAMP)
    private Date dateOfCreate;

    //Статус заказа
    @Column(name = "status")
    private String status;

    public Order() {
    }

    public Order(int id, int cost, Person person, Date dateOfCreate, String status) {
        this.id = id;
        this.cost = cost;
        this.person = person;
        this.dateOfCreate = dateOfCreate;
        this.status = status;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getCost() {
        return cost;
    }

    public void setCost(int cost) {
        this.cost = cost;
    }

    public Person getPerson() {
        return person;
    }
    public void setPerson(Person person) {
        this.person = person;
    }

    public Date getDateOfCreate() {
        return dateOfCreate;
    }

    public void setDateOfCreate(Date dateOfCreate) {
        this.dateOfCreate = dateOfCreate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public List<ProductInOrder> getProducts() {
        return products;
    }

    public void setProducts(List<ProductInOrder> products) {
        this.products = products;
    }

    @Override
    public String toString() {
        return "Order{" +
                "id=" + id +
                ", cost=" + cost +
                ", person=" + person +
                ", products=" + products +
                ", dateOfCreate=" + dateOfCreate +
                ", status='" + status + '\'' +
                '}';
    }
}
