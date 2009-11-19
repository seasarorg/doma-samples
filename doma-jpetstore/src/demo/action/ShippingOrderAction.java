package demo.action;

import javax.annotation.Resource;

import org.seasar.framework.beans.util.Beans;
import org.seasar.struts.annotation.ActionForm;
import org.seasar.struts.annotation.Execute;
import org.seasar.struts.exception.ActionMessagesException;

import demo.entity.Order;
import demo.form.ShippingOrderForm;
import demo.session.Cart;
import demo.session.PurchaseOrder;
import demo.session.User;

public class ShippingOrderAction {

    @ActionForm
    @Resource
    protected ShippingOrderForm shippingOrderForm;

    // out
    public Order order;

    @Execute(validator = false, input = "/account/signinForm")
    public String newOrderForm() {
        User user = User.get();
        Cart cart = Cart.get();

        if (!user.isAuthenticated()) {
            throw new ActionMessagesException(
                    "You must sign on before attempting to check out.  Please sign on and try checking out again.",
                    false);
        }
        if (cart.getCartItemList().isEmpty()) {
            throw new ActionMessagesException(
                    "You must sign on before attempting to check out.  Please sign on and try checking out again.",
                    false);
        }

        PurchaseOrder purchaseOrder = PurchaseOrder.get();
        order = purchaseOrder.getOrder();
        Beans.copy(order, shippingOrderForm).execute();

        return "shippingOrderForm.jsp";
    }

    @Execute(validator = true, input = "shippingOrderForm.jsp")
    public String continueOrder() {
        PurchaseOrder purchaseOrder = PurchaseOrder.get();
        order = purchaseOrder.getOrder();
        if (order == null) {
            throw new ActionMessagesException(
                    "An error occurred processing your order.", false);
        }
        Beans.copy(shippingOrderForm, order).excludesNull()
                .excludesWhitespace().execute();
        PurchaseOrder.put(purchaseOrder);
        return "/order/confirmOrder.jsp";
    }

}