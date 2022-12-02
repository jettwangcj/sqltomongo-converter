package cn.org.wangchangjiu.sqltomongo.core.adapter;

import cn.org.wangchangjiu.sqltomongo.core.common.ConversionFunction;
import cn.org.wangchangjiu.sqltomongo.core.exception.SqlParserException;
import cn.org.wangchangjiu.sqltomongo.core.parser.data.LookUpData;
import cn.org.wangchangjiu.sqltomongo.core.util.SqlSupportedSyntaxCheckUtil;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.Function;
import net.sf.jsqlparser.expression.operators.relational.EqualsTo;
import net.sf.jsqlparser.schema.Column;
import org.apache.commons.lang3.StringUtils;

/**
 * @Classname MyJoinExpressionVisitorAdapter
 * @Description
 * @Date 2022/11/24 15:24
 * @Created by wangchangjiu
 */
public class MyJoinExpressionVisitorAdapter extends BaseExpressionVisitorAdapter {

    private final LookUpData data;

    private final String majorTable;

    public MyJoinExpressionVisitorAdapter(LookUpData data, String majorTable){
        this.data = data;
        this.majorTable = majorTable;
    }

    @Override
    public void visit(EqualsTo equalsTo) {

        Expression leftExpression = equalsTo.getLeftExpression();
        Expression rightExpression = equalsTo.getRightExpression();

      //  ConversionFunction function = null;
        Column left , right;
      //  String conversionField = null;

        leftExpression.accept(this);
        rightExpression.accept(this);
        LookUpData.Let let = new LookUpData.Let();
        if(leftExpression instanceof Function){
            // 检查支持的函数
            // 因为 JOIN 函数支持 类型转换函数 检查有多个参数报错或者没有参数报错
            Function leftFunction = Function.class.cast(leftExpression);

            SqlSupportedSyntaxCheckUtil.checkTableAssociationSupportedFunction(leftFunction);

          //  function = ConversionFunction.parser(leftFunction.getName());
            let.setFunction(ConversionFunction.parser(leftFunction.getName()));
            left = Column.class.cast(leftFunction.getParameters().getExpressions().get(0));
            let.setConversionField(left.getColumnName());
           // conversionField = left.getColumnName();
            // conversionFieldTable = left.getTable().getName();
        } else {
            left = Column.class.cast(leftExpression);
        }

        if(rightExpression instanceof Function){

            Function rightFunction = Function.class.cast(rightExpression);
            // 检查支持的函数 因为 JOIN 函数支持 类型转换函数 检查有多个参数报错或者没有参数报错
            SqlSupportedSyntaxCheckUtil.checkTableAssociationSupportedFunction(rightFunction);
           // function = ConversionFunction.parser(rightFunction.getName());
            let.setFunction(ConversionFunction.parser(rightFunction.getName()));
            right = Column.class.cast(rightFunction.getParameters().getExpressions().get(0));
           // conversionField = right.getColumnName();
            let.setConversionField(right.getColumnName());
        } else {
            right = Column.class.cast(rightExpression);
        }

       // String localField = null, foreignField = null ;

      /*  if(data.getAlias().equalsIgnoreCase(conversionFieldTable)){
            // 函数发生在被关联表时，不支持
            throw new SqlParserException("语法错误，表关联时，objectId、string转换函数需要作用在主表中");
        }*/

        if(!StringUtils.isEmpty(data.getAlias())){
            // 关联表
            String leftTableName = left.getTable().getName();
            String rightTableName = right.getTable().getName();

            // 当出现三表关联时 考虑关联第三张表是主表关联的还是第二张表关联的
            if(leftTableName.equals(this.majorTable) || rightTableName.equals(this.majorTable)){
                // 使用主表关联  localField: 源集合中的match值 ,bookListId, foreignField: 待Join的集合的match值 id
                // 源集合 就是主表
                if(data.getAlias().equals(leftTableName)){

                   /* foreignField = left.getColumnName();
                    localField = right.getColumnName();*/

                    let.setLocalField(right.getColumnName());
                    let.setForeignField(left.getColumnName());
                    let.setFieldAlias("temp_".concat(let.getLocalField()));

                } else if(data.getAlias().equals(rightTableName)){
                  /*  foreignField = right.getColumnName();
                    localField = left.getColumnName() ;*/
                    let.setLocalField(left.getColumnName());
                    let.setForeignField(right.getColumnName());
                    let.setFieldAlias("temp_".concat(let.getLocalField()));
                }

            } else {
                // 由于 mongo 语法的特殊性，三张表关联，是不支持直接用第二张表管理第三张表的，举个例子：
                // from tab1 t1 left on tab2 t2 on t1.id = t2.t1_id left join tab3 t3 on t2.id = t3.t2_id  不允许
                //  mongog 语法 ： db.tab1.aggregate()， 都是由主表去关联的

                throw new SqlParserException("语法错误，多表关联时，只能用主表去关联其他表");
               /* // 使用中间表关联
                // 如果被关联表有别名 那么 别名.字段 的 是外键
                foreignField = left.getColumnName().contains(currTableAlias) ? left.getColumnName() : right.getColumnName();
                localField = !left.getColumnName().contains(currTableAlias) ? left.getColumnName() : right.getColumnName();
                // 关联第三张表时 用第二张表去关联 拼接第二张表名称
                // 例如： from tab1 t1 left on tab2 t2 on t1.id = t2.t1_id left join tab3 t3 on t2.id = t3.t2_id
                // 这里 tab3 是和 tab2 关联 所以 localField = tab2.id , mongo 是这样的
                localField = table.getName().concat(".").concat(localField);*/
            }
        } else {
            // 没有别名 按照默认习惯  主键在前，外键在后
            /*localField = left.getColumnName();
            foreignField = right.getColumnName();*/
            let.setLocalField(left.getColumnName());
            let.setForeignField(right.getColumnName());
            let.setFieldAlias("temp_".concat(let.getLocalField()));
        }

        data.setLet(let);
      /*  data.setLocalField(localField);
        data.setForeignField(foreignField);
        data.setFunction(function);
        data.setConversionField(conversionField);*/
        data.setAs("tmp_".concat(data.getTable()));

    }
}
