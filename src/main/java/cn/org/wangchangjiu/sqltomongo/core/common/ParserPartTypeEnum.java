package cn.org.wangchangjiu.sqltomongo.core.common;

import cn.org.wangchangjiu.sqltomongo.core.parser.*;

/**
 * @Classname ParserPartType
 * @Description
 * @Date 2022/8/12 11:44
 * @Created by wangchangjiu
 */
public enum ParserPartTypeEnum {

    JOIN,
    GROUP,
    WHERE,
    HAVING,
    LIMIT,
    PROJECT,
    ORDER;


    /**
     *  获取解析器 不带插件
     * @param typeEnum
     * @return
     */
    public static PartSQLParser buildNoPlugin (ParserPartTypeEnum typeEnum){
        PartSQLParser partSQLParser = null;
        if (typeEnum == ParserPartTypeEnum.GROUP) {
            partSQLParser = new GroupSQLParser();
        } else if (typeEnum == ParserPartTypeEnum.HAVING) {
            partSQLParser = new HavingSQLParser();
        } else if (typeEnum == ParserPartTypeEnum.LIMIT) {
            partSQLParser = new LimitSQLParser();
        } else if (typeEnum == ParserPartTypeEnum.PROJECT) {
            partSQLParser = new ProjectSQLParser();
        } else if (typeEnum == ParserPartTypeEnum.ORDER) {
            partSQLParser = new OrderSQLParser();
        } else if (typeEnum == ParserPartTypeEnum.JOIN) {
            partSQLParser = new JoinSQLParser();
        } else if (typeEnum == ParserPartTypeEnum.WHERE) {
            partSQLParser = new WhereSQLParser();
        }
        return partSQLParser;
    }
}
