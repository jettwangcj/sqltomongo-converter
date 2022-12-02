package cn.org.wangchangjiu.sqltomongo.core.common;

import lombok.Data;
import org.bson.Document;
import org.springframework.data.mongodb.core.aggregation.Aggregation;

import java.util.List;

@Data
public class MongoParserResult {

   private List<Document> documents;

   private String collectionName;

   public MongoParserResult(){}

   public MongoParserResult(List<Document> documents, String collectionName){
       this.documents = documents;
       this.collectionName = collectionName;
   }



}
