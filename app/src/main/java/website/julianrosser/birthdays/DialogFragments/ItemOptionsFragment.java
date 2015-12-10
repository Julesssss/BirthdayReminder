package website.julianrosser.birthdays.DialogFragments;


import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;

import website.julianrosser.birthdays.MainActivity;
import website.julianrosser.birthdays.R;

public class ItemOptionsFragment extends DialogFragment {

    // Logging tag which displays class name in readable form
    private String TAG = getClass().getSimpleName();

    // Use this instance of the interface to deliver action events
    ItemOptionsListener mListener;

    static int birthdayListPosition;


    public ItemOptionsFragment() {
        // Required empty public constructor
    }

    // New instance is preferable as we have option to initialize here instead of using passed in params.
    public static ItemOptionsFragment newInstance(int position) {

        birthdayListPosition = position;

        return new ItemOptionsFragment();
    }


    /* MainActivity implements this interface in order to receive event callbacks. Passes the
    DialogFragment in case the host needs to query it. */
    public interface ItemOptionsListener {
        void onItemEdit(ItemOptionsFragment dialog, int position);

        void onItemDelete(ItemOptionsFragment dialog, int position);
    }


    // We override the Fragment.onAttach() method to instantiate NoticeDialogListener and read bundle data
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        // Verify that the host activity implements the callback interface
        try {
            // Instantiate the NoticeDialogListener so we can send events to the host
            mListener = (ItemOptionsListener) activity;
        } catch (ClassCastException e) {
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException(activity.toString()
                    + " must implement ItemOptions listener");
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        String nameTitle = MainActivity.birthdaysList.get(birthdayListPosition).getName();

        builder.setTitle(nameTitle)
                .setItems(getResources().getStringArray(R.array.item_menu_array), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                        switch (which) {
                            case 0:
                                mListener.onItemEdit(ItemOptionsFragment.this, birthdayListPosition);
                                break;
                            case 1:
                                mListener.onItemDelete(ItemOptionsFragment.this, birthdayListPosition);
                                break;
                        }
                    }
                });
        return builder.create();
    }
}
