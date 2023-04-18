package cn.org.wangchangjiu.sqltomongo.core.parser.data;

import cn.org.wangchangjiu.sqltomongo.core.common.SortDirection;
import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

@Data
@Builder
public class SortData implements Serializable {

    /**
     *  排序
     */
    private SortDirection direction;

    /**
     *  排序字段
     */
    private String field;


    public SortData(){}

    public SortData(SortDirection direction, String field){
        this.direction = direction;
        this.field = field;
    }
}
