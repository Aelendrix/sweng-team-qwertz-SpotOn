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
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Created by quentin on 12.10.16.
 */

public class DatabaseQueryActivity extends AppCompatActivity {

    // Firebase instance variables
    private DatabaseReference myDBref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_database_query);
        myDBref = FirebaseDatabase.getInstance().getReference("keyVal");
        populateDB();
    }


    // This was meant to populate the database with some java objects, but trying this throws a
    // DatabaseException : Found conflicting getters for name isChangingConfigurations
    // which I could not find any documentation about -> will need to resolve later


    private void populateDB(){
        HashMap<String, DummyDataType_KeyVal> dummyData = new HashMap<>();
        dummyData.put("0", new DummyDataType_KeyVal("Hello","World"));
        dummyData.put("1", new DummyDataType_KeyVal("Hola","Mundo"));
        dummyData.put("2", new DummyDataType_KeyVal("Hallo","Welt"));
        myDBref.setValue(dummyData);
    }

    public void queryDB(View v) {
        // retrieve queried keyword
        EditText keyTextField = (EditText) findViewById(R.id.keyTextField);
        final String queryKey = keyTextField.getText().toString();

        // do the query
        myDBref.orderByChild("key").equalTo(queryKey).addListenerForSingleValueEvent(
                new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    System.out.println(dataSnapshot.getValue()+" "+dataSnapshot.exists());
                    // inconsistant data typing
                    //String queryResult = ((List<HashMap<String,String>>) dataSnapshot.getValue()).get(0).get("val");
                    //setQueryResultTextField(queryResult);
                }else{
                    setQueryResultTextField("No such element in database");
                }
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
        });

        /*Query query = myDBref.orderByChild("cle").equalTo(queryKey);
        query.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                String queryResult = ((HashMap<String, String>)dataSnapshot.getValue()).get("val");
                setQueryResultTextField(queryResult);
            }
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                this.onChildChanged(dataSnapshot, s);
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
        });*/
    }

    public void setQueryResultTextField(String result){
        TextView valueDisplay = (TextView) findViewById((R.id.QueryResultView));
        valueDisplay.setText(result);
    }

}
