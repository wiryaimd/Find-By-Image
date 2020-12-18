package com.wiryaimd.findbyimage.util;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.wiryaimd.findbyimage.R;

public class DialogMessage {

    public static void showMessage(Context context, LayoutInflater inflater, String msg){
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        View dialogview = inflater.inflate(R.layout.dialog_showmessage, null, false);
        TextView tvmsg = dialogview.findViewById(R.id.dialogmsg_msg);
        Button btnok = dialogview.findViewById(R.id.dialogmsg_btnok);
        tvmsg.setText(msg);
        builder.setView(dialogview);
        final AlertDialog dialog = builder.create();

        btnok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();

    }

}
