package cn.org.wangchangjiu.sqltomongo.core.parser;

import cn.org.wangchangjiu.sqltomongo.core.adapter.MyFromItemVisitorAdapter;
import cn.org.wangchangjiu.sqltomongo.core.parser.data.LookUpData;
import cn.org.wangchangjiu.sqltomongo.core.parser.data.PartSQLParserData;
import net.sf.jsqlparser.statement.select.FromItem;
import net.sf.jsqlparser.statement.select.Join;
import net.sf.jsqlparser.statement.select.PlainSelect;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;

public class JoinSQLParser extends AbstractPartSQLParser  {

    @Override
    public void proceedData(PlainSelect plainSelect, PartSQLParserData partSQLParserData) {
        // JOIN
        List<Join> joins = plainSelect.getJoins();
        if(!CollectionUtils.isEmpty(joins)){
            List<LookUpData> lookUpData = new ArrayList<>();
            for (Join join : joins) {
                FromItem rightItem = join.getRightItem();
                LookUpData data = new LookUpData();
                data.setJoinType(join.isLeft() ? LookUpData.JoinType.LEFT_JOIN : join.isInner() ? LookUpData.JoinType.INNER_JOIN : null);
                rightItem.accept(new MyFromItemVisitorAdapter(data, join,
                        StringUtils.isEmpty(partSQLParserData.getMajorTableAlias()) ?
                                partSQLParserData.getMajorTable() : partSQLParserData.getMajorTableAlias(),
                        partSQLParserData.getSubQueryLevel(), super.builder));
                lookUpData.add(data);
            }
            partSQLParserData.setJoinParser(lookUpData);
        }

    }
}
