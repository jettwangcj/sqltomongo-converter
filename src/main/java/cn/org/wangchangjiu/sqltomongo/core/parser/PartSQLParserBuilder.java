package cn.org.wangchangjiu.sqltomongo.core.parser;

import cn.org.wangchangjiu.sqltomongo.core.common.ParserPartTypeEnum;

/**
 * @Classname PartSQLParserFactory
 * @Description
 * @Date 2022/12/1 16:27
 * @Created by wangchangjiu
 */
public interface PartSQLParserBuilder {

    PartSQLParser getPartSQLParserInstance(ParserPartTypeEnum item);


}
