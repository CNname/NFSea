package fi.jamk.nfsea;

import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.databinding.ObservableArrayList;
import android.databinding.ObservableList;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.NfcEvent;
import android.os.Parcelable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.nio.charset.Charset;

import static android.nfc.NdefRecord.createMime;

public class MainActivity extends AppCompatActivity implements NfcAdapter.CreateNdefMessageCallback, NfcAdapter.OnNdefPushCompleteCallback {

    private final String DB_TABLE = "nfseaMessages";
    private SQLiteDatabase db;
    private Cursor cursor;
    private int sentMsgsCount;
    NfcAdapter mNfcAdapter;
    ObservableArrayList<NFSeaMessage> messages;
    ObservableArrayList<NFSeaMessage> bendingMessages;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        messages = new ObservableArrayList<>();
        bendingMessages = new ObservableArrayList<>();
        db = (new NFSeaDatabase(this)).getWritableDatabase();

        queryData();
        updateButtons();

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                android.app.FragmentManager fm = getFragmentManager();
                DialogFragment df = new SendMessageDialogFragment();
                df.show(fm, "sendmessage");
            }
        });

        messages.addOnListChangedCallback(new ObservableList.OnListChangedCallback<ObservableList<NFSeaMessage>>() {
            @Override
            public void onChanged(ObservableList<NFSeaMessage> nfSeaMessages) {

            }

            @Override
            public void onItemRangeChanged(ObservableList<NFSeaMessage> nfSeaMessages, int i, int i1) {

            }

            @Override
            public void onItemRangeInserted(ObservableList<NFSeaMessage> nfSeaMessages, int i, int i1) {
                ObservableArrayList<NFSeaMessage> newMessages = (ObservableArrayList) nfSeaMessages;
                Button receivedMsgs = (Button) findViewById(R.id.receivedMessagesBtn);
                receivedMsgs.setText("Received messages: " + newMessages.size());
            }

            @Override
            public void onItemRangeMoved(ObservableList<NFSeaMessage> nfSeaMessages, int i, int i1, int i2) {

            }

            @Override
            public void onItemRangeRemoved(ObservableList<NFSeaMessage> nfSeaMessages, int i, int i1) {

            }
        });

        bendingMessages.addOnListChangedCallback(new ObservableList.OnListChangedCallback<ObservableList<NFSeaMessage>>() {
            @Override
            public void onChanged(ObservableList<NFSeaMessage> nfSeaMessages) {

            }

            @Override
            public void onItemRangeChanged(ObservableList<NFSeaMessage> nfSeaMessages, int i, int i1) {

            }

            @Override
            public void onItemRangeInserted(ObservableList<NFSeaMessage> nfSeaMessages, int i, int i1) {
                ObservableArrayList<NFSeaMessage> newMessages = (ObservableArrayList) nfSeaMessages;
                Button bendingMsgs = (Button) findViewById(R.id.bendingMessagesBtn);
                bendingMsgs.setText(getApplicationContext().getResources().getString(R.string.bendingMessages) + ": " + newMessages.size());
            }

            @Override
            public void onItemRangeMoved(ObservableList<NFSeaMessage> nfSeaMessages, int i, int i1, int i2) {

            }

            @Override
            public void onItemRangeRemoved(ObservableList<NFSeaMessage> nfSeaMessages, int i, int i1) {

            }
        });


        // Check for available NFC Adapter
        mNfcAdapter = NfcAdapter.getDefaultAdapter(this);
        if (mNfcAdapter == null) {
            Toast.makeText(this, "NFC is not available", Toast.LENGTH_LONG).show();
            finish();
            return;
        }
        // Register callback
        mNfcAdapter.setNdefPushMessageCallback(this, this);

        processIntent(getIntent());

    }

    public void queryData(){
        String[] resultColumns = new String[]{"_id","messageTitle","messageContent"};
        cursor = db.query(DB_TABLE,resultColumns,null,null,null,null,"messageTitle ASC",null);

    }



    private void updateButtons() {
        System.out.println("updateButtons called");
        sentMsgsCount = 0;

        if (cursor.moveToFirst()) {
            do {
                //String score = cursor.getString(1); // columnIndex
                sentMsgsCount++;
                System.out.println("Määrä: "+sentMsgsCount);
            } while(cursor.moveToNext());

        }

        Button receivedMsgs = (Button) findViewById(R.id.receivedMessagesBtn);
        receivedMsgs.setText("Received messages: " + messages.size());

        Button sentMsgs = (Button) findViewById(R.id.sentMessageBtn);
        sentMsgs.setText("Sent messages: " + sentMsgsCount);
    }

    @Override
    public NdefMessage createNdefMessage(NfcEvent event) {

        if (bendingMessages.size() == 0) return null;
        else {
            String message = (bendingMessages.get(0).getContent());
            NdefMessage msg = new NdefMessage(
                    new NdefRecord[]{
                            createMime(
                                    "text/plain", message.getBytes(Charset.forName("UTF-8"))
                            ), NdefRecord.createApplicationRecord(getPackageName())
                    });
            return msg;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        processIntent(getIntent());
    }

    @Override
    public void onNewIntent(Intent intent) {
        // onResume gets called after this to handle the intent
        processIntent(intent);
    }

    /**
     * Parses the NDEF Message from the intent
     */
    //@Override
    public void processIntent(Intent intent) {

        if (NfcAdapter.ACTION_NDEF_DISCOVERED.equals(intent.getAction())) {
            Parcelable[] rawMsgs = intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);
            // only one message sent during the beam
            NdefMessage msg = (NdefMessage) rawMsgs[0];
            // record 0 contains the MIME type, record 1 is the AAR, if present
            NFSeaMessage receivedMsg = new NFSeaMessage("Test", new String(msg.getRecords()[0].getPayload()));
            messages.add(receivedMsg);
            Toast.makeText(getApplicationContext(), "asdasdsa" + msg.getRecords()[0].getPayload().toString(), Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onNdefPushComplete(NfcEvent event) {
        bendingMessages.clear();
        Toast.makeText(getApplicationContext(), "Push complete", Toast.LENGTH_LONG).show();
    }

    public class SendMessageDialogFragment extends DialogFragment {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            // Get the layout inflater
            LayoutInflater inflater = getActivity().getLayoutInflater();
            final View inflator = inflater.inflate(R.layout.dialog_sendmessage, null);

            final View addMessageView = inflater.inflate(R.layout.dialog_sendmessage, null);
            builder.setView(addMessageView)
                    // Add action buttons
                    .setPositiveButton(R.string.addMessage, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int id) {
                            EditText titleField = (EditText) addMessageView.findViewById(R.id.messageHeading);
                            String heading = titleField.getText().toString();

                            EditText messageContent = (EditText) addMessageView.findViewById(R.id.messageContent);
                            String content = messageContent.getText().toString();

                            /*ContentValues values = new ContentValues(2);
                            values.put("messageTitle", heading);
                            values.put("messageContent", content);

                            db.insert("nfseaMessages", null, values);
                            queryData();
                            updateButtons();*/
                            bendingMessages.add(new NFSeaMessage(heading, content));

                        }
                    })
                    .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                        }
                    });
            return builder.create();
        }
    }



    public void openSentMessages(View view) {
        Intent intent = new Intent(view.getContext(), SentMessagesActivity.class);
        view.getContext().startActivity(intent);
    }

    public void openReceivedMessages(View view) {
        Intent intent = new Intent(view.getContext(), ReceivedMessagesActivity.class);

        Bundle bundle = new Bundle();
        bundle.putSerializable("messages", messages);
        intent.putExtras(bundle);

        view.getContext().startActivity(intent);
    }

    // close cursor and db connection
    @Override
    public void onDestroy() {
        super.onDestroy();
        cursor.close();
        db.close();
    }

}