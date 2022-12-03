package cn.org.wangchangjiu.sqltomongo.core.parser;

import cn.org.wangchangjiu.sqltomongo.core.adapter.MyExpressionVisitorAdapter;
import cn.org.wangchangjiu.sqltomongo.core.common.ParserPartTypeEnum;
import cn.org.wangchangjiu.sqltomongo.core.parser.data.MatchData;
import cn.org.wangchangjiu.sqltomongo.core.parser.data.PartSQLParserData;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.statement.select.PlainSelect;
import org.apache.commons.lang3.ObjectUtils;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class HavingSQLParser extends AbstractPartSQLParser {

    private List<MatchData> parser(Expression having) {
        if (ObjectUtils.isNotEmpty(having)) {
            List<MatchData> matchData = new ArrayList<>();
            having.accept(new MyExpressionVisitorAdapter(ParserPartTypeEnum.HAVING, matchData));
            matchData = matchData.stream()
                    .sorted(Comparator.comparing(MatchData::getPriority)
                            .reversed().thenComparing(MatchData::getSort))
                    .collect(Collectors.toList());
            return matchData;
        }
        return new ArrayList<>();
    }


    @Override
    public void proceedData(PlainSelect plain, PartSQLParserData data) {
        List<MatchData> matchData = this.parser(plain.getHaving());
        data.setHavingData(matchData);
    }
}
