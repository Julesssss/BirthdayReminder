package website.julianrosser.birthdays;


import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

/**
 * A simple {@link Fragment} subclass.
 */
public class AddEditBirthdayFragment extends DialogFragment {

    // Keys for passing birthday information to Dialog
    final static String MODE_KEY = "key_mode";
    final static String DATE_KEY = "key_date";
    final static String MONTH_KEY = "key_month";
    final static String POS_KEY = "key_pos";
    final static String NAME_KEY = "key_position";

    // To check if we are in new birthday mode or editing birthday mode.
    int ADD_OR_EDIT_MODE;
    final static int MODE_ADD = 0;
    final static int MODE_EDIT = 1;

    // Reference to passed bundle when in edit mode todo is this global reference necessary?
    Bundle bundle;

    // Needed as we inflate in onCreate, but access in onStart. Due to Overriding dialog button, onStart is needed.
    View view;

    // Use this instance of the interface to deliver action events
    NoticeDialogListener mListener;

    // Logging tag which displays class name in readable form
    private String TAG = getClass().getSimpleName();


    public AddEditBirthdayFragment() {
        // Required empty public constructor
    }

    // New instance is preferable as we have option to initialize here instead of using passed in params.
    static AddEditBirthdayFragment newInstance() {
        return new AddEditBirthdayFragment();
    }

    /* MainActivity implements this interface in order to receive event callbacks. Passes the
    DialogFragment in case the host needs to query it. */
    public interface NoticeDialogListener {
        void onDialogPositiveClick(website.julianrosser.birthdays.AddEditBirthdayFragment dialog, String name, int date, int month);
    }

    // We override the Fragment.onAttach() method to instantiate NoticeDialogListener and read bundle data
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

        // Detect which state mode Dialog should be in
        bundle = this.getArguments();

        // Null check, then set mode reference
        if (bundle != null) {
            ADD_OR_EDIT_MODE = bundle.getInt(MODE_KEY, MODE_ADD);

        } else { // Fallback to default Mode todo - is this even possible?
            ADD_OR_EDIT_MODE = MODE_ADD;
            Log.e(TAG, "Bundle is null!");
        }
    }

    @NonNull // todo - why?
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Build the dialog and get LayoutInflater
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();

        // Inflate the brilliantly designed layout, passing null as the parent view because its
        // going in the dialog layout // todo - what does this mean?
        view = inflater.inflate(R.layout.add_edit_birthday_fragment, null);

        // Get DatePicker reference and hide year spinner
        final DatePicker datePicker = (DatePicker) view.findViewById(R.id.datePicker);
        datePicker.findViewById(Resources.getSystem().getIdentifier("year", "id", "android")).setVisibility(View.GONE); // todo - why id & android?

        // Set Birthday name and birth date if in Edit mode
        if (ADD_OR_EDIT_MODE == MODE_EDIT) {

            EditText editText = (EditText) view.findViewById(R.id.editTextName);
            editText.setText(bundle.getString(NAME_KEY));

            // Move cursor to end of text
            editText.setSelection(editText.getText().length());

            datePicker.updateDate(2015, bundle.getInt(MONTH_KEY), bundle.getInt(DATE_KEY)); // TODO!!!! 2015 is irrelevent???  no effect?
        }

        // Set view, then add buttons and title
        builder.setView(view)
                // Add action buttons
                .setPositiveButton(R.string.button_positive, null)
                .setNegativeButton(R.string.button_negative, null)
                .setTitle(getDialogTitle());

        return builder.create();
    }

    /**
     * Use onStart to set Button clickListener, which enables us to override the 'Done' button. Then we can
     * stop dialog from being dismissed and the new birthday from being created until the name String is to our liking.
     */
    @Override
    public void onStart() {
        super.onStart();    //super.onStart() is where dialog.show() is actually called on the underlying dialog, so we have to do it after this point

        // References need to be final as they're used in inner class
        final AlertDialog dialog = (AlertDialog) getDialog();
        final EditText editText = (EditText) view.findViewById(R.id.editTextName);
        final DatePicker datePicker = (DatePicker) view.findViewById(R.id.datePicker);

        // Use my custom onFocusChange function. // todo - use view.OnFocus... ? Not View
        View.OnFocusChangeListener onFocusChangeListener = new MyFocusChangeListener();
        editText.setOnFocusChangeListener(onFocusChangeListener);

        // Null check
        if (dialog != null) {

            // This is it! The done button listener which we override onStart to use.
            final Button positiveButton = dialog.getButton(Dialog.BUTTON_POSITIVE);
            positiveButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    // Only allow birthday to be saved and dialog dismissed if String isn't empty
                    if (editText.getText().toString().length() != 0) {

                        // Get date and month from datepicker
                        int dateOfMonth = datePicker.getDayOfMonth();
                        int month = datePicker.getMonth();

                        // If we're editing a previous birthday, delete the old to make way for this replacement
                        if (ADD_OR_EDIT_MODE == MODE_EDIT) {
                            // Delete old birthday from array (MA.birthday list array) todo - FIX this when reordering occurs
                            MainActivity.deleteFromArray(bundle.getInt(POS_KEY));
                        }

                        // Send the positive button event back to MainActivity
                        mListener.onDialogPositiveClick(AddEditBirthdayFragment.this, editText.getText().toString(), dateOfMonth, month);

                        // Finally close the dialog, and breath a sign of relief
                        dialog.dismiss();

                    } else {
                        Toast.makeText(getActivity(), "No name entered", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    /**
     * Custom OnFocusChangeListener which enables focus to be taken away from EditText, so that the
     * soft keyboard can be hidden. // todo - Could use this for DatePicker also?
     */
    private class MyFocusChangeListener implements View.OnFocusChangeListener {

        public void onFocusChange(View view, boolean hasFocus) {

            if (view.getId() == R.id.editTextName && !hasFocus) {
                // Get input manager and hide keyboard
                InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0); // no flags
            }
        }
    }

    /**
     * Return the title of our lovely new DialogFragment
     */
    private String getDialogTitle() {
        switch (ADD_OR_EDIT_MODE) {
            case MODE_EDIT:
                return getString(R.string.dialog_edit);
            default:
                return getString(R.string.dialog_add);
        }
    }
}
