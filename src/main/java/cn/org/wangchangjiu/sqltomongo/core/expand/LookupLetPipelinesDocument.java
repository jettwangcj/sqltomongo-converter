package cn.org.wangchangjiu.sqltomongo.core.expand;

import org.bson.Document;

import java.util.List;

/**
 * @Classname LookupLetPipelinesOperation
 * @Description 扩展 lookup语法
 * @Date 2022/11/25 15:12
 * @Created by wangchangjiu
 */
public class LookupLetPipelinesDocument {

    private String from;
    private Document let;
    private List<Document> pipeline;
    private String as;

    private LookupLetPipelinesDocument(String from, Document let, List<Document> pipeline, String as) {
        this.from = from;
        this.let = let;
        this.pipeline = pipeline;
        this.as = as;
    }

    public Document toDocument() {
        return new Document("$lookup",
                new Document("from", this.from)
                        .append("let", this.let)
                        .append("pipeline", this.pipeline)
                        .append("as", this.as));
    }

    public static final class LetBuilder {

        private final Document doc = new Document();

        public static LetBuilder newBuilder() {
            return new LetBuilder();
        }

        public LetBuilder add(String key, Object value){
            this.doc.append(key, value);
            return this;
        }

        public Document build(){
            return this.doc;
        }

    }


    public static final class LookupLetPipelinesDocumentBuilder {

        private String from;
        private Document let;
        private List<Document> pipeline;
        private String as;

        /**
         *
         * @return never {@literal null}.
         */
        public static LookupLetPipelinesDocumentBuilder newBuilder() {
            return new LookupLetPipelinesDocumentBuilder();
        }

        public LookupLetPipelinesDocumentBuilder from(String form) {
            this.from = form;
            return this;
        }

        public LookupLetPipelinesDocument as(String as) {
            this.as = as;
            return new LookupLetPipelinesDocument(from, let, pipeline, as);
        }

        public LookupLetPipelinesDocumentBuilder let(Document let) {
            this.let = let;
            return this;
        }

        public LookupLetPipelinesDocumentBuilder pipeline(List<Document> pipeline) {

            this.pipeline = pipeline;
            return this;
        }
    }

}
