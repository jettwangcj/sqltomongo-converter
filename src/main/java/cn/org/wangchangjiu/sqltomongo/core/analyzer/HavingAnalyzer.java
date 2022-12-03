package cn.org.wangchangjiu.sqltomongo.core.analyzer;

import cn.org.wangchangjiu.sqltomongo.core.parser.data.LookUpData;
import cn.org.wangchangjiu.sqltomongo.core.parser.data.MatchData;
import cn.org.wangchangjiu.sqltomongo.core.parser.data.PartSQLParserData;
import org.bson.Document;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @Classname HavingAnalyzer
 * @Description
 * @Date 2022/8/12 11:53
 * @Created by wangchangjiu
 */
public class HavingAnalyzer extends MatchAnalyzer {


    @Override
    public void proceed(List<Document> documents, PartSQLParserData data) {

        List<MatchData> havingData = data.getHavingData();
        String majorTableAlias = data.getMajorTableAlias();
        List<LookUpData> joinParser = data.getJoinParser();

        // 别名和表的映射
        Map<String, LookUpData> lookUpDataMap = joinParser.stream().collect(Collectors.toMap(LookUpData::getAlias, Function.identity()));

        // 分析 having
        if (!CollectionUtils.isEmpty(havingData)) {
            documents.addAll(analysisMatch(majorTableAlias, havingData, lookUpDataMap));
        }

    }
}
