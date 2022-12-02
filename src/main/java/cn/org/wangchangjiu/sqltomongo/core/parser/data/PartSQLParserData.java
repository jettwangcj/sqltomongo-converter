package cn.org.wangchangjiu.sqltomongo.core.parser.data;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * @Classname PartSQLParserData
 * @Description
 * @Date 2022/8/12 15:49
 * @Created by wangchangjiu
 */
@Data
public class PartSQLParserData {

    /**
     *  子查询的深度
     */
    private Integer subQueryLevel;

    /**
     *  主表别名
     */
    private String majorTableAlias;

    /**
     *  主表
     */
    private String majorTable;

    /**
     *  关联表
     */
    private List<LookUpData> joinParser;

    /**
     *  投影
     */
    private List<ProjectData> projectData;

    /**
     *  过滤
     */
    private List<MatchData> matchData;

    /**
     *  分组
     */
    private List<GroupData> groupData;

    /**
     *  过滤
     */
    private List<MatchData> havingData;

    /**
     *  排序
     */
    private List<SortData> sortData;

    /**
     *  分页
     */
    private LimitData limitData;

    public List<LookUpData> getJoinParser() {
        return Optional.ofNullable(joinParser).orElse(new ArrayList<>());
    }

    public List<ProjectData> getProjectData() {
        return Optional.ofNullable(projectData).orElse(new ArrayList<>());
    }

    public List<MatchData> getMatchData() {
        return Optional.ofNullable(matchData).orElse(new ArrayList<>());
    }

    public List<GroupData> getGroupData() {
        return Optional.ofNullable(groupData).orElse(new ArrayList<>());
    }

    public List<MatchData> getHavingData() {
        return Optional.ofNullable(havingData).orElse(new ArrayList<>());
    }

    public List<SortData> getSortData() {
        return Optional.ofNullable(sortData).orElse(new ArrayList<>());
    }
}
