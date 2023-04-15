package cn.org.wangchangjiu.sqltomongo.core.analyzer;

import cn.org.wangchangjiu.sqltomongo.core.parser.data.PartSQLParserData;
import cn.org.wangchangjiu.sqltomongo.core.parser.data.SortData;
import org.apache.commons.collections4.CollectionUtils;
import org.bson.Document;

import java.util.List;

/**
 * @Classname SortAnalyzer
 * @Description
 * @Date 2022/8/12 11:55
 * @Created by wangchangjiu
 */
public class SortAnalyzer extends AbstractAnalyzer {

    @Override
    public void proceed(List<Document> documents, PartSQLParserData data) {
        List<SortData> sortData = data.getSortData();
        // 分析 排序  构建 mongo API
        if (!CollectionUtils.isEmpty(sortData)) {
            Document object = new Document();
            sortData.stream().forEach(sort -> object.put(sort.getField(), sort.getDirection().isAscending() ? 1 : -1));
            documents.add(new Document("$sort", object));
        }
    }


}
