package cn.org.wangchangjiu.sqltomongo.core.parser;

/**
 * @Classname AbstractPartSQLParser
 * @Description
 * @Date 2022/12/1 16:54
 * @Created by wangchangjiu
 */
public abstract class AbstractPartSQLParser implements PartSQLParser{

    protected PartSQLParserBuilder builder;

    @Override
    public void setParserBuilder(PartSQLParserBuilder builder){
        this.builder = builder;
    }

}
