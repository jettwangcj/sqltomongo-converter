package cn.org.wangchangjiu.sqltomongo.core.adapter;

import cn.org.wangchangjiu.sqltomongo.core.parser.data.ProjectData;
import net.sf.jsqlparser.expression.operators.relational.ItemsListVisitorAdapter;

import java.util.List;

/**
 * @Classname MyItemsListVisitorAdapter
 * @Description
 * @Date 2022/11/23 17:51
 * @Created by wangchangjiu
 */
public class MyItemsListVisitorAdapter extends ItemsListVisitorAdapter {

    private final List<ProjectData> projectData;

    public MyItemsListVisitorAdapter(List<ProjectData> projectData){
        this.projectData = projectData;
    }

}
