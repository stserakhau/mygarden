package com.i4biz.mygarden.service.shop;


import com.i4biz.mygarden.dao.GenericDAO;
import com.i4biz.mygarden.dao.shop.IOrderDAO;
import com.i4biz.mygarden.dao.shop.IOrderItemDAO;
import com.i4biz.mygarden.dao.shop.IOrderTransactionDAO;
import com.i4biz.mygarden.domain.shop.Order;
import com.i4biz.mygarden.domain.shop.OrderItem;
import com.i4biz.mygarden.service.AbstractService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ShopService extends AbstractService<Order, Order, Long> implements IShopService {
    @Autowired
    IOrderDAO orderDAO;

    @Autowired
    IOrderItemDAO orderItemDAO;

    @Autowired
    IOrderTransactionDAO orderTransactionDAO;

    @Override
    protected GenericDAO<Order, Order, Long> getServiceDAO() {
        return orderDAO;
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public OrderRequest createOrder(OrderRequest orderForm) {
        Order o = orderForm.order;
        orderDAO.saveOrUpdate(o);

        for (OrderItem oi : orderForm.items) {
            oi.setOrderId(o.getId());
            orderItemDAO.saveOrUpdate(oi);
        }

        return orderForm;
    }
}
