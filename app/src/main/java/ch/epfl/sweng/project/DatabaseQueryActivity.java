package ch.epfl.sweng.project;

import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

/** This activity allows the user to type in a key, and look it up on a firebase database
 *  of key/value String pairs.
 *  It displays the result on the screen
 *
 *  Some code has been commented out, as I tried to implement key/value String-Objects pairs
 *  but it's broken as of now, because of data typing issues (can't cast the data correctly)
 */

public class DatabaseQueryActivity extends AppCompatActivity {

    // The path to the root of the stored key/value pairs in the daabase
    private final String PATH_TO_KEYVAL_ROOT = "keyVal";

    // Firebase instance variables
    private DatabaseReference myDBref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_database_query);
        myDBref = FirebaseDatabase.getInstance().getReference(PATH_TO_KEYVAL_ROOT);
        //populateDB(); // to fill the database with java object (broken now)
    }

    /**
     *  Reads the content of the EditText keyTextField and returns it
     * @return the content of the EditText keyTextField
     */
    public String readQueryKeyword(){
        EditText keyTextField = (EditText) findViewById(R.id.keyTextField);
        return keyTextField.getText().toString();
    }

    /**
     * Sets the textField that is supposed to display the result of the query to result
     * @param result : the string that the textField should display
     */
    public void setQueryResultTextField(String result){
        TextView valueDisplay = (TextView) findViewById((R.id.QueryResultView));
        valueDisplay.setText(result);
    }

    /**
     *  queries the database for the keyword/text that is inside keyTextField
     */
    public void queryDB(View v) {
        //retrieve query keyword
        final String queryKey = readQueryKeyword();

        // create a new Query object, and add a listener to it. The onDataChange method will be executed once
        myDBref.orderByChild("cle").equalTo(queryKey).addListenerForSingleValueEvent(
                new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                System.out.println("datasnapshot : "+dataSnapshot.getValue());
                if(dataSnapshot.exists()){
                    int nbOfResults=0;
                    for(DataSnapshot child : dataSnapshot.getChildren()){
                        System.out.println("child : "+child.getValue());
                        HashMap<String, String> resultAsMap = ((HashMap<String, String>) child.getValue());
                        String queryResult = resultAsMap.get("val");
                        System.out.println(resultAsMap+" "+queryResult);
                        setQueryResultTextField(queryResult);
                        nbOfResults+=1;
                    }
                    if(nbOfResults!=1){
                        setQueryResultTextField("Several results found [wut?]");
                    }
                }else{
                    setQueryResultTextField("No such element in database");
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                // in case we get a database error
            }
        });
    }

    // Will be used in the future to write Java objects on the database
    /*private void populateDB(){
        HashMap<String, DummyDataType_KeyVal> dummyData = new HashMap<>();
        dummyData.put("1", new DummyDataType_KeyVal("Hello","World"));
        dummyData.put("0", new DummyDataType_KeyVal("Hola","Mundo"));
        dummyData.put("2", new DummyDataType_KeyVal("Hallo","Welt"));
        myDBref.setValue(dummyData);
    }*/
}
