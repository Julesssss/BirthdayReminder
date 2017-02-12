package website.julianrosser.birthdays.fragments.DialogFragments;


import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import website.julianrosser.birthdays.R;
import website.julianrosser.birthdays.model.Birthday;

public class ItemOptionsFragment extends DialogFragment {

    private static Birthday sBirthday;

    final int DIALOG_WIDTH_SIZE = 220;

    // Use this instance of the interface to deliver action events
    static ItemOptionsListener mListener;

    public ItemOptionsFragment() {
        // Required empty public constructor
    }

    // New instance is preferable as we have option to initialize here instead of using passed in params.
    public static ItemOptionsFragment newInstance(Birthday birthday) {

        ItemOptionsFragment itemOptionsFragment = new ItemOptionsFragment();

        // Get selected birthday's title
        sBirthday = birthday;

        return itemOptionsFragment;
    }

    /* BirthdayListActivity implements this interface in order to receive event callbacks. Passes the
    DialogFragment in case the host needs to query it. */
    public interface ItemOptionsListener {
        void onItemEdit(ItemOptionsFragment dialog, Birthday birthday);

        void onItemDelete(ItemOptionsFragment dialog, Birthday birthday);

        void onItemToggleAlarm(ItemOptionsFragment dialog, Birthday birthday);
    }

    // We override the Fragment.onAttach() method to instantiate ItemOptionListener and read bundle data
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        // Verify that the host activity implements the callback interface
        try {
            // Instantiate the ItemOptionListener so we can send events to the host
            mListener = (ItemOptionsListener) activity;
        } catch (ClassCastException e) {
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException(activity.toString()
                    + " must implement ItemOptions listener");
        }
    }

    // Set background colour and dialog width
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        // Set background depending on theme
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getContext());

        if (prefs.getString(getResources().getString(R.string.pref_theme_key), "0").equals("0")) {
            getDialog().getWindow().setBackgroundDrawable(ContextCompat.getDrawable(getActivity().getApplicationContext(), R.drawable.dialog_background_blue));
        } else if (prefs.getString(getResources().getString(R.string.pref_theme_key), "0").equals("1")) {
            getDialog().getWindow().setBackgroundDrawable(ContextCompat.getDrawable(getActivity().getApplicationContext(), R.drawable.dialog_background_pink));
        } else {
            getDialog().getWindow().setBackgroundDrawable(ContextCompat.getDrawable(getActivity().getApplicationContext(), R.drawable.dialog_background_green));
        }

//        getDialog().getWindow().setLayout(Utils.getPixelsFromDP(getActivity(), DIALOG_WIDTH_SIZE), ViewGroup.LayoutParams.WRAP_CONTENT);
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

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(
                new ContextThemeWrapper(getActivity(), R.style.DialogFragmentTheme));
        LayoutInflater inflater = getActivity().getLayoutInflater();

        // Inflate the brilliantly designed layout, passing null as the parent view
        ListView listView = (ListView) inflater.inflate(R.layout.item_edit_fragment, null);

        // Create adapter using custom class
        OptionListAdapter adapter = new OptionListAdapter(getActivity(),
                getResources().getStringArray(R.array.item_menu_array));
        listView.setAdapter(adapter);

        // Set click listener
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0:
                        mListener.onItemEdit(ItemOptionsFragment.this, sBirthday);
                        break;
                    case 1:
                        mListener.onItemDelete(ItemOptionsFragment.this, sBirthday);
                        break;
                    case 2:
                        mListener.onItemToggleAlarm(ItemOptionsFragment.this, sBirthday);
                        break;
                }
            }
        });

        // Pass custom view to Dialog builder
        builder.setView(listView);

        // Set title to name of selected birthday
        builder.setTitle(sBirthday.getName());

        return builder.create();
    }

    // Custom Adapter for displaying custom context menu and icons
    public class OptionListAdapter extends BaseAdapter {

        String[] result;
        Context context;
        int[] imageIcons = {R.drawable.ic_edit_white_24dp,
                R.drawable.ic_delete_white_24dp, R.drawable.ic_alarm_on_white_24dp, R.drawable.ic_alarm_off_white_24dp};

        private LayoutInflater inflater = null;

        public OptionListAdapter(Context birthdayListActivity, String[] stringArray) {

            result = stringArray;
            context = birthdayListActivity;
            inflater = (LayoutInflater) context.
                    getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public int getCount() {
            return result.length;
        }

        @Override
        public Object getItem(int position) {
            return result[position];
        }

        @Override
        public long getItemId(int position) {
            return result[position].hashCode();
        }

        public class ItemOptionHolder {
            TextView textView;
            ImageView imageIcon;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ItemOptionHolder itemOptionHolder = new ItemOptionHolder();

            View rowView = inflater.inflate(R.layout.item_edit_fragment_list, null);

            itemOptionHolder.textView = (TextView) rowView.findViewById(R.id.option_list_textview);
            itemOptionHolder.textView.setText(result[position]);

            // Get references
            itemOptionHolder.imageIcon = (ImageView) rowView.findViewById(R.id.imageView);

            if (position == 2) {
                itemOptionHolder.imageIcon.setImageDrawable(sBirthday.getRemindAlarmDrawable());
            } else {
                itemOptionHolder.imageIcon.setImageDrawable(ContextCompat.getDrawable(getActivity().getApplicationContext(), imageIcons[position]));
            }

            return rowView;
        }
    }
}