package cn.org.wangchangjiu.sqltomongo.core.common;

/**
 * @Classname SortDirection
 * @Description
 * @Date 2022/12/2 18:08
 * @Created by wangchangjiu
 */
public enum SortDirection {

    ASC,
    DESC;

    private SortDirection() {
    }

    public boolean isAscending() {
        return this.equals(ASC);
    }

    public boolean isDescending() {
        return this.equals(DESC);
    }


}
