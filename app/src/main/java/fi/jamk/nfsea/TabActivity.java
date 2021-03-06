package fi.jamk.nfsea;


import android.content.ContentValues;
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
import android.support.design.widget.TabLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.nio.charset.Charset;

public class TabActivity extends AppCompatActivity implements SendMessageDialogFragment.SendMessageDialogListener,
        NfcAdapter.CreateNdefMessageCallback,
        NfcAdapter.OnNdefPushCompleteCallback,
        NFSeaMessageFragment.OnListFragmentInteractionListener{

    private SectionsPagerAdapter mSectionsPagerAdapter;
    private ViewPager mViewPager;
    private final String DB_TABLE = "messages";
    private SQLiteDatabase db;
    private Cursor cursor;
    NfcAdapter mNfcAdapter;
    private static ObservableArrayList<NFSeaMessage> receivedMessages;
    private static ObservableArrayList<NFSeaMessage> pendingMessages;
    private static ObservableArrayList<NFSeaMessage> sentMessages;
    public static String PACKAGE_NAME;
    private TabLayout tabLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tab);

        PACKAGE_NAME = getApplicationContext().getPackageName();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        db = (new NFSeaDatabase(this)).getWritableDatabase();

        //queryData();
        // init messages and create dummy data for Testing
        receivedMessages = new ObservableArrayList<>();
        pendingMessages = new ObservableArrayList<>();
        sentMessages = new ObservableArrayList<>();

        getMessages();
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SendMessageDialogFragment df = new SendMessageDialogFragment();
                df.show(getSupportFragmentManager(), "");
            }
        });
        initObservableList();

        // Check for available NFC Adapter
        mNfcAdapter = NfcAdapter.getDefaultAdapter(this);
        if (mNfcAdapter == null) {
            Toast.makeText(this, "NFC is not available", Toast.LENGTH_LONG).show();
            finish();
            return;
        }
        // Register callbacks
        mNfcAdapter.setNdefPushMessageCallback(this, this);
        mNfcAdapter.setOnNdefPushCompleteCallback(this, this);

        processIntent(getIntent());
    }

    public void initObservableList() {
        receivedMessages.addOnListChangedCallback(new ObservableList.OnListChangedCallback<ObservableList<NFSeaMessage>>() {
            @Override
            public void onChanged(ObservableList<NFSeaMessage> nfSeaMessages) {

            }

            @Override
            public void onItemRangeChanged(ObservableList<NFSeaMessage> nfSeaMessages, int i, int i1) {

            }

            @Override
            public void onItemRangeInserted(ObservableList<NFSeaMessage> nfSeaMessages, int i, int i1) {

            }

            @Override
            public void onItemRangeMoved(ObservableList<NFSeaMessage> nfSeaMessages, int i, int i1, int i2) {

            }

            @Override
            public void onItemRangeRemoved(ObservableList<NFSeaMessage> nfSeaMessages, int i, int i1) {
                mSectionsPagerAdapter.notifyDataSetChanged();
            }
        });

        pendingMessages.addOnListChangedCallback(new ObservableList.OnListChangedCallback<ObservableList<NFSeaMessage>>() {
            @Override
            public void onChanged(ObservableList<NFSeaMessage> nfSeaMessages) {

            }

            @Override
            public void onItemRangeChanged(ObservableList<NFSeaMessage> nfSeaMessages, int i, int i1) {
                //mSectionsPagerAdapter.notifyDataSetChanged();
            }

            @Override
            public void onItemRangeInserted(ObservableList<NFSeaMessage> nfSeaMessages, int i, int i1) {
                ObservableArrayList<NFSeaMessage> newMessages = (ObservableArrayList) nfSeaMessages;
                Toast.makeText(getApplicationContext(), "Message added to pending messages", Toast.LENGTH_SHORT).show();
                mSectionsPagerAdapter.notifyDataSetChanged();
            }

            @Override
            public void onItemRangeMoved(ObservableList<NFSeaMessage> nfSeaMessages, int i, int i1, int i2) {

            }

            @Override
            public void onItemRangeRemoved(ObservableList<NFSeaMessage> nfSeaMessages, int i, int i1) {
               mSectionsPagerAdapter.notifyDataSetChanged();
            }
        });

        sentMessages.addOnListChangedCallback(new ObservableList.OnListChangedCallback<ObservableList<NFSeaMessage>>() {
            @Override
            public void onChanged(ObservableList<NFSeaMessage> nfSeaMessages) {

            }

            @Override
            public void onItemRangeChanged(ObservableList<NFSeaMessage> nfSeaMessages, int i, int i1) {
            }

            @Override
            public void onItemRangeInserted(ObservableList<NFSeaMessage> nfSeaMessages, int i, int i1) {

            }

            @Override
            public void onItemRangeMoved(ObservableList<NFSeaMessage> nfSeaMessages, int i, int i1, int i2) {

            }

            @Override
            public void onItemRangeRemoved(ObservableList<NFSeaMessage> nfSeaMessages, int i, int i1) {
                mSectionsPagerAdapter.notifyDataSetChanged();
            }
        });
    }

    /**
     * Get messages from SQLite database and sort them into arrayLists
     */
    public void getMessages(){
        String[] resultColumns = new String[]{"_id","messageTitle","messageContent", "messageStatus"};
        cursor = db.query(DB_TABLE,resultColumns,null,null,null,null,"messageTitle ASC",null);

        if(cursor.moveToFirst()) {
            do{
                if( cursor.getString(3).equals("received")){
                    Log.i("Viestin id: ", ""+cursor.getInt(0));
                    receivedMessages.add(new NFSeaMessage(cursor.getInt(0) ,cursor.getString(1), cursor.getString(2), cursor.getString(3)));
                } else if ( cursor.getString(3).equals("sent")){
                    sentMessages.add(new NFSeaMessage(cursor.getInt(0), cursor.getString(1), cursor.getString(2), cursor.getString(3)));
                }
            } while(cursor.moveToNext());
        }
    }

    public static ObservableArrayList getMessageArray(){
        return receivedMessages;
    }

    public static ObservableArrayList getPendingMessagesArray() { return pendingMessages; }

    public static ObservableArrayList getSentMessagesArray() { return sentMessages; }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_tab, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_refresh) {
            mSectionsPagerAdapter.notifyDataSetChanged();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onDialogPositiveClick(android.support.v4.app.DialogFragment dialog, String heading, String content, String status) {
        pendingMessages.add(new NFSeaMessage(heading, content, status));
    }

    @Override
    public void onDialogNegativeClick(android.support.v4.app.DialogFragment dialog) {
        // Nothing
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

    @Override
    public NdefMessage createNdefMessage(NfcEvent event) {
        if (pendingMessages.size() == 0) return null;
        else {

            NdefRecord[] records = new NdefRecord[pendingMessages.size() + 1];
            GsonBuilder builder = new GsonBuilder();
            builder.serializeNulls(); // outputs also the null values

            for (int i = 0; i < pendingMessages.size(); i++){

                Gson gson = builder.create();
                String msg = gson.toJson(pendingMessages.get(i));
                byte[] payload = msg.getBytes(Charset.forName("UTF-8"));

                NdefRecord record = NdefRecord.createMime("text/plain",payload);
                records[i] = record;
            }

            records[pendingMessages.size()] = NdefRecord.createApplicationRecord(PACKAGE_NAME);
            return new NdefMessage(records);

        }
    }

    // Process incoming message(s)
    public void processIntent(Intent intent) {

        if (NfcAdapter.ACTION_NDEF_DISCOVERED.equals(intent.getAction())) {
            Parcelable[] rawMsgs = intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);
            GsonBuilder builder = new GsonBuilder();

            if (rawMsgs != null) {
                NdefMessage receivedMsg = (NdefMessage) rawMsgs[0];
                NdefRecord[] records = receivedMsg.getRecords();

                for (int i=0; i<records.length; i++) {
                    Gson gson = builder.create();
                    String jsonObj = new String(records[i].getPayload());

                    if (jsonObj.equals(getPackageName())) { continue; }

                    NFSeaMessage msg = gson.fromJson(jsonObj, NFSeaMessage.class);
                    msg.setStatus("received");
                    receivedMessages.add(msg);
                    mSectionsPagerAdapter.notifyDataSetChanged();
                    insertData(msg);
                }

                Toast.makeText(getApplicationContext(), "Received " + (records.length-1) + " messages.", Toast.LENGTH_LONG).show();

            } else {
                Toast.makeText(getApplicationContext(), "Received nothing", Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    public void onNdefPushComplete(NfcEvent event) {
        for(int i = 0; i < pendingMessages.size(); i++){
            pendingMessages.get(i).setStatus("sent");
            insertData(pendingMessages.get(i));
        }
        sentMessages.addAll(pendingMessages);
        pendingMessages.clear();
        mSectionsPagerAdapter.notifyDataSetChanged();
    }

    /**
     * onLongClick events handling.
     * @param item
     */
    @Override
    public void onListFragmentInteraction(NFSeaMessage item) {
        String[] args = {String.valueOf(item.getId())};
        db.delete("messages", "_id=?", args);
    }

    public class SectionsPagerAdapter extends FragmentStatePagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).

            return NFSeaMessageFragment.newInstance(position + 1);
        }

        @Override
        public int getItemPosition(Object object){
            return POSITION_NONE;
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return 3;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return getApplicationContext().getResources().getString(R.string.receivedMessages);
                case 1:
                    return getApplicationContext().getResources().getString(R.string.sentMessages);
                case 2:
                    return getApplicationContext().getResources().getString(R.string.pendingMessages);
            }
            return null;
        }
    }

    public void insertData(NFSeaMessage msg){
        ContentValues values = new ContentValues();
        values.put("messageTitle", msg.getTitle());
        values.put("messageContent", msg.getContent());
        values.put("messageStatus", msg.getStatus());
        db.insert(DB_TABLE, null, values);
    }
}
