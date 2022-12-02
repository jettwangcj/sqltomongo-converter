package cn.org.wangchangjiu.sqltomongo.core.common;

import org.bson.Document;

public enum ConversionFunction {

    TO_OBJECT_ID("objectId", "{ \"$toObjectId\": \"%s\" }"),
    TO_STRING("string", "{ \"$toString\": \"%s\" }");

    private String code;

    private String parser;

    public String getCode() {
        return code;
    }

    public String getParser() {
        return parser;
    }

    ConversionFunction(String code, String parser) {
        this.code = code;
        this.parser = parser;
    }

    public static ConversionFunction parser(String code) {
        for (ConversionFunction function : ConversionFunction.values()) {
            // 函数不区分大小写
            if (function.code.equalsIgnoreCase(code)) {
                return function;
            }
        }
        return null;
    }

    public  Document convert(String field){
        String format = String.format(this.getParser(), field);
        return Document.parse(format);
    }

}
