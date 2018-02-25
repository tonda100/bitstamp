package net.osomahe.bitstamp.entity;

import java.time.ZonedDateTime;


/**
 * @author Antonin Stoklasek
 */
public class BuyTransaction {

    private String orderId;

    private ZonedDateTime dateTime;

    private Double price;

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public ZonedDateTime getDateTime() {
        return dateTime;
    }

    public void setDateTime(ZonedDateTime dateTime) {
        this.dateTime = dateTime;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    @Override
    public String toString() {
        return "BuyTransaction{" +
                "orderId='" + orderId + '\'' +
                ", dateTime=" + dateTime +
                ", price=" + price +
                '}';
    }
}
