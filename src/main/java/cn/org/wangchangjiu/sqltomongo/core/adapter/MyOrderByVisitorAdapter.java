package cn.org.wangchangjiu.sqltomongo.core.adapter;

import cn.org.wangchangjiu.sqltomongo.core.common.SortDirection;
import cn.org.wangchangjiu.sqltomongo.core.exception.SqlParserException;
import cn.org.wangchangjiu.sqltomongo.core.parser.data.SortData;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.schema.Column;
import net.sf.jsqlparser.statement.select.OrderByElement;
import net.sf.jsqlparser.statement.select.OrderByVisitor;

import java.util.List;

/**
 * @Classname MyOrderByVisitorAdapter
 * @Description
 * @Date 2022/11/23 18:09
 * @Created by wangchangjiu
 */
public class MyOrderByVisitorAdapter implements OrderByVisitor {

    private final List<SortData> sortData;

    public MyOrderByVisitorAdapter(List<SortData> sortData){
        this.sortData = sortData;
    }

    @Override
    public void visit(OrderByElement orderByElement) {
        Expression expression = orderByElement.getExpression();
        if (expression instanceof Column) {
            Column column = (Column) (expression);
            SortDirection direction =  orderByElement.isAsc() ?  SortDirection.ASC : SortDirection.DESC;
            sortData.add(new SortData(direction, column.getColumnName()));
        } else {
            throw new SqlParserException("order by 后面只能是列");
        }
    }
}
