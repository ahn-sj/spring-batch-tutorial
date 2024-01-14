package org.tally.batchtutorial.job.core.domain.account;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.tally.batchtutorial.job.core.domain.order.Order;

import java.time.LocalDate;

@Entity
@Table(name = "accounts")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Account {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String orderItem;
    private int price;
    private LocalDate orderDate;
    private LocalDate accountDate;

    public Account(final Order order) {
        this.id = order.getId();
        this.orderItem = order.getOrderItem();
        this.price = order.getPrice();
        this.orderDate = order.getOrderDate();
        this.accountDate = LocalDate.now();
    }

}
