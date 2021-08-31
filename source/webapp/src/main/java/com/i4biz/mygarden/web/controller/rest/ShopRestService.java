package com.i4biz.mygarden.web.controller.rest;

import com.i4biz.mygarden.domain.user.User;
import com.i4biz.mygarden.security.ApplicationPrincipal;
import com.i4biz.mygarden.service.shop.IShopService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.security.Principal;

@Controller
@RequestMapping("/shop")
public class ShopRestService {
    @Autowired
    private IShopService shopService;

    @RequestMapping(value = "/", method = RequestMethod.POST)
    @ResponseBody
    public IShopService.OrderRequest createSpecies(@RequestBody IShopService.OrderRequest orderForm, Principal principal) {
        User user = ((ApplicationPrincipal) ((UsernamePasswordAuthenticationToken) principal).getPrincipal()).getUser();
        Long userId = user.getId();
        orderForm.order.setOwnerId(userId);

        IShopService.OrderRequest order = shopService.createOrder(orderForm);
        return order;
    }
}
