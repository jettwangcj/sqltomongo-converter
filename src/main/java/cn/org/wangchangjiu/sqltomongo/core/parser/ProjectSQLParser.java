package cn.org.wangchangjiu.sqltomongo.core.parser;

import cn.org.wangchangjiu.sqltomongo.core.adapter.MySelectItemVisitorAdapter;
import cn.org.wangchangjiu.sqltomongo.core.parser.data.PartSQLParserData;
import cn.org.wangchangjiu.sqltomongo.core.parser.data.ProjectData;
import net.sf.jsqlparser.statement.select.PlainSelect;
import net.sf.jsqlparser.statement.select.SelectItem;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

public class ProjectSQLParser extends AbstractPartSQLParser {

    @Override
    public void proceedData(PlainSelect plain, PartSQLParserData data) {
        List<SelectItem> selectItems = plain.getSelectItems();
        if (!CollectionUtils.isEmpty(selectItems)) {
            List<ProjectData> projects = new ArrayList<>();
            selectItems.forEach(selectItem -> selectItem.accept(new MySelectItemVisitorAdapter(projects)));
            data.setProjectData(projects);
        }
    }
}
