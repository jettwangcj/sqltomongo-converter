package cn.org.wangchangjiu.sqltomongo.core.analyzer;

import cn.org.wangchangjiu.sqltomongo.core.parser.data.LimitData;
import cn.org.wangchangjiu.sqltomongo.core.parser.data.PartSQLParserData;
import org.apache.commons.lang3.ObjectUtils;
import org.bson.Document;

import java.util.List;

/**
 * @Classname SortAnalyzer
 * @Description
 * @Date 2022/8/12 11:55
 * @Created by wangchangjiu
 */
public class LimitAnalyzer extends AbstractAnalyzer {

    @Override
    public void proceed(List<Document> documents, PartSQLParserData data) {
        LimitData limitData = data.getLimitData();

        // 分页  构建 mongo API
        if (ObjectUtils.isNotEmpty(limitData)) {
            Document skipDoc = new Document();
            skipDoc.append("$skip", limitData.getOffsetValue() * limitData.getRowCount());
            documents.add(skipDoc);

            Document limitDoc = new Document();
            limitDoc.append("$limit", limitData.getRowCount());
            documents.add(limitDoc);
        }
    }


}
