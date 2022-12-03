package cn.org.wangchangjiu.sqltomongo.core.analyzer;

import cn.org.wangchangjiu.sqltomongo.core.parser.data.LookUpData;
import cn.org.wangchangjiu.sqltomongo.core.parser.data.PartSQLParserData;
import cn.org.wangchangjiu.sqltomongo.core.parser.data.ProjectData;
import org.apache.commons.lang3.StringUtils;
import org.bson.Document;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @Classname ProjectAnalyzer
 * @Description 投影分析器
 * @Date 2022/8/12 11:54
 * @Created by wangchangjiu
 */
public class ProjectAnalyzer extends AbstractAnalyzer {

    @Override
    public void proceed(List<Document> documents, PartSQLParserData data) {

        String majorTableAlias = data.getMajorTableAlias();
        List<ProjectData> projectData = data.getProjectData();
        List<LookUpData> joinParser = data.getJoinParser();

        // 别名和表的映射
        Map<String, LookUpData> lookUpDataMap = joinParser.stream().collect(Collectors.toMap(LookUpData::getAlias, Function.identity()));

        // 分析投影  构建 mongo API
        documents.addAll(analysisProject(majorTableAlias, projectData, lookUpDataMap));

    }


    /**
     * 分析 投影 构建 投影 Mongo API
     *
     * @param majorTableAlias
     * @param projectData
     * @param lookUpDataMap
     * @return
     */
    private static List<Document> analysisProject(String majorTableAlias, List<ProjectData> projectData,
                                                              Map<String, LookUpData> lookUpDataMap) {
        List<Document> documents = new ArrayList<>();

        Document fieldObject = new Document();
        projectData.stream().forEach(project -> {
            // 字段携带表别名 例如 select t1.id,t2.name from
            if (StringUtils.isNotEmpty(project.getTable())) {
                String table = project.getTable();
                if (table.equals(majorTableAlias)) {
                    // 主表投影
                    fieldObject.append(project.getAlias(), project.getField());

                } else {

                    // 被关联表 需要携带 as （被关联表数据集）
                    LookUpData lookUpData = lookUpDataMap.get(project.getTable());
                    String target = lookUpData == null ? project.getField() : lookUpData.getAs().concat(".").concat(project.getField());
                    fieldObject.append(project.getField(), target);
                }

            } else {
                // 没有 字段携带的 就是单表
                fieldObject.append(project.getAlias(), "$".concat(project.getField()));
            }
        });
        documents.add(new Document("$project", fieldObject));
        return documents;
    }

}
