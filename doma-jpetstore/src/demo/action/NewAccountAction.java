package demo.action;

import java.util.List;

import javax.annotation.Resource;

import org.apache.struts.action.ActionMessages;
import org.seasar.framework.beans.util.Beans;
import org.seasar.struts.annotation.ActionForm;
import org.seasar.struts.annotation.Execute;

import demo.entity.Account;
import demo.form.NewAccountForm;
import demo.service.AccountService;
import demo.util.TokenUtil;

public class NewAccountAction {

    protected AccountService accountService = new AccountService();

    @ActionForm
    @Resource
    protected NewAccountForm newAccountForm;

    // out
    public List<String> languageList = Constants.LANGUAGE_LIST;

    // out
    public List<String> categoryList = Constants.CATEGORY_LIST;

    // out
    public Account account;

    @Execute(validator = false)
    public String newAccountForm() {

        TokenUtil.save();

        return "newAccountForm.jsp";
    }

    @Execute(validator = true, validate = "validateToken", input = "newAccountForm.jsp")
    public String newAccount() {
        account = new Account();
        Beans.copy(newAccountForm, account).execute();
        try {
            accountService.insertAccount(account);
            return "/signin/signinForm?redirect=true";
        } catch (Exception e) {
            throw new RuntimeException(
                    "There was a problem creating your Account Information.  Cause: "
                            + e, e);
        }
    }

    public ActionMessages validateToken() {
        return TokenUtil.validate();
    }

}
