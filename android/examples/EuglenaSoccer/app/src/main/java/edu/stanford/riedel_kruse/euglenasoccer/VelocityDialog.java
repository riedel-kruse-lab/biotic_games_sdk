package edu.stanford.riedel_kruse.euglenasoccer;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;

/**
 * Created by honestykim on 6/4/2015.
 */
public class VelocityDialog extends DialogFragment {
    LayoutInflater inflater;    //LayoutInflator instantiates XML file into its corresponding View objects. View is a class for the building blocks of UI
    View v;

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        inflater = getActivity().getLayoutInflater();   //getActivity returns the activity current fragment is associated with. (activity is class that creates window for user to interactwith
        //getLayoutInflater returns instance of layoutinflater corresponding to context
        v = inflater.inflate(R.layout.velocity_experiment_message, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setView(v).setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        }).setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        return builder.create();
    }
}
