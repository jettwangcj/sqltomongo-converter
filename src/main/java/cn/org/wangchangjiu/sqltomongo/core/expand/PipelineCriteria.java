package cn.org.wangchangjiu.sqltomongo.core.expand;

import org.bson.Document;
import org.springframework.data.mongodb.core.query.CriteriaDefinition;

import java.util.Arrays;
import java.util.LinkedHashMap;

/**
 * @Classname PipelineCriteria
 * @Description
 * @Date 2022/11/30 10:51
 * @Created by wangchangjiu
 */
public class PipelineCriteria {

    private LinkedHashMap<String, Object> criteria = new LinkedHashMap<String, Object>();

    public PipelineCriteria expr(Object left, Object right) {
        this.criteria.put("$expr", Arrays.asList(left, right));
        return this;
    }


    public Document toDocument() {
        return new Document(criteria);
    }

}
