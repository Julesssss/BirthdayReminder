package website.julianrosser.birthdays;


import android.app.Activity;
import android.app.Dialog;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

/**
 * A simple {@link Fragment} subclass.
 */
public class AddEditBirthdayFragment extends DialogFragment {

    // mode selection;
    final static String MODE_KEY = "key_mode";
    final static String DATE_KEY = "key_date";
    final static String MONTH_KEY = "key_month";
    final static String POSITION_KEY = "key_position";
    final static String NAME_KEY = "key_position";

    final static int MODE_ADD = 0;
    final static int MODE_EDIT = 1;
    int ADD_OR_EDIT_MODE;

    Bundle bundle;

    View view;
    private String TAG = getClass().getSimpleName();

    public AddEditBirthdayFragment() {
        // Required empty public constructor
    }

    /* The activity that creates an instance of this dialog fragment must
   * implement this interface in order to receive event callbacks.
   * Each method passes the DialogFragment in case the host needs to query it. */
    public interface NoticeDialogListener {
        void onDialogPositiveClick(website.julianrosser.birthdays.AddEditBirthdayFragment dialog, String name, int date, int month);
    }

    // Use this instance of the interface to deliver action events
    NoticeDialogListener mListener;

    // Override the Fragment.onAttach() method to instantiate the NoticeDialogListener
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        // Verify that the host activity implements the callback interface
        try {
            // Instantiate the NoticeDialogListener so we can send events to the host
            mListener = (NoticeDialogListener) activity;
        } catch (ClassCastException e) {
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException(activity.toString()
                    + " must implement NoticeDialogListener");
        }

        // Get add or edit mode
        bundle = this.getArguments();
        if (bundle != null) {
            ADD_OR_EDIT_MODE = bundle.getInt(MODE_KEY, MODE_ADD);

        } else { // Default Mode todo - is this even possible?
            ADD_OR_EDIT_MODE = MODE_ADD;
        }
        Log.d(TAG, "MODE: " + ADD_OR_EDIT_MODE);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Build the dialog and set up the button click handlers
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();

        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        view = inflater.inflate(R.layout.add_edit_birthday_fragment, null);

        // Get DatePicker reference and hide year picker
        final DatePicker datePicker = (DatePicker) view.findViewById(R.id.datePicker);
        datePicker.findViewById(Resources.getSystem().getIdentifier("year", "id", "android")).setVisibility(View.GONE);

        // Set current name and birth date if in Edit mode
        if (ADD_OR_EDIT_MODE == MODE_EDIT) {
            EditText editText = (EditText) view.findViewById(R.id.editTextName);
            editText.setText(bundle.getString(NAME_KEY));
            // Move cursor to end of text
            editText.setSelection(editText.getText().length());

            datePicker.updateDate(2015, bundle.getInt(DATE_KEY), bundle.getInt(MONTH_KEY)); // TODO!!!! 2015 is irrelevent???  no effect?
        }

        // Set view and add buttons
        builder.setView(view)
                // Add action buttons
                .setPositiveButton(R.string.button_positive, null)
                .setNegativeButton(R.string.button_negative, null)
                .setTitle(getDialogTitle());

        return builder.create();
    }

    /**
     * Use onStart to set Button clickListener so we can prevent Dialog from closing until name is entered
     */
    @Override
    public void onStart() {
        super.onStart();    //super.onStart() is where dialog.show() is actually called on the underlying dialog, so we have to do it after this point
        final AlertDialog d = (AlertDialog) getDialog();

        final EditText editText = (EditText) view.findViewById(R.id.editTextName);
        final DatePicker datePicker = (DatePicker) view.findViewById(R.id.datePicker);

        if (d != null) {
            final Button positiveButton = (Button) d.getButton(Dialog.BUTTON_POSITIVE);
            positiveButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    // Send the positive button event back to the host activity
                    if (editText.getText().toString().length() != 0) {

                        int dateOfMonth = datePicker.getDayOfMonth();
                        int month = datePicker.getMonth();

                        if (ADD_OR_EDIT_MODE == MODE_EDIT) {
                            // Delete old birthday from array (MA.birthdaylist array) todo - FIX this when reordering occurs
                            MainActivity.birthdaysList.remove(bundle.getInt(POSITION_KEY));
                        }

                        mListener.onDialogPositiveClick(AddEditBirthdayFragment.this, editText.getText().toString(), dateOfMonth, month);

                        d.dismiss();

                    } else {
                        Toast.makeText(getActivity(), "No name entered", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    private String getDialogTitle() {
        switch (ADD_OR_EDIT_MODE) {
            case MODE_ADD:
                return getString(R.string.dialog_add);
            case MODE_EDIT:
                return getString(R.string.dialog_edit);
            default:
                return getString(R.string.action_add);
        }
    }
}
