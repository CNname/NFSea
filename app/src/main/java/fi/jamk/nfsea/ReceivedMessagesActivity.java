package fi.jamk.nfsea;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import java.util.ArrayList;

public class ReceivedMessagesActivity extends AppCompatActivity {

    ArrayList<NFSeaMessage> messages;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_received_messages);

        // get messages
        Intent intent = this.getIntent();
        Bundle bundle = intent.getExtras();
        messages = (ArrayList<NFSeaMessage>) bundle.getSerializable("messages");

        TextView tv = (TextView) findViewById(R.id.tempTextview);
        tv.setText("Message count: " + messages.size());

    }
}
