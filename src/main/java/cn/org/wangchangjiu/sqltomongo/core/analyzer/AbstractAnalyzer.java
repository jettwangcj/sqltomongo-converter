package cn.org.wangchangjiu.sqltomongo.core.analyzer;

import cn.org.wangchangjiu.sqltomongo.core.parser.data.PartSQLParserData;
import org.bson.Document;

import java.util.List;

/**
 * @Classname Analyzer
 * @Description
 * @Date 2022/8/12 11:51
 * @Created by wangchangjiu
 */
public abstract class AbstractAnalyzer implements Analyzer {

    protected AnalyzerBuilder analyzerBuilder;

    protected Analyzer checker = null;

    @Override
    public void setNextAnalyzer(Analyzer checker) {
        this.checker = checker;
    }

    @Override
    public void analysis(List<Document> documents, PartSQLParserData data) {
        this.proceed(documents, data);
        if (this.checker != null) {
            // 责任链设计模式 ， 传递给下一个分析器
            checker.analysis(documents, data);
        }
    }

    @Override
    public abstract void proceed(List<Document> documents, PartSQLParserData data);


    @Override
    public void setAnalyzerBuilder(AnalyzerBuilder builder){
        this.analyzerBuilder = builder;
    }


    public static class Builder {
        private Analyzer head;
        private Analyzer tail;

        // 添加处理者
        public Builder addAnalyzer(Analyzer chain) {
            if (this.head == null) {
                this.head = this.tail = chain;
                return this;
            }
            // 设置下一个处理者
            this.tail.setNextAnalyzer(chain);
            this.tail = chain;
            return this;
        }

        public Analyzer build() {
            return this.head;
        }
    }

}
