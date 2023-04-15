package cn.org.wangchangjiu.sqltomongo.core.analyzer;

import cn.org.wangchangjiu.sqltomongo.core.expand.LookupLetPipelinesDocument;
import cn.org.wangchangjiu.sqltomongo.core.expand.PipelineCriteria;
import cn.org.wangchangjiu.sqltomongo.core.parser.data.LookUpData;
import cn.org.wangchangjiu.sqltomongo.core.parser.data.PartSQLParserData;
import org.apache.commons.collections4.CollectionUtils;
import org.bson.Document;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @Classname JoinAnalyzer
 * @Description Join 分析器
 * @Date 2022/8/12 11:51
 * @Created by wangchangjiu
 */
public class JoinAnalyzer extends AbstractAnalyzer {

    @Override
    public void proceed(List<Document> documents, PartSQLParserData data) {

        List<LookUpData> joinParser = data.getJoinParser();
        documents.addAll(analysisJoin(joinParser));

    }


    /**
     * 分析 JOIN 构建 mongo 表关联 API
     *
     * @param joinParser
     * @return
     */
    private List<Document> analysisJoin(List<LookUpData> joinParser) {
        List<Document> documents = new ArrayList<>();
        if (!CollectionUtils.isEmpty(joinParser)) {
            joinParser.stream().forEach(join -> {
                if(join.isSubSelect()){
                    // 处理关联表是一个子查询
                    PartSQLParserData partSQLParserData = join.getPartSQLParserData();

                    List<Document> subDocuments = new ArrayList<>();
                    Analyzer analyzer = super.analyzerBuilder.newAnalyzerInstance();
                    analyzer.analysis(subDocuments, partSQLParserData);

                    LookUpData.Let letData = join.getLet();
                    subDocuments.add(buildRelationMatch(letData));

                    LookupLetPipelinesDocument pipelinesDocument = LookupLetPipelinesDocument
                            .LookupLetPipelinesDocumentBuilder.newBuilder()
                            .from(join.getTable())
                            .let(buildLet(letData))
                            .pipeline(subDocuments)
                            .as(join.getAlias());

                    documents.add(pipelinesDocument.toDocument());
                    // 展平
                    documents.add(buildUnwind(join));
                } else {
                    // 处理关联表是一个 单表
                    LookUpData.Let letData = join.getLet();
                    LookupLetPipelinesDocument pipelinesDocument = LookupLetPipelinesDocument
                            .LookupLetPipelinesDocumentBuilder.newBuilder()
                            .from(join.getTable())
                            .let(buildLet(letData))
                            .pipeline(Arrays.asList(buildRelationMatch(letData)))
                            .as(join.getAs());
                    documents.add(pipelinesDocument.toDocument());
                    // 展平
                    documents.add(buildUnwind(join));
                }
            });
        }
        return documents;
    }

    private Document buildUnwind(LookUpData join){
        if (join.getJoinType() == null) {
            return new Document("$unwind", join.getAs());
        }
        Document unwindArgs = new Document();
        unwindArgs.put("path", "$".concat(join.getAs()));
        unwindArgs.put("preserveNullAndEmptyArrays", join.getJoinType() == LookUpData.JoinType.LEFT_JOIN);
        return new Document("$unwind", unwindArgs);
    }

    private Document buildRelationMatch(LookUpData.Let letData) {
        PipelineCriteria pipelineCriteria = new PipelineCriteria();
        if (letData.conversionIsForeignField()) {
            // ForeignField 转换字段
            Document convert = letData.getFunction().convert(letData.getConversionField());
            pipelineCriteria.expr(convert, "$".concat(letData.getFieldAlias()));
        } else {
            pipelineCriteria.expr(letData.getForeignField(), "$".concat(letData.getFieldAlias()));
        }
        return new Document("$match", pipelineCriteria.toDocument());
    }

    private Document buildLet(LookUpData.Let letData) {
        LookupLetPipelinesDocument.LetBuilder letBuilder = LookupLetPipelinesDocument.LetBuilder
                .newBuilder();
        if(letData.conversionIsLocalField()){
            // LocalField 转换字段
            Document convert = letData.getFunction().convert("$".concat(letData.getConversionField()));
            letBuilder.add(letData.getFieldAlias(), convert);
        } else {
            letBuilder.add(letData.getFieldAlias(), "$".concat(letData.getLocalField()));
        }
        return letBuilder.build();
    }
}
