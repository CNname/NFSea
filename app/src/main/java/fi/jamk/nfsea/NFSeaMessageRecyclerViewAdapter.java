package fi.jamk.nfsea;

import android.databinding.ObservableArrayList;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class NFSeaMessageRecyclerViewAdapter extends RecyclerView.Adapter<NFSeaMessageRecyclerViewAdapter.ViewHolder> {

    public final NFSeaMessageFragment.OnListFragmentInteractionListener mListener;
    private static final String ARG_SECTION_NUMBER = "section_number";
    private ObservableArrayList<NFSeaMessage> mValues;

    public NFSeaMessageRecyclerViewAdapter(ObservableArrayList<NFSeaMessage> messages, NFSeaMessageFragment.OnListFragmentInteractionListener listener) {
        mValues = messages;
        mListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_nfseamessage, parent, false);
        //TextView textView = (TextView) view.findViewById(R.id.message_list_empty);
        //textView.setText("motley crue");
        return new ViewHolder(view);
    }

   @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mItem = mValues.get(position);
        holder.mIdView.setText(holder.mItem.getTitle());
        holder.mContentView.setText(holder.mItem.getContent());

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mListener) {
                    // Notify the active callbacks interface (the activity, if the
                    // fragment is attached to one) that an item has been selected.
                    mListener.onListFragmentInteraction(holder.mItem);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final NFSeaTextView mIdView;
        public final NFSeaTextView mContentView;
        public NFSeaMessage mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mIdView = (NFSeaTextView) mView.findViewById(R.id.message_list_title);
            mContentView = (NFSeaTextView) mView.findViewById(R.id.message_list_content);
        }


    }
}
