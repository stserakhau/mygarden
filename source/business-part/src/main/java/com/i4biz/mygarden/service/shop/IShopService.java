package com.i4biz.mygarden.service.shop;

import com.i4biz.mygarden.domain.shop.Order;
import com.i4biz.mygarden.domain.shop.OrderItem;
import com.i4biz.mygarden.service.IService;
import lombok.Data;

import java.util.List;

public interface IShopService extends IService<Order, Order, Long> {

    OrderRequest createOrder(OrderRequest orderForm);

    @Data
    public static class OrderRequest {
        public Order order;

        public List<OrderItem> items;
    }
}
