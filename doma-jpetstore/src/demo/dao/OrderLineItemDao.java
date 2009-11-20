package demo.dao;

import java.util.List;

import org.seasar.doma.BatchInsert;
import org.seasar.doma.Dao;
import org.seasar.doma.Select;

import demo.cool.config.AppConfig;
import demo.entity.OrderLineItem;

@Dao(config = AppConfig.class)
public interface OrderLineItemDao {

    @Select
    List<OrderLineItem> getLineItemsByOrderId(int orderId);

    @BatchInsert
    int[] insertLineItem(List<OrderLineItem> lineItems);
}
