package cn.org.wangchangjiu.sqltomongo.core.parser;

import cn.org.wangchangjiu.sqltomongo.core.analyzer.Analyzer;
import cn.org.wangchangjiu.sqltomongo.core.analyzer.AnalyzerBuilder;
import cn.org.wangchangjiu.sqltomongo.core.common.MongoParserResult;
import cn.org.wangchangjiu.sqltomongo.core.common.ParserPartTypeEnum;
import cn.org.wangchangjiu.sqltomongo.core.parser.data.PartSQLParserData;
import cn.org.wangchangjiu.sqltomongo.core.util.SqlCommonUtil;
import com.alibaba.fastjson.JSON;
import net.sf.jsqlparser.statement.select.PlainSelect;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bson.Document;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationOperation;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public class SelectSQLTypeParser {

    private static final Log logger = LogFactory.getLog(SelectSQLTypeParser.class);

    private PartSQLParserBuilder parserBuilder;

    private AnalyzerBuilder analyzerBuilder;

    public SelectSQLTypeParser(PartSQLParserBuilder parserBuilder, AnalyzerBuilder analyzerBuilder){
        this.parserBuilder = parserBuilder;
        this.analyzerBuilder = analyzerBuilder;
    }

    public PartSQLParserData getPartSQLParserData(String parameterSql) {
        PlainSelect plain = SqlCommonUtil.parserSelectSql(parameterSql);
        // 解析后的数据
        PartSQLParserData data = new PartSQLParserData();
        // 第一层查询 不是子查询
        data.setSubQueryLevel(0);
        data.setMajorTableAlias(SqlCommonUtil.getMajorTableAlias(plain));
        data.setMajorTable(SqlCommonUtil.getMajorTable(plain));

        // 并行解析 提高速度
        Stream.of(ParserPartTypeEnum.values()).parallel().forEach(item -> {
            // 开始解析SQL各个部分
            PartSQLParser instance = parserBuilder.getPartSQLParserInstance(item);
            instance.setParserBuilder(parserBuilder);
            instance.proceedData(plain, data);
        });
        return data;
    }

    public MongoParserResult mongoAggregationAnalyzer(PartSQLParserData data){
        // ====== 下面开始 分析 各个部分 构建 Mongo API ============
        List<Document> documents = new ArrayList<>();

        // 使用责任链设计模式开始分析 每个部分 SQL 封装 MongoAPI
        Analyzer analyzer = analyzerBuilder.newAnalyzerInstance();
        analyzer.setAnalyzerBuilder(analyzerBuilder);
        analyzer.analysis(documents, data);

       /* Aggregation aggregation = Aggregation.newAggregation(operations);
        if (logger.isInfoEnabled()) {
            logger.info("build Aggregation : " + JSON.toJSONString(aggregation));
        }*/
        return new MongoParserResult(documents, data.getMajorTable());
    }

}
