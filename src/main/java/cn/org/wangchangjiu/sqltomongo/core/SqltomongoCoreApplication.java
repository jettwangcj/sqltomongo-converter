package cn.org.wangchangjiu.sqltomongo.core;

import cn.org.wangchangjiu.sqltomongo.core.analyzer.DefaultAnalyzerBuilder;
import cn.org.wangchangjiu.sqltomongo.core.common.MongoParserResult;
import cn.org.wangchangjiu.sqltomongo.core.parser.DefaultPartSQLParserBuilder;
import cn.org.wangchangjiu.sqltomongo.core.parser.SelectSQLTypeParser;
import cn.org.wangchangjiu.sqltomongo.core.parser.data.PartSQLParserData;

public class SqltomongoCoreApplication {

    public static void main(String[] args) {

        //  String sql = " SELECT ne.novelId,ne.episodesNo, ne.enable, nr.readNum FROM novelEpisodes ne left join ( select nr.id from novelReadRecord nr where nr.readNum = 10) R on ne.novelId = R.novelId  ";

        //  String sql = "SELECT ne.novelId, ne.episodesNo, ne.enable, nr.readNum FROM novelEpisodes ne left join(select nr.id from novelReadRecord nr inner join novel n on nr.novelId = string(n._id) where nr.readNum = 10 and n.score > 5) R on ne.novelId = R.novelId";

        //  String sql = "SELECT n._id, n.languageList, n.score, ne.enable, nr.readNum FROM novel n left join novelEpisodes ne on string(n._id) = ne.novelId left join novelReadRecord nr on ne.novelId = nr.novelId where ne.episodesNo = 4 limit 0,10";

          String sql = "SELECT n._id, n.languageList, n.score, ne.enable FROM novel n left join novelEpisodes ne on string(n._id) = ne.novelId where ne.episodesNo = 4 and ne.tt > 1 limit 0,10";
        // String sql = "SELECT n._id, n.languageList, n.score, ne.enable FROM novel n left join (select nr.novelId, ne.episodesNo from novelEpisodes nr where ne.episodesNo = 4) ne on string(n._id) = ne.novelId where ne.episodesNo = 4 limit 0,10";
       // String sql = "SELECT n._id, n.languageList, n.score, ne.enable FROM novel n left join novelEpisodes ne on string(n._id) = ne.novelId where ne.episodesNo in (1,4) and ne.tt like '%10'  limit 2,10";


        SelectSQLTypeParser selectSQLTypeParser = new SelectSQLTypeParser(new DefaultPartSQLParserBuilder(), new DefaultAnalyzerBuilder());
        PartSQLParserData parserData = selectSQLTypeParser.getPartSQLParserData(sql);
        MongoParserResult mongoParserResult = selectSQLTypeParser.mongoAggregationAnalyzer(parserData);


        System.out.println(mongoParserResult.toJson());

    }

}
