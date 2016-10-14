package ch.epfl.sweng.project;

/**
 * This class is a Dummy key/value pair data type for demonstration purpose in database query.
 * broken as of now
 */

public class DummyDataType_KeyVal {
    private String cle;
    private String val;
    public DummyDataType_KeyVal(){
        cle="";
        val="";
    }
    public DummyDataType_KeyVal(String k, String v){
        this.cle=new String(k);
        this.val=new String(v);
    }
    public String getKey(){
        return cle;
    }
    public String getValue(){
        return val;
    }
}