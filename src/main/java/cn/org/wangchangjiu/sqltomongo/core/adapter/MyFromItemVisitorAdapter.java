package cn.org.wangchangjiu.sqltomongo.core.adapter;

import cn.org.wangchangjiu.sqltomongo.core.parser.PartSQLParserBuilder;
import cn.org.wangchangjiu.sqltomongo.core.parser.data.LookUpData;
import cn.org.wangchangjiu.sqltomongo.core.parser.data.PartSQLParserData;
import cn.org.wangchangjiu.sqltomongo.core.util.SqlSupportedSyntaxCheckUtil;
import lombok.extern.slf4j.Slf4j;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.schema.Table;
import net.sf.jsqlparser.statement.select.FromItemVisitorAdapter;
import net.sf.jsqlparser.statement.select.Join;
import net.sf.jsqlparser.statement.select.SelectBody;
import net.sf.jsqlparser.statement.select.SubSelect;

/**
 * @Classname MyFromItemVisitorAdapter
 * @Description
 * @Date 2022/11/23 17:30
 * @Created by wangchangjiu
 */
public class MyFromItemVisitorAdapter extends FromItemVisitorAdapter {

    private final LookUpData data;

    private final Join join;

    private final String majorTable;

    private Integer subQueryLevel;

    private PartSQLParserBuilder builder;

    public MyFromItemVisitorAdapter(LookUpData data, Join join, String majorTable, Integer subQueryLevel, PartSQLParserBuilder builder){
        this.data = data;
        this.join = join;
        this.majorTable = majorTable;
        this.subQueryLevel = subQueryLevel;
        this.builder = builder;
    }

    @Override
    public void visit(Table table) {

        // 检查 表关联时 只支持 等值匹配并且匹配条件只有一个
        SqlSupportedSyntaxCheckUtil.checkTableAssociationCondition(join.getOnExpressions());
        // 根据 外键关联  所以条件只有一个
        Expression onExpression = join.getOnExpression();
        data.setTable(table.getName());
        data.setAlias(table.getAlias() == null ? "" : table.getAlias().getName());

        onExpression.accept(new MyJoinExpressionVisitorAdapter(data, majorTable));

    }

    @Override
    public void visit(SubSelect subSelect) {
        // select    SelectItem   from   FromItem   where   Expression
        // 子查询 别名
        String aliasName = subSelect.getAlias().getName();
        data.setTable(aliasName);
        data.setAlias(aliasName);
        data.setSubSelect(true);

        // 检查 表关联时 只支持 等值匹配并且匹配条件只有一个
        SqlSupportedSyntaxCheckUtil.checkTableAssociationCondition(join.getOnExpressions());
        Expression onExpression = join.getOnExpression();
        onExpression.accept(new MyJoinExpressionVisitorAdapter(data, majorTable));

        // 处理子查询
        SelectBody selectBody = subSelect.getSelectBody();
        PartSQLParserData parserData = new PartSQLParserData();
        // 子查询深度加一
        parserData.setSubQueryLevel(this.subQueryLevel + 1);
        selectBody.accept(new MySelectVisitorAdapter(parserData, builder));
        data.setTable(parserData.getMajorTable());
        data.setPartSQLParserData(parserData);

        data.setAs(aliasName);
    }




}
