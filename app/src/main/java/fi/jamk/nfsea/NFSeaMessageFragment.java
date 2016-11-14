package fi.jamk.nfsea;

import android.content.Context;
import android.databinding.ObservableArrayList;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.util.ArrayList;

public class NFSeaMessageFragment extends Fragment  {

    private ObservableArrayList<NFSeaMessage> messages;
    private static final String ARG_COLUMN_COUNT = "column-count";
    private static final String ARG_SECTION_NUMBER = "section_number";
    private int mColumnCount = 1;
    private OnListFragmentInteractionListener mListener;

    public NFSeaMessageFragment() {}

    public static NFSeaMessageFragment newInstance(int sectionNumber) {
        NFSeaMessageFragment fragment = new NFSeaMessageFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mColumnCount = getArguments().getInt(ARG_SECTION_NUMBER);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_nfseamessage_list, container, false);

        // Set the adapter
        if (view instanceof RecyclerView) {
            Context context = view.getContext();
            RecyclerView recyclerView = (RecyclerView) view;
            recyclerView.setLayoutManager(new LinearLayoutManager(context));
            switch (mColumnCount) {
                case 0:
                   // Toast.makeText(context, "Olen case 0: " +mColumnCount, Toast.LENGTH_LONG ).show();
                case 1:
                    recyclerView.setAdapter(new NFSeaMessageRecyclerViewAdapter(TabActivity.getMessageArray(), mListener));
                    break;
                case 2:
                    ObservableArrayList<NFSeaMessage> temp = TabActivity.getSentMessagesArray();
                    recyclerView.setAdapter(new NFSeaMessageRecyclerViewAdapter(temp, mListener));
                    break;
                case 3:
                    recyclerView.setAdapter(new NFSeaMessageRecyclerViewAdapter(TabActivity.getPendingMessagesArray(), mListener));
                    break;
            }
            recyclerView.getAdapter().notifyDataSetChanged();
        }
        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnListFragmentInteractionListener) {
            mListener = (OnListFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnListFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnListFragmentInteractionListener {
        void onListFragmentInteraction(NFSeaMessage item);
    }
}
