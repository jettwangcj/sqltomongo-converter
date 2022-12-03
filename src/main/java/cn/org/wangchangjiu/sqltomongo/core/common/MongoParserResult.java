package cn.org.wangchangjiu.sqltomongo.core.common;

import lombok.Data;
import org.bson.Document;
import org.springframework.util.CollectionUtils;

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

   public String toJson(){
       StringBuilder sb = new StringBuilder("[");
       if(!CollectionUtils.isEmpty(this.documents)){
           this.documents.stream().forEach(doc -> sb.append(doc.toJson()).append(","));
       }
       sb.deleteCharAt(sb.lastIndexOf(","));
       sb.append("]");
       return sb.toString();
   }


}
