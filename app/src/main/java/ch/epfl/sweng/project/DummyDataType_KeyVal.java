package ch.epfl.sweng.project;

/**
 * Created by quentin on 13.10.16.
 */

public class DummyDataType_KeyVal {
    private String key;
    private String value;
    public DummyDataType_KeyVal(){
        key="";
        value="";
    }
    public DummyDataType_KeyVal(String k, String v){
        this.key=new String(k);
        this.value=new String(v);
    }
    public String getKey(){
        return key;
    }
    public String getValue(){
        return value;
    }
}