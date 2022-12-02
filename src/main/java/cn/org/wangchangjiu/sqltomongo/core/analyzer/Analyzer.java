package cn.org.wangchangjiu.sqltomongo.core.analyzer;

import cn.org.wangchangjiu.sqltomongo.core.parser.data.PartSQLParserData;
import org.bson.Document;

import java.util.List;

/**
 * @Classname Analyzer
 * @Description
 * @Date 2022/8/15 16:24
 * @Created by wangchangjiu
 */
public interface Analyzer {

    void setNextAnalyzer(Analyzer checker);

    void analysis(List<Document> documents, PartSQLParserData data);

    void proceed(List<Document> documents, PartSQLParserData data);

    void setAnalyzerBuilder(AnalyzerBuilder builder);

}
