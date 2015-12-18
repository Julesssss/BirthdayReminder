package website.julianrosser.birthdays.DialogFragments;


import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.view.ContextThemeWrapper;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import website.julianrosser.birthdays.MainActivity;
import website.julianrosser.birthdays.R;

public class ItemOptionsFragment extends DialogFragment {

    // Logging tag which displays class name in readable form
    private String TAG = getClass().getSimpleName();

    final int DIALOG_WIDTH_SIZE = 200;

    // Use this instance of the interface to deliver action events
    static ItemOptionsListener mListener;

    // Reference to selected Birthday's position in Array
    static int birthdayListPosition;

    // String reference which we use for title
    static String titleName;

    public ItemOptionsFragment() {
        // Required empty public constructor
    }

    // New instance is preferable as we have option to initialize here instead of using passed in params.
    public static ItemOptionsFragment newInstance(int position) {

        ItemOptionsFragment itemOptionsFragment = new ItemOptionsFragment();

        // We need reference to selected birthday for passing back to MainActivity
        birthdayListPosition = position;

        // Get selected birthday's title
        titleName = MainActivity.birthdaysList.get(birthdayListPosition).getName();

        return itemOptionsFragment;
    }


    /* MainActivity implements this interface in order to receive event callbacks. Passes the
    DialogFragment in case the host needs to query it. */
    public interface ItemOptionsListener {
        void onItemEdit(ItemOptionsFragment dialog, int position);

        void onItemDelete(ItemOptionsFragment dialog, int position);

        void onItemToggleAlarm(ItemOptionsFragment dialog, int position);
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

    // Set background colour and dialog width
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        getDialog().getWindow().setBackgroundDrawable(getResources().getDrawable(R.drawable.dialog_background));
        getDialog().getWindow().setLayout(getPixelsFromDP(DIALOG_WIDTH_SIZE), ViewGroup.LayoutParams.WRAP_CONTENT);
        // To ensure data is kept through orientation change
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

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(
                new ContextThemeWrapper(getActivity(), R.style.DialogFragmentTheme));

        LayoutInflater inflater = getActivity().getLayoutInflater();

        // Inflate the brilliantly designed layout, passing null as the parent view
        ListView listView = (ListView) inflater.inflate(R.layout.item_edit_fragment, null);

        // Create adapter using custom class
        OptionListAdapter adapter = new OptionListAdapter(MainActivity.getContext(),
                getResources().getStringArray(R.array.item_menu_array));
        listView.setAdapter(adapter);

        // Set click listener
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0:
                        mListener.onItemEdit(ItemOptionsFragment.this, birthdayListPosition);
                        break;
                    case 1:
                        mListener.onItemDelete(ItemOptionsFragment.this, birthdayListPosition);
                        break;
                    case 2:
                        mListener.onItemToggleAlarm(ItemOptionsFragment.this, birthdayListPosition);
                        break;
                }
            }
        });

        // Pass custom view to Dialog builder
        builder.setView(listView);

        // Set title to name of selected birthday
        builder.setTitle(titleName);

        return builder.create();
    }

    // Custom Adapter for displaying custom context menu and icons
    public class OptionListAdapter extends BaseAdapter {

        String[] result;
        Context context;
        int[] imageIcons = {R.drawable.ic_edit_white_24dp,
                R.drawable.ic_delete_white_24dp, R.drawable.ic_alarm_on_white_24dp, R.drawable.ic_alarm_off_white_24dp};

        private LayoutInflater inflater = null;

        public OptionListAdapter(MainActivity mainActivity, String[] stringArray) {

            result = stringArray;
            context = mainActivity;
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

        public class Holder {
            TextView textView;
            ImageView imageIcon;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            Holder holder = new Holder();

            View rowView = inflater.inflate(R.layout.item_edit_fragment_list, null);

            holder.textView = (TextView) rowView.findViewById(R.id.option_list_textview);
            holder.textView.setText(result[position]);

            // Get references
            holder.imageIcon = (ImageView) rowView.findViewById(R.id.imageView);

            if (position == 2) {
                holder.imageIcon.setImageDrawable(MainActivity.birthdaysList.get(birthdayListPosition).getRemindAlarmDrawable());
            } else {
                    holder.imageIcon.setImageDrawable(getResources().getDrawable(imageIcons[position]));
            }

            return rowView;
        }
    }
}