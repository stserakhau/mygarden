package com.i4biz.mygarden.dao.shop;

import com.i4biz.mygarden.dao.GenericDAOImpl;
import com.i4biz.mygarden.domain.shop.Order;
import org.springframework.stereotype.Repository;

@Repository()
public class OrderDAO extends GenericDAOImpl<Order, Order, Long> implements IOrderDAO {
}
