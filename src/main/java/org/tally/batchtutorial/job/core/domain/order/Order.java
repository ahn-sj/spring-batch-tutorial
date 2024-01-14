package org.tally.batchtutorial.job.core.domain.order;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.ToString;

import java.time.LocalDate;

@Entity
@Table(name = "orders")
@ToString
@Getter
public class Order {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String orderItem;
    private int price;
    private LocalDate orderDate;
}
