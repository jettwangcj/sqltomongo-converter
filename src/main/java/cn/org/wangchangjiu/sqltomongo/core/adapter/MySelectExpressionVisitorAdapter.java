package cn.org.wangchangjiu.sqltomongo.core.adapter;

import cn.org.wangchangjiu.sqltomongo.core.common.AggregationFunction;
import cn.org.wangchangjiu.sqltomongo.core.parser.data.ProjectData;
import cn.org.wangchangjiu.sqltomongo.core.util.SqlSupportedSyntaxCheckUtil;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.Function;
import net.sf.jsqlparser.expression.operators.relational.ExpressionList;
import net.sf.jsqlparser.schema.Column;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.List;

/**
 * @Classname MyJoinExpressionVisitorAdapter
 * @Description
 * @Date 2022/11/24 15:24
 * @Created by wangchangjiu
 */
public class MySelectExpressionVisitorAdapter extends BaseExpressionVisitorAdapter {

    private final ProjectData data;

    public MySelectExpressionVisitorAdapter(ProjectData data){
        this.data = data;
    }

    @Override
    public void visit(Function function) {
        String functionName = function.getName();
        SqlSupportedSyntaxCheckUtil.checkProjectSupportFunction(functionName);
        ExpressionList parameters = function.getParameters();
        List<Expression> expressions = parameters.getExpressions();
        SqlSupportedSyntaxCheckUtil.checkFunctionColumn(expressions);
        // 解析出 函数 和 字段
        data.setFunction(AggregationFunction.parser(functionName));
        Expression expression = expressions.get(0);
        // 交给处理列的
        expression.accept(this);
    }

    @Override
    public void visit(Column column) {
        String columnName = column.getColumnName();
        if(StringUtils.isEmpty(data.getAlias())){
            data.setAlias(columnName);
        }
        String table = null;
        if (ObjectUtils.isNotEmpty(column.getTable())) {
            table = column.getTable().getName();
        }
        data.setTable(table);
        data.setField(columnName);
    }

}
