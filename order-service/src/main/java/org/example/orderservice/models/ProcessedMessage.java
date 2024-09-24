package org.example.orderservice.models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "Processed_Messages")
public class ProcessedMessage {

    @Id
    @Column(name = "id")
    private int id;

    public ProcessedMessage(int id) {
        this.id = id;
    }

    public ProcessedMessage() {

    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
