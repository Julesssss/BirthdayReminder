package website.julianrosser.birthdays.DialogFragments;


import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.view.ContextThemeWrapper;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import java.util.Calendar;

import website.julianrosser.birthdays.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class AddEditFragment extends DialogFragment {

    // Keys for passing birthday information to Dialog
    public final static String MODE_KEY = "key_mode";
    public final static String DATE_KEY = "key_date";
    public final static String MONTH_KEY = "key_month";
    public final static String SHOW_YEAR_KEY = "key_show_year";
    public final static String YEAR_KEY = "key_year";
    public final static String POS_KEY = "key_pos";
    public final static String NAME_KEY = "key_position";

    // To check if we are in new birthday mode or editing birthday mode.
    int ADD_OR_EDIT_MODE;
    public final static int MODE_ADD = 0;
    public final static int MODE_EDIT = 1;

    final int DIALOG_WIDTH_SIZE = 280;

    // Reference to passed bundle when in edit mode
    Bundle bundle;

    // Needed as we inflate in onCreate, but access in onStart. Due to Overriding dialog button, onStart is needed.
    View view;

    // Use this instance of the interface to deliver action events
    NoticeDialogListener mListener;
    private CheckBox checkYearToggle;

    public AddEditFragment() {
        // Required empty public constructor
    }

    // New instance is preferable as we have option to initialize here instead of using passed in params.
    public static AddEditFragment newInstance() {
        return new AddEditFragment();
    }

    /* MainActivity implements this interface in order to receive event callbacks. Passes the
    DialogFragment in case the host needs to query it. */
    public interface NoticeDialogListener {
        void onDialogPositiveClick(AddEditFragment dialog, String name, int date, int month, int year, boolean includeYear, int AddEditMode, int position);
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

        } else { // Fallback to default mode
            ADD_OR_EDIT_MODE = MODE_ADD;
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        // Build the dialog and get LayoutInflater
        AlertDialog.Builder builder = new AlertDialog.Builder(
                new ContextThemeWrapper(getActivity(), R.style.DialogFragmentTheme));
        LayoutInflater inflater = getActivity().getLayoutInflater();

        // Inflate the brilliantly designed layout, passing null as the parent view because its
        // going in the dialog layout
        view = inflater.inflate(R.layout.add_edit_birthday_fragment, null);
        // Get DatePicker reference and hide year spinner
        final DatePicker datePicker = (DatePicker) view.findViewById(R.id.datePicker);
        setUpDatePicker(datePicker);

        checkYearToggle = (CheckBox) view.findViewById(R.id.checkboxShowYear);
        checkYearToggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                setYearFieldVisibility(isChecked, datePicker);
            }
        });

        // Set Birthday name and birth date if in Edit mode
        if (ADD_OR_EDIT_MODE == MODE_EDIT) {

            boolean showYear = bundle.getBoolean(SHOW_YEAR_KEY, true);
            checkYearToggle.setChecked(showYear);
            setYearFieldVisibility(showYear, datePicker);

            EditText editText = (EditText) view.findViewById(R.id.editTextName);
            editText.setText(bundle.getString(NAME_KEY));

            // Move cursor to end of text
            editText.setSelection(editText.getText().length());

            // Set DatePicker
            int spinnerYear =  bundle.getInt(YEAR_KEY);
            int spinnerMonth =  bundle.getInt(MONTH_KEY);
            int spinnerDate =  bundle.getInt(DATE_KEY);
            datePicker.updateDate(spinnerYear, spinnerMonth, spinnerDate);
        }

        // Set view, then add buttons and title
        builder.setView(view)
                // Add action buttons
                .setPositiveButton(R.string.button_positive, null)
                .setNegativeButton(R.string.button_negative, null)
                .setTitle(getDialogTitle());

        return builder.create();
    }

    private void setUpDatePicker(final DatePicker datePicker) {
        Calendar today = Calendar.getInstance();
        setYearFieldVisibility(false, datePicker);
        datePicker.init(2000, today.get(Calendar.MONTH), today.get(Calendar.DATE), new DatePicker.OnDateChangedListener() {
            @Override
            public void onDateChanged(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(datePicker.getWindowToken(), 0);
            }
        });
    }

    private void setYearFieldVisibility(boolean isChecked, DatePicker datePicker) {
        if (isChecked) {
            datePicker.findViewById(Resources.getSystem().getIdentifier("year", "id", "android")).setVisibility(View.VISIBLE);
        } else {
            datePicker.findViewById(Resources.getSystem().getIdentifier("year", "id", "android")).setVisibility(View.GONE);
        }
    }

    // Set background colour and dialog width
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        getDialog().getWindow().setLayout(getPixelsFromDP(DIALOG_WIDTH_SIZE), ViewGroup.LayoutParams.WRAP_CONTENT);

        setRetainInstance(true);
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    // This is needed as bug in Compat library destroys Fragment on Activity rotation
    @Override
    public void onDestroyView() {
        if (getDialog() != null && getRetainInstance())
            getDialog().setDismissMessage(null);
        super.onDestroyView();
    }

    // Helper method for getting exact pixel size for device from density independent pixels
    public int getPixelsFromDP(int px) {
        Resources r = getResources();
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, px, r.getDisplayMetrics());
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

        // Use my custom onFocusChange function.
        View.OnFocusChangeListener onFocusChangeListener = new MyFocusChangeListener();
        editText.setOnFocusChangeListener(onFocusChangeListener);

        // Set background depending on theme
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getContext());

        if (prefs.getString(getResources().getString(R.string.pref_theme_key), "0").equals("0")) {
            getDialog().getWindow().setBackgroundDrawable(ContextCompat.getDrawable(getActivity().getApplicationContext(), R.drawable.dialog_background_blue));
            // Set buttons accent colour
            ((AlertDialog) getDialog()).getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(ContextCompat.getColor(getActivity().getApplicationContext(), R.color.material_lime_500));
            ((AlertDialog) getDialog()).getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(ContextCompat.getColor(getActivity().getApplicationContext(), R.color.material_lime_500));
        } else if (prefs.getString(getResources().getString(R.string.pref_theme_key), "0").equals("1")) {
            getDialog().getWindow().setBackgroundDrawable(ContextCompat.getDrawable(getActivity().getApplicationContext(), R.drawable.dialog_background_pink));
            // Set buttons accent colour
            ((AlertDialog) getDialog()).getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(ContextCompat.getColor(getActivity().getApplicationContext(), R.color.blue_accent_400));
            ((AlertDialog) getDialog()).getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(ContextCompat.getColor(getActivity().getApplicationContext(), R.color.blue_accent_400));
        } else {
            getDialog().getWindow().setBackgroundDrawable(ContextCompat.getDrawable(getActivity().getApplicationContext(), R.drawable.dialog_background_green));
            // Set buttons accent colour
            ((AlertDialog) getDialog()).getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(ContextCompat.getColor(getActivity().getApplicationContext(), R.color.blue_accent_700));
            ((AlertDialog) getDialog()).getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(ContextCompat.getColor(getActivity().getApplicationContext(), R.color.blue_accent_700));
        }

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
                        int year = datePicker.getYear();
                        boolean includeYear = checkYearToggle.isChecked();

                        // Send the positive button event back to MainActivity
                        mListener.onDialogPositiveClick(AddEditFragment.this, editText.getText().toString(),
                                dateOfMonth, month, year, includeYear,
                                ADD_OR_EDIT_MODE, bundle.getInt(POS_KEY));

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
     * soft keyboard can be hidden.
     */
    private class MyFocusChangeListener implements View.OnFocusChangeListener {

        public void onFocusChange(View view, boolean hasFocus) {

            if ((view.getId() == R.id.editTextName || view.getId() == R.id.datePicker) && !hasFocus) {
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
