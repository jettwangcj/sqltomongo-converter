# sqltomongo-converter

#### 介绍
sql 语法转化为 mongo 语法核心转化器


#### 使用说明

1. 代码托管在 Maven 中央仓库中
```
<dependency>
  <groupId>cn.org.wangchangjiu</groupId>
  <artifactId>sqltomongo-converter</artifactId>
  <version>{sqltomongo.version}</version>
</dependency>
```


2. 利用 `SelectSQLTypeParser` 静态方法 `SelectSQLTypeParser.defaultConverterToString(String sql)` 转换；

例：
```
String sql = "SELECT n._id, n.languageList, n.score, ne.enable FROM novel n left join novelEpisodes ne on n._id = objectId(ne.novelId) limit 0,10"
String mongo = SelectSQLTypeParser.defaultConverterToString(sql)

```

