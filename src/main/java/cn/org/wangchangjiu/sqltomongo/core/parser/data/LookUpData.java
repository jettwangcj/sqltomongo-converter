package cn.org.wangchangjiu.sqltomongo.core.parser.data;

import cn.org.wangchangjiu.sqltomongo.core.common.ConversionFunction;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LookUpData implements Serializable {

    /** 要连接的目标表 **/
    private String table;
    /**
     *  连接表别名
     */
    private String alias;

    /**
     *  join 类型
     */
    private JoinType joinType;


    /**
     *  当连接查询查询出来之后，外表的查询结果
     */
    private String as;

    private Let let;

    /**
     *  知否是子查询
     */
    private boolean subSelect;

    /**
     *  子查询数据
     */
    private PartSQLParserData partSQLParserData;

    /**
     *  join 类型
     */
    public enum JoinType {

        LEFT_JOIN,

        INNER_JOIN

    }

    @Data
    public static class Let {

        /**
         *  作为连接参照的本表字段
         */
        private String localField;

        /**
         *  作为连接参照的目标表字段
         */
        private String foreignField;

        /**
         *  字段别名
         */
        private String fieldAlias;

        /**
         *  转换字段
         */
        private String conversionField;

        /**
         *  转换函数 连接条件需要转化
         */
        private ConversionFunction function;

        /**
         * 转换字段是否是 LocalField
         * @return
         */
        public boolean conversionIsLocalField(){
            return function != null && conversionField.equals(localField);
        }

        /**
         *  转换字段是否是 ForeignField
         * @return
         */
        public boolean conversionIsForeignField(){
            return function != null && conversionField.equals(foreignField);
        }
    }
}
