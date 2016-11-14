package fi.jamk.nfsea;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;


public class SendMessageDialogFragment extends DialogFragment {


    private final String STATUS = "pending";

    SendMessageDialogListener mListener;

    public interface SendMessageDialogListener{
        void onDialogPositiveClick(DialogFragment dialog, String heading, String content, String status);
        void onDialogNegativeClick(DialogFragment dialog);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        // Verify that the host activity implements the callback interface
        try {
            // Instantiate the ClockDialogListener so we can send events to the host
            mListener = (SendMessageDialogListener) activity;
        } catch (ClassCastException e) {
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException(activity.toString() + " must implement SendMessageDialogListener");
        }
    }


    public Dialog onCreateDialog(Bundle savedInstanceState) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            // Get the layout inflater
            LayoutInflater inflater = getActivity().getLayoutInflater();

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

                            if (heading.isEmpty() && content.isEmpty()) {
                                Toast.makeText(getContext(), "Please write something before adding a message.", Toast.LENGTH_LONG).show();
                            } else {
                                mListener.onDialogPositiveClick(SendMessageDialogFragment.this, heading, content, STATUS);
                            }

                        }
                    })
                    .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            mListener.onDialogNegativeClick(SendMessageDialogFragment.this);
                        }
                    });
            return builder.create();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

}
