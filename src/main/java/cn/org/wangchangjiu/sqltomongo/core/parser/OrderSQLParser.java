package cn.org.wangchangjiu.sqltomongo.core.parser;

import cn.org.wangchangjiu.sqltomongo.core.adapter.MyOrderByVisitorAdapter;
import cn.org.wangchangjiu.sqltomongo.core.parser.data.PartSQLParserData;
import cn.org.wangchangjiu.sqltomongo.core.parser.data.SortData;
import net.sf.jsqlparser.statement.select.OrderByElement;
import net.sf.jsqlparser.statement.select.PlainSelect;
import org.apache.commons.collections4.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

public class OrderSQLParser extends AbstractPartSQLParser {

    @Override
    public void proceedData(PlainSelect plain, PartSQLParserData data) {
        List<OrderByElement> orderByElements = plain.getOrderByElements();
        if (!CollectionUtils.isEmpty(orderByElements)) {
            List<SortData> sortData = new ArrayList<>();
            orderByElements.stream().forEach(orderByElement -> orderByElement.accept(new MyOrderByVisitorAdapter(sortData)));
            data.setSortData(sortData);
        }
    }
}
