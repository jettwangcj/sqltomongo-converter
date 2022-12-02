package cn.org.wangchangjiu.sqltomongo.core.analyzer;

import cn.org.wangchangjiu.sqltomongo.core.common.AggregationFunction;
import cn.org.wangchangjiu.sqltomongo.core.parser.data.*;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.bson.Document;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @Classname GroupAnalyzer
 * @Description
 * @Date 2022/8/12 11:55
 * @Created by wangchangjiu
 */
public class GroupAnalyzer extends AbstractAnalyzer {

    @Override
    public void proceed(List<Document> documents, PartSQLParserData data) {

        // 投影数据
        List<ProjectData> projectData = data.getProjectData();
        // 过滤
        List<MatchData> havingData = data.getHavingData();
        // 分组
        List<GroupData> groupData = data.getGroupData();
        // 关联
        List<LookUpData> joinParser = data.getJoinParser();

        // having 中出现的 聚合函数 也要计算出来  ProjectData 重写 equals 目的是去重
        //  SELECT emp_no from salaries group by emp_no HAVING count(salary)>15
        Set<ProjectData> projectDataSet = new HashSet<>();
        projectDataSet.addAll(projectData);
        if (!CollectionUtils.isEmpty(havingData)) {
            havingData.stream().filter(match -> !match.getIsOperator()).forEach(item -> {
                MatchData.RelationExpressionItem expression = (MatchData.RelationExpressionItem) item.getExpression();
                projectDataSet.add(ProjectData.builder()
                        .table(expression.getTableAlias())
                        .field(expression.getField())
                        .function(expression.getFunction()).build());
            });
        }

        // 别名和表的映射
        Map<String, LookUpData> lookUpDataMap = joinParser.stream().collect(Collectors.toMap(LookUpData::getAlias, Function.identity()));

        // 分析 分组 构建 Mongo API
        Document document = analysisGroup(new ArrayList<>(projectDataSet), groupData, lookUpDataMap);
        if(document != null){
            documents.add(document);
        }

        // 不知道为啥 mongo 里面投影有个问题 只有一个 group by 的话 ，对这个 group by 字段投影会失败 TODO 这里做特殊处理
        if (!CollectionUtils.isEmpty(groupData) && groupData.size() == 1) {
            String field = groupData.get(0).getField();
            projectData.stream().filter(project -> field.equals(project.getField())).findFirst()
                    .ifPresent(project -> project.setField("_id"));
        }
    }

    /**
     * 构建 Mongo 分组 API
     *
     * @param projectData
     * @param groupData
     * @param lookUpDataMap
     * @return
     */
    private static Document analysisGroup(List<ProjectData> projectData, List<GroupData> groupData, Map<String, LookUpData> lookUpDataMap) {

        if (!CollectionUtils.isEmpty(groupData)) {

            List<ProjectData> functionProjectData = projectData.stream().filter(item -> ObjectUtils.isNotEmpty(item.getFunction())).collect(Collectors.toList());

            Map<AggregationFunction, List<ProjectData>> functionProjectDataMap = new HashMap<>();
            if (!CollectionUtils.isEmpty(functionProjectData)) {
                functionProjectDataMap.putAll(functionProjectData.stream().collect(Collectors.groupingBy(ProjectData::getFunction)));
            }

            Document fieldsDoc = new Document();
            groupData.stream().forEach(group -> {

                String field = group.getField();

                if (StringUtils.isNotEmpty(group.getTableAlias())) {
                    // 被关联的表 需要有 结果集 as 的前缀
                    LookUpData lookUpData = lookUpDataMap.get(group.getTableAlias());
                    if (ObjectUtils.isNotEmpty(lookUpData)) {
                        field = lookUpData.getAs().concat(".").concat(field);
                    }
                }
                fieldsDoc.append(field, "$".concat(field));
            });

            Document groupDoc = new Document();
            groupDoc.append("_id", fieldsDoc);

            functionProjectDataMap.forEach((function, projectDataList) -> {
                if (AggregationFunction.SUM == function) {
                    // 求和
                    handleAggregationFunction(lookUpDataMap, projectDataList, (field, alias) -> groupDoc.append(alias, new Document("$sum", "$".concat(field))));
                } else if (AggregationFunction.AVG == function) {
                    // 求平均
                    handleAggregationFunction(lookUpDataMap, projectDataList, (field, alias) ->  groupDoc.append(alias, new Document("$avg", "$".concat(field))));
                } else if (AggregationFunction.MIN == function) {
                    // 最小值
                    handleAggregationFunction(lookUpDataMap, projectDataList, (field, alias) ->  groupDoc.append(alias, new Document("$min", "$".concat(field))));
                } else if (AggregationFunction.MAX == function) {
                    // 最大值
                    handleAggregationFunction(lookUpDataMap, projectDataList, (field, alias) ->  groupDoc.append(alias, new Document("$max", "$".concat(field))));
                } else if (AggregationFunction.FIRST == function) {
                    // 分组和第一个
                    handleAggregationFunction(lookUpDataMap, projectDataList, (field, alias) ->  groupDoc.append(alias, new Document("$first", "$".concat(field))));
                } else if (AggregationFunction.LAST == function) {
                    // 分组 最后一个
                    handleAggregationFunction(lookUpDataMap, projectDataList, (field, alias) ->  groupDoc.append(alias, new Document("$last", "$".concat(field))));
                }
            });
            return new Document("$group",  groupDoc);
        }
        return null;
    }


    private static void handleAggregationFunction(Map<String, LookUpData> lookUpDataMap, List<ProjectData> projectDataList, BiConsumer<String, String> groupOperationBiConsumer) {
        projectDataList.stream().forEach(project -> {
            String field = project.getField();

            if (StringUtils.isNotEmpty(project.getTable())) {
                // 被关联的表 需要有 结果集 as 的前缀
                LookUpData lookUpData = lookUpDataMap.get(project.getTable());
                if (lookUpData != null) {
                    field = lookUpData.getAs().concat(".").concat(field);
                }
            }
            groupOperationBiConsumer.accept(field, field);
        });
    }

}
