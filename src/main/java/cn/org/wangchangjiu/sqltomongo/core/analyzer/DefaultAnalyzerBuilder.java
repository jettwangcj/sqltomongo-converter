package cn.org.wangchangjiu.sqltomongo.core.analyzer;

/**
 * @Classname DefaultAnalyzerBuilder
 * @Description
 * @Date 2022/12/2 17:56
 * @Created by wangchangjiu
 */
public class DefaultAnalyzerBuilder implements AnalyzerBuilder {

    @Override
    public Analyzer newAnalyzerInstance() {
        return new AbstractAnalyzer.Builder()
                .addAnalyzer(new JoinAnalyzer())
                .addAnalyzer(new MatchAnalyzer())
                .addAnalyzer(new GroupAnalyzer())
                .addAnalyzer(new HavingAnalyzer())
                .addAnalyzer(new SortAnalyzer())
                .addAnalyzer(new LimitAnalyzer())
                .addAnalyzer(new ProjectAnalyzer())
                .build();
    }
}
