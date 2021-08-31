package com.i4biz.mygarden.dao.shop;

import com.i4biz.mygarden.dao.GenericDAOImpl;
import com.i4biz.mygarden.domain.shop.OrderItem;
import org.springframework.stereotype.Repository;

@Repository()
public class OrderItemDAO extends GenericDAOImpl<OrderItem, OrderItem, Long> implements IOrderItemDAO {
}
