package com.i4biz.mygarden.dao.shop;

import com.i4biz.mygarden.dao.GenericDAOImpl;
import com.i4biz.mygarden.domain.shop.OrderTransaction;
import org.springframework.stereotype.Repository;

@Repository()
public class OrderTransactionDAO extends GenericDAOImpl<OrderTransaction, OrderTransaction, Long> implements IOrderTransactionDAO {
}
