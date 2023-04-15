package cn.org.wangchangjiu.sqltomongo.core.analyzer;

import cn.org.wangchangjiu.sqltomongo.core.parser.data.LookUpData;
import cn.org.wangchangjiu.sqltomongo.core.parser.data.MatchData;
import cn.org.wangchangjiu.sqltomongo.core.parser.data.PartSQLParserData;
import org.apache.commons.collections4.CollectionUtils;
import org.bson.Document;

import java.util.*;
import java.util.function.Function;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * @Classname MatchAnalyzer
 * @Description
 * @Date 2022/8/12 11:53
 * @Created by wangchangjiu
 */
public class MatchAnalyzer extends AbstractAnalyzer {


    @Override
    public void proceed(List<Document> documents, PartSQLParserData data) {
        List<LookUpData> joinParser = data.getJoinParser();
        List<MatchData> matchData = data.getMatchData();

        Map<String, LookUpData> lookUpDataMap = new HashMap<>();
        // 别名和表的映射
        if(!CollectionUtils.isEmpty(joinParser)){
            lookUpDataMap.putAll(joinParser.stream().collect(Collectors.toMap(LookUpData::getAlias, Function.identity())));
        }

        // 分析 匹配 过滤  构建 mongo API
        documents.addAll(analysisMatch(data.getMajorTableAlias(), matchData, lookUpDataMap));

    }

    /**
     * 分析 过滤 匹配条件 构建 mongo 过滤 API
     *
     * @param majorTableAlias
     * @param matchData
     * @param lookUpDataMap
     * @return
     */
    protected List<Document> analysisMatch(String majorTableAlias, List<MatchData> matchData, Map<String, LookUpData> lookUpDataMap) {
        List<Document> documents = new ArrayList<>();
        Deque<Document> stack = new ArrayDeque<>();
        if (!CollectionUtils.isEmpty(matchData)) {
            // matchData 已经安装优先级排序了 这里不需要处理优先级
            matchData.stream().forEach(matchDataItem -> {
                if (!matchDataItem.getIsOperator()) {
                    // 不是操作符 入栈
                    MatchData.RelationExpressionItem expression = (MatchData.RelationExpressionItem) matchDataItem.getExpression();
                    String operator = expression.getOperator();
                    String field = expression.getField();
                    String tableAlias = expression.getTableAlias();
                    Object paramValue = expression.getValue();
                    LookUpData lookUpData = lookUpDataMap.get(tableAlias);

                    if (!majorTableAlias.equals(tableAlias) && lookUpData != null) {
                        field = lookUpData.getAs().concat(".").concat(field);
                    }

                    Document where = new Document();
                    if ("=".equals(operator)) {
                        where.append(field, paramValue);
                    } else if ("LIKE".equalsIgnoreCase(operator)) {
                        Pattern pattern = Pattern.compile("^.*" + paramValue + ".*$", 2);
                        where.append(field, new Document("$regex", pattern));
                    } else if ("IN".equalsIgnoreCase(operator)) {
                        if (paramValue instanceof Collection) {
                            Collection collection = (Collection) paramValue;
                            where.append(field, new Document("$in", collection.toArray(new Object[collection.size()])));
                        }
                    } else if ("<".equals(operator)) {
                        where.append(field, new Document("$lt", paramValue));
                    } else if ("<=".equals(operator)) {
                        where.append(field, new Document("$lte", paramValue));
                    } else if (">".equals(operator)) {
                        where.append(field, new Document("$gt", paramValue));
                    } else if (">=".equals(operator)) {
                        where.append(field, new Document("$gte", paramValue));
                    } else if ("!=".equals(operator) || "<>".equals(operator)) {
                        where.append(field, new Document("$ne", paramValue));
                    }
                    stack.push(where);
                } else {
                    MatchData.OperatorExpressionItem expression = MatchData.OperatorExpressionItem.class.cast(matchDataItem.getExpression());
                    // 操作符 取出两个 操作数 来计算
                    Document left = stack.pop();
                    Document right = stack.pop();
                    Document result = null;
                    if ("AND".equalsIgnoreCase(expression.getOperatorName())) {
                        result = new Document("$and", Arrays.asList(left, right));
                    } else if ("OR".equalsIgnoreCase(expression.getOperatorName())) {
                        result = new Document("$or", Arrays.asList(left, right));
                    }
                    stack.push(result);
                }
            });
        }

        if (!stack.isEmpty()) {
            Document matchDoc = stack.getFirst();
            // $match
            documents.add(new Document("$match", matchDoc));
        }
        return documents;
    }

}
