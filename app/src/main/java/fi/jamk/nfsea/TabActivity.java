package fi.jamk.nfsea;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.databinding.ObservableArrayList;
import android.databinding.ObservableList;
import android.nfc.NdefMessage;
import android.nfc.NfcAdapter;
import android.nfc.NfcEvent;
import android.support.design.widget.TabLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import android.widget.TextView;
import android.widget.Toast;

public class TabActivity extends AppCompatActivity implements SendMessageDialogFragment.SendMessageDialogListener,
        NfcAdapter.CreateNdefMessageCallback,
        NfcAdapter.OnNdefPushCompleteCallback,
        NFSeaMessageFragment.OnListFragmentInteractionListener{


    private SectionsPagerAdapter mSectionsPagerAdapter;
    private ViewPager mViewPager;
    private final String DB_TABLE = "messages";
    private SQLiteDatabase db;
    private Cursor cursor;
    private int sentMsgsCount;
    NfcAdapter mNfcAdapter;
    private int pendingMessagesSize;
    private static ObservableArrayList<NFSeaMessage> messages;
    private static ObservableArrayList<NFSeaMessage> pendingMessages;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tab);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        db = (new NFSeaDatabase(this)).getWritableDatabase();
        queryData();
        // init messages and create dummy data for Testing
        messages = new ObservableArrayList<>();
        messages.add(new NFSeaMessage("pending title", "placeholder content", "pending"));
        messages.add(new NFSeaMessage("pending title", "placeholder content", "pending"));
        messages.add(new NFSeaMessage("pending title", "placeholder content", "pending"));
        messages.add(new NFSeaMessage("pending title", "placeholder content", "pending"));
        messages.add(new NFSeaMessage("received title", "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Aliquam at nulla eget ipsum", "received"));
        messages.add(new NFSeaMessage("received title", "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Aliquam at nulla eget ipsum", "received"));
        messages.add(new NFSeaMessage("received title", "placeholder content", "received"));
        messages.add(new NFSeaMessage("received title", "placeholder content", "received"));
        messages.add(new NFSeaMessage("received title", "placeholder content", "received"));
        messages.add(new NFSeaMessage("sent title", "placeholder content", "sent"));
        messages.add(new NFSeaMessage("sent title", "placeholder content", "sent"));
        messages.add(new NFSeaMessage("sent title", "placeholder content", "sent"));

        pendingMessages = new ObservableArrayList<>();

        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //android.app.FragmentManager fm = getFragmentManager();
                SendMessageDialogFragment df = new SendMessageDialogFragment();
                df.show(getSupportFragmentManager(), "");
            }
        });
        initObservableList();
    }

    public void initObservableList() {
        messages.addOnListChangedCallback(new ObservableList.OnListChangedCallback<ObservableList<NFSeaMessage>>() {
            @Override
            public void onChanged(ObservableList<NFSeaMessage> nfSeaMessages) { }

            @Override
            public void onItemRangeChanged(ObservableList<NFSeaMessage> nfSeaMessages, int i, int i1) { }

            @Override
            public void onItemRangeInserted(ObservableList<NFSeaMessage> nfSeaMessages, int i, int i1) {
               // ObservableArrayList<NFSeaMessage> newMessages = (ObservableArrayList) nfSeaMessages;
            }

            @Override
            public void onItemRangeMoved(ObservableList<NFSeaMessage> nfSeaMessages, int i, int i1, int i2) { }

            @Override
            public void onItemRangeRemoved(ObservableList<NFSeaMessage> nfSeaMessages, int i, int i1) { }
        });

        pendingMessages.addOnListChangedCallback(new ObservableList.OnListChangedCallback<ObservableList<NFSeaMessage>>() {
            @Override
            public void onChanged(ObservableList<NFSeaMessage> nfSeaMessages) { }

            @Override
            public void onItemRangeChanged(ObservableList<NFSeaMessage> nfSeaMessages, int i, int i1) { }

            @Override
            public void onItemRangeInserted(ObservableList<NFSeaMessage> nfSeaMessages, int i, int i1) {
                ObservableArrayList<NFSeaMessage> newMessages = (ObservableArrayList) nfSeaMessages;
                pendingMessagesSize = newMessages.size();
                Toast.makeText(getApplicationContext(), "Message added to pending messages", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onItemRangeMoved(ObservableList<NFSeaMessage> nfSeaMessages, int i, int i1, int i2) { }

            @Override
            public void onItemRangeRemoved(ObservableList<NFSeaMessage> nfSeaMessages, int i, int i1) { }
        });
    }

    public static ObservableArrayList getMessageArray(){
        return messages;
    }

    public static ObservableArrayList getPendingMessagesArray() { return pendingMessages; }

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

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
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
    public NdefMessage createNdefMessage(NfcEvent event) {
        return null;
    }

    @Override
    public void onNdefPushComplete(NfcEvent event) {

    }

    @Override
    public void onListFragmentInteraction(NFSeaMessage item) {

    }

    /*  super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_received_messages);

        // get messages
        Intent intent = this.getIntent();
        Bundle bundle = intent.getExtras();
        messages = (ArrayList<NFSeaMessage>) bundle.getSerializable("messages");

        TextView tv = (TextView) findViewById(R.id.tempTextview);
        tv.setText("Message count: " + messages.size());*/
    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";

        public PlaceholderFragment() {
        }

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_tab, container, false);
            TextView textView = (TextView) rootView.findViewById(R.id.section_label);
            textView.setText(getString(R.string.section_format, getArguments().getInt(ARG_SECTION_NUMBER)));
            return rootView;
        }
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

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

    public void queryData(){
        String[] resultColumns = new String[]{"_id","messageTitle","messageContent", "messageStatus"};
        cursor = db.query(DB_TABLE,resultColumns,null,null,null,null,"messageTitle ASC",null);
    }
}
