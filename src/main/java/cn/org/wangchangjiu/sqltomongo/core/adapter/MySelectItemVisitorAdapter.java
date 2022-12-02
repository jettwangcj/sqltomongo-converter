package cn.org.wangchangjiu.sqltomongo.core.adapter;

import cn.org.wangchangjiu.sqltomongo.core.parser.data.ProjectData;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.statement.select.SelectExpressionItem;
import net.sf.jsqlparser.statement.select.SelectItemVisitorAdapter;

import java.util.List;

/**
 * @Classname MySelectItemVisitorAdapter
 * @Description
 * @Date 2022/11/23 18:20
 * @Created by wangchangjiu
 */
public class MySelectItemVisitorAdapter extends SelectItemVisitorAdapter {

    private final List<ProjectData> projects;

    public MySelectItemVisitorAdapter(List<ProjectData> projects){
        this.projects = projects;
    }


    @Override
    public void visit(SelectExpressionItem selectExpressionItem) {
        // 别名
        String alias = selectExpressionItem.getAlias() == null ? "" : selectExpressionItem.getAlias().getName();
        Expression expression = selectExpressionItem.getExpression();
        ProjectData projectData = new ProjectData();
        projectData.setAlias(alias);
        expression.accept(new MySelectExpressionVisitorAdapter(projectData));
        projects.add(projectData);
    }


}
