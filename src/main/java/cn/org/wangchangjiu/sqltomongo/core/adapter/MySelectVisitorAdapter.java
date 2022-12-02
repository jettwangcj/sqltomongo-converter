package cn.org.wangchangjiu.sqltomongo.core.adapter;

import cn.org.wangchangjiu.sqltomongo.core.common.ParserPartTypeEnum;
import cn.org.wangchangjiu.sqltomongo.core.parser.PartSQLParserBuilder;
import cn.org.wangchangjiu.sqltomongo.core.parser.data.PartSQLParserData;
import cn.org.wangchangjiu.sqltomongo.core.util.SqlCommonUtil;
import net.sf.jsqlparser.statement.select.PlainSelect;
import net.sf.jsqlparser.statement.select.SelectVisitorAdapter;

import java.util.stream.Stream;

/**
 *  select   SelectItemVisitor   from   FromItemVisitor   where   ExpressionVisitor
 * @Classname MySelectVisitorAdapter
 * @Description 处理整个SQL
 * @Date 2022/11/24 10:44
 * @Created by wangchangjiu
 */
public class MySelectVisitorAdapter extends SelectVisitorAdapter {

    private final PartSQLParserData partSQLParserData;

    private final PartSQLParserBuilder builder;

    public MySelectVisitorAdapter(PartSQLParserData partSQLParserData, PartSQLParserBuilder builder){
        this.partSQLParserData = partSQLParserData;
        this.builder = builder;
    }

    @Override
    public void visit(PlainSelect plainSelect) {
        // select   SelectItem   from   FromItem   where   Expression
        partSQLParserData.setMajorTableAlias(SqlCommonUtil.getMajorTableAlias(plainSelect));
        partSQLParserData.setMajorTable(SqlCommonUtil.getMajorTable(plainSelect));
        Stream.of(ParserPartTypeEnum.values()).parallel().forEach(item -> {
            // 开始解析SQL各个部分
            builder.getPartSQLParserInstance(item).proceedData(plainSelect, partSQLParserData);
        });
    }

}
