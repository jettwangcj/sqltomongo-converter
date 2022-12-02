package cn.org.wangchangjiu.sqltomongo.core.parser;

import cn.org.wangchangjiu.sqltomongo.core.adapter.MyGroupByVisitorAdapter;
import cn.org.wangchangjiu.sqltomongo.core.parser.data.GroupData;
import cn.org.wangchangjiu.sqltomongo.core.parser.data.PartSQLParserData;
import net.sf.jsqlparser.statement.select.GroupByElement;
import net.sf.jsqlparser.statement.select.PlainSelect;

import java.util.ArrayList;
import java.util.List;

public class GroupSQLParser extends AbstractPartSQLParser {

    @Override
    public void proceedData(PlainSelect plain, PartSQLParserData data) {
        GroupByElement groupBy = plain.getGroupBy();
        if(groupBy != null){
            List<GroupData> groupData = new ArrayList<>();
            groupBy.accept(new MyGroupByVisitorAdapter(groupData));
            data.setGroupData(groupData);
        }
    }
}
