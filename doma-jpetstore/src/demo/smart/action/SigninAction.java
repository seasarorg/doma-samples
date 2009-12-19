package demo.smart.action;

import javax.annotation.Resource;

import org.seasar.struts.annotation.ActionForm;
import org.seasar.struts.annotation.Execute;
import org.seasar.struts.exception.ActionMessagesException;

import demo.session.User;
import demo.smart.entity.Account;
import demo.smart.form.SigninForm;
import demo.smart.service.AccountService;
import demo.smart.session.Cart;
import demo.smart.session.PurchaseOrder;
import demo.util.ExternalContextUtil;

public class SigninAction {

    protected AccountService accountService = new AccountService();

    @ActionForm
    @Resource
    protected SigninForm signinForm;

    // out
    public Account account;

    @Execute(validator = false)
    public String signinForm() {
        return "signinForm.jsp";
    }

    @Execute(validator = true, input = "signinForm.jsp")
    public String signin() {
        account = accountService.getAccount(signinForm.username,
                signinForm.password);
        if (account == null) {
            throw new ActionMessagesException(
                    "Invalid username or password. Signin failed.", false);
        }

        Cart cart = Cart.get();
        PurchaseOrder purchaseOrder = PurchaseOrder.get();

        ExternalContextUtil.invalidateSession();

        User user = new User();
        user.setUsername(account.username);
        user.setFirstName(account.firstName);
        user.setAuthenticated(true);
        User.put(user);
        Cart.put(cart);
        PurchaseOrder.put(purchaseOrder);
        return "/";
    }

    @Execute(validator = false)
    public String signout() {
        ExternalContextUtil.invalidateSession();
        return "/";
    }
}
