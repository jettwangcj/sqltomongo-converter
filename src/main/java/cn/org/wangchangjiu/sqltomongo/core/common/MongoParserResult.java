package cn.org.wangchangjiu.sqltomongo.core.common;

import lombok.Data;
import org.apache.commons.collections4.CollectionUtils;
import org.bson.Document;

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

   private String toJson(){
       StringBuilder sb = new StringBuilder("[");
       if(!CollectionUtils.isEmpty(this.documents)){
           this.documents.stream().forEach(doc -> sb.append(doc.toJson()).append(","));
       }
       sb.deleteCharAt(sb.lastIndexOf(","));
       sb.append("]");
       return sb.toString();
   }

    public String toJsonString(){
        String json = this.toJson();
        return String.format("db.%s.aggregate(%s)", collectionName,json);
    }

    public String formatCommand(){
        return String.format("{ aggregate:\"%s\", pipeline:%s, maxTimeMS: 10000, cursor: {}}", this.getCollectionName(), this.toJson());
    }

}
