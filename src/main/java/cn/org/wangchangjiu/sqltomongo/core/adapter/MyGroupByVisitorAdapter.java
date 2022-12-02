package cn.org.wangchangjiu.sqltomongo.core.adapter;

import cn.org.wangchangjiu.sqltomongo.core.exception.SqlParserException;
import cn.org.wangchangjiu.sqltomongo.core.parser.data.GroupData;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.schema.Column;
import net.sf.jsqlparser.statement.select.GroupByElement;
import net.sf.jsqlparser.statement.select.GroupByVisitor;

import java.util.List;
import java.util.Objects;

/**
 * @Classname MyGroupByVisitorAdapter
 * @Description
 * @Date 2022/11/23 18:03
 * @Created by wangchangjiu
 */
public class MyGroupByVisitorAdapter implements GroupByVisitor {

    private final List<GroupData> groupData;

    public MyGroupByVisitorAdapter(List<GroupData> groupData){
        this.groupData = groupData;
    }

    @Override
    public void visit(GroupByElement element) {
        if ( Objects.nonNull(element)) {
            List<Expression> groupByExpressions = element.getGroupByExpressions();
            for (Expression groupByExpression : groupByExpressions) {
                if (groupByExpression instanceof Column) {
                    Column column = (Column) (groupByExpression);
                    // 分组字段的表别名
                    String tableAlias = column.getTable() == null ? "" : column.getTable().getName();
                    groupData.add(new GroupData(tableAlias, column.getColumnName()));
                } else {
                    throw new SqlParserException("group by 后面只能是列");
                }
            }
        }
    }
}
