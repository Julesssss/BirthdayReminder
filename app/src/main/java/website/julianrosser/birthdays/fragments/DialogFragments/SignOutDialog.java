package website.julianrosser.birthdays.fragments.DialogFragments;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;

import website.julianrosser.birthdays.R;

public class SignOutDialog extends DialogFragment {

    private SignOutCallback signOutCallback;

    public interface SignOutCallback {
        void onClicked();
    }

    public static SignOutDialog newInstance(SignOutCallback signOutCallback) {
       SignOutDialog dialog = new SignOutDialog();
       dialog.setCallback(signOutCallback);
       return dialog;
    }

    private void setCallback(SignOutCallback signOutCallback) {
        this.signOutCallback = signOutCallback;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(getString(R.string.dialog_sign_out_message))
                .setTitle(getString(R.string.dialog_sign_out_title))
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        signOutCallback.onClicked();
                    }
                })
                .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) { }
                });
        // Create the AlertDialog object and return it
        return builder.create();
    }
}