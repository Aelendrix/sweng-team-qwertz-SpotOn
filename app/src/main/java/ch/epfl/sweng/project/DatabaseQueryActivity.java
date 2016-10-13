package ch.epfl.sweng.project;

import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.appindexing.Thing;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import java.util.HashMap;

/**
 * Created by quentin on 12.10.16.
 */

public class DatabaseQueryActivity extends AppCompatActivity {

    // Firebase instance variables
    private DatabaseReference myDBref;
    // query result saved as class object to be able to access it in the Listener
    protected String queryResult;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_database_query);
        myDBref = FirebaseDatabase.getInstance().getReference("keyVal");
    }

    /*
    // This was meant to populate the database with some java objects, but trying this throws a
    // DatabaseException : Found conflicting getters for name isChangingConfigurations
    // which I could not find any documentation about -> will need to resolve later
    private class KeyVal {
        private String key;
        private String value;
        public KeyVal(){
            key="";
            value="";
        }
        public KeyVal(String k, String v){
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

    private void populateDB(){
        HashMap<String, KeyVal> dummyData = new HashMap<>();
        dummyData.put("0", new KeyVal("Hello","World"));
        dummyData.put("1", new KeyVal("Hola","Mundo"));
        dummyData.put("2", new KeyVal("Hallo","Welt"));
        myDBref.setValue(dummyData);
        KeyVal kv1 = new KeyVal("Hello","World");
        String key = myDBref.push().getKey();
        myDBref.child(key).setValue(kv1);
    }*/

    public void queryDB(View v) {
        EditText keyTextField = (EditText) findViewById(R.id.keyTextField);
        String queryKey = keyTextField.getText().toString();

        /*myDBref = FirebaseDatabase.getInstance().getReference();
        ValueEventListener valueListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                System.out.println(dataSnapshot.getValue());
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
            private void exploreChildren(DataSnapshot dataSnapshot){
                if(dataSnapshot.hasChildren()){
                    Iterator<DataSnapshot> children = dataSnapshot.getChildren().iterator();
                    while(children.hasNext()){
                        exploreChildren(children.next());
                    }
                }else{
                    System.out.println(dataSnapshot.getValue());
                }
            }
        };*/

        Query query = myDBref.orderByChild("cle").equalTo(queryKey);
        query.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                String queryResult = ((HashMap<String, String>)dataSnapshot.getValue()).get("val");
                setQueryResultTextField(queryResult);
            }
            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                HashMap<String, String> result = (HashMap<String, String>)dataSnapshot.getValue();
                String obtained = result.get("val");
                System.out.println(obtained);
                setQueryResultTextField(obtained);
            }
            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }
            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    public void setQueryResultTextField(String result){
        TextView valueDisplay = (TextView) findViewById((R.id.QueryResultView));
        valueDisplay.setText(result);
    }

}
