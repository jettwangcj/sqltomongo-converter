package cn.org.wangchangjiu.sqltomongo.core.parser;

import cn.org.wangchangjiu.sqltomongo.core.common.ParserPartTypeEnum;

/**
 * @Classname DefaultPartSQLParserBuilder
 * @Description
 * @Date 2022/12/1 17:06
 * @Created by wangchangjiu
 */
public class DefaultPartSQLParserBuilder implements PartSQLParserBuilder {
    @Override
    public PartSQLParser getPartSQLParserInstance(ParserPartTypeEnum typeEnum) {
        return ParserPartTypeEnum.buildNoPlugin(typeEnum);
    }
}
