package cn.org.wangchangjiu.sqltomongo.core.parser;

import cn.org.wangchangjiu.sqltomongo.core.adapter.MyExpressionVisitorAdapter;
import cn.org.wangchangjiu.sqltomongo.core.common.ParserPartTypeEnum;
import cn.org.wangchangjiu.sqltomongo.core.parser.data.MatchData;
import cn.org.wangchangjiu.sqltomongo.core.parser.data.PartSQLParserData;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.statement.select.PlainSelect;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class WhereSQLParser extends AbstractPartSQLParser {

    @Override
    public void proceedData(PlainSelect plainSelect, PartSQLParserData partSQLParserData) {
        // Where
        Expression where = plainSelect.getWhere();
        if(where != null){
            List<MatchData> items = new ArrayList<>();
            where.accept(new MyExpressionVisitorAdapter(ParserPartTypeEnum.WHERE, items));
            items = items.stream()
                    .sorted(Comparator.comparing(MatchData::getPriority)
                            .reversed().thenComparing(MatchData::getSort))
                    .collect(Collectors.toList());
            partSQLParserData.setMatchData(items);
        }

    }
}
