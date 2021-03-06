/*
 * Copyright 2004-2010 the Seasar Foundation and the Others.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific language
 * governing permissions and limitations under the License.
 */
package demo.smart.action;

import java.util.List;

import javax.annotation.Resource;

import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;
import org.seasar.struts.annotation.ActionForm;
import org.seasar.struts.annotation.Execute;
import org.seasar.struts.exception.ActionMessagesException;
import org.seasar.struts.util.ActionMessagesUtil;

import demo.action.Authorize;
import demo.smart.entity.Order;
import demo.smart.entity.OrderLineItem;
import demo.smart.form.OrderForm;
import demo.smart.service.OrderService;
import demo.smart.session.Cart;
import demo.smart.session.PurchaseOrder;
import demo.smart.session.User;
import demo.smart.util.ExternalContextUtil;
import demo.smart.util.TokenUtil;

@Authorize
public class OrderAction {

    @Resource
    protected OrderService orderService;

    @ActionForm
    @Resource
    protected OrderForm orderForm;

    // out
    public Order order;

    // out
    public List<OrderLineItem> lineItems;

    // out
    public List<Order> orderList;

    @Execute(validator = false)
    public String listOrders() {
        User user = User.get();
        orderList = orderService.getOrdersByUsername(user.getUsername());
        return "listOrders.jsp";
    }

    @Execute(urlPattern = "viewOrder/{orderId}", validator = true, input = "listOrders")
    public String viewOrder() {
        int orderId = Integer.valueOf(orderForm.orderId);
        order = orderService.getOrder(orderId);
        lineItems = orderService.getOrderLineItems(orderId);

        if (order == null || lineItems == null) {
            throw new ActionMessagesException(String.format(
                    "orderId[%s] is not found.", orderId), false);
        }

        User user = User.get();
        if (user.isAuthorized() && user.getUsername().equals(order.username)) {
            return "viewOrder.jsp";
        }
        throw new ActionMessagesException("You may only view your own orders.",
                false);
    }

    @Execute(validator = false, validate = "validateToken", input = "confirmOrderForm.jsp")
    public String confirm() {
        PurchaseOrder purchaseOrder = PurchaseOrder.get();
        Order order = purchaseOrder.getOrder();
        List<OrderLineItem> lineItems = purchaseOrder.getLineItems();
        if (order == null || lineItems == null) {
            throw new ActionMessagesException(
                    "An error occurred processing your order.", false);
        }
        orderService.insertOrder(order, lineItems);
        Cart.clear();

        ActionMessages messages = new ActionMessages();
        messages.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage(
                "Thank you, your order has been submitted.", false));
        ActionMessagesUtil.addMessages(ExternalContextUtil.getSession(),
                messages);
        return "/";
    }

    public ActionMessages validateToken() {
        return TokenUtil.validate();
    }

}