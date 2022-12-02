package cn.org.wangchangjiu.sqltomongo.core.parser;

import cn.org.wangchangjiu.sqltomongo.core.parser.data.LimitData;
import cn.org.wangchangjiu.sqltomongo.core.parser.data.PartSQLParserData;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.JdbcNamedParameter;
import net.sf.jsqlparser.expression.LongValue;
import net.sf.jsqlparser.statement.select.Limit;
import net.sf.jsqlparser.statement.select.PlainSelect;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.Objects;

public class LimitSQLParser extends AbstractPartSQLParser {

    private static final Log logger = LogFactory.getLog(LimitSQLParser.class);

    public LimitData parser(Limit limit) {
        if (Objects.nonNull(limit)) {
            Expression offset = limit.getOffset();
            Expression rowCount = limit.getRowCount();

            Integer offsetValue = null;
            if(offset instanceof LongValue){
                offsetValue = Integer.valueOf(LongValue.class.cast(offset).getStringValue());
            }
            if(offset instanceof JdbcNamedParameter){
                offsetValue = Integer.valueOf(JdbcNamedParameter.class.cast(offset).getName());
            }

            Integer rowCountValue = null;
            if(rowCount instanceof LongValue){
                rowCountValue = Integer.valueOf(LongValue.class.cast(rowCount).getStringValue());
            }
            if(rowCount instanceof JdbcNamedParameter){
                rowCountValue = Integer.valueOf(JdbcNamedParameter.class.cast(rowCount).getName());
            }


            if (offsetValue != null && rowCountValue != null) {
                return new LimitData(offsetValue, rowCountValue);
            } else {
                if(logger.isWarnEnabled()){
                    logger.warn("offset 或者 rowCount 不是 JdbcNamedParameter 实例，放弃解析 Limit");
                }
            }
        }
        return null;
    }

    @Override
    public void proceedData(PlainSelect plain, PartSQLParserData data) {
        LimitData limitData = this.parser(plain.getLimit());
        data.setLimitData(limitData);
    }
}
