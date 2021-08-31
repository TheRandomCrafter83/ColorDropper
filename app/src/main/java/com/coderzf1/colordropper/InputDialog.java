package com.coderzf1.colordropper;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Build;
import android.renderscript.ScriptGroup;
import android.text.Html;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;

public class InputDialog {
    interface InputDialogCallback{
        void onOkClick(String result);
        void onCancelClick();
    }

    private InputDialogCallback callback;
    private Context context;

    public InputDialog(Context context){
        this.context = context;
    }
    public void setInputDialogCallback(InputDialogCallback callback){
        this.callback = callback;
    }


    public void showInputDialog (final String message, final String title, final String default_text, final Color dialogBgColor, final Color dialogTextColor) {
        android.app.AlertDialog dlg;
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(context);
        if (!title.equals("")){
            builder.setTitle(title);
        }else{
            builder.setTitle(context.getApplicationInfo().name);
        }

        //create the layout
        LinearLayout dlgLayout = new LinearLayout(context);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dlgLayout.setLayoutParams(params);
        dlgLayout.setOrientation(LinearLayout.VERTICAL);
        dlgLayout.setBackgroundColor(dialogBgColor.toArgb());
        final EditText userInput = new EditText(context);
        if (!TextUtils.isEmpty(default_text)){
            userInput.setText(default_text);
        }
        //_SetCompoundDrawable(userInput, 0, "url");
        userInput.setCompoundDrawables(context.getResources().getDrawable(R.drawable.ic_baseline_language_24,null),null,context.getResources().getDrawable(R.drawable.clear,null),null);
        //_SetCompoundDrawable(userInput, 2, "clear");
        userInput.setCompoundDrawablePadding(16);
        userInput.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                final int DRAWABLE_LEFT = 0;
                final int DRAWABLE_TOP = 1;
                final int DRAWABLE_RIGHT = 2;
                final int DRAWABLE_BOTTOM = 3;
                if(event.getAction() == MotionEvent.ACTION_UP) {
                    if(event.getRawX() >= (userInput.getRight() - userInput.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width()-userInput.getPaddingRight())) {
                        userInput.setText("");
                        return true;
                    }
                }
                return false;
            }
        });
        LinearLayout.LayoutParams tlp = new LinearLayout.LayoutParams(android.widget.LinearLayout.LayoutParams.MATCH_PARENT, android.widget.LinearLayout.LayoutParams.WRAP_CONTENT);
        int m = 32 * (int) Resources.getSystem().getDisplayMetrics().density;
        tlp.setMargins(m,m/4,m,m/4);
        userInput.setLayoutParams(tlp);
        userInput.setPadding(m/2,m/2,m/2,m/2);
        userInput.setTextSize(18);
        android.graphics.drawable.GradientDrawable gd = new android.graphics.drawable.GradientDrawable();
        gd.setCornerRadius(8);
        gd.setColor(Color.parseColor("#ffffffff"));
        gd.setStroke(2,Color.BLACK);
        userInput.setBackground(gd);
        userInput.setHint(message);

        final TextView txtMessage = new TextView(context);
        if (!message.equals("")){
            int r = Color.red(dialogTextColor.toArgb());
            int g = Color.green(dialogTextColor.toArgb());
            int b = Color.blue(dialogTextColor.toArgb());
            String hex = String.format("#%02x%02x%02x", r, g, b);
            String messageHtml = "<font color='" + hex + "'>" + message + "</font>";
            txtMessage.setText(Html.fromHtml(messageHtml, Html.FROM_HTML_MODE_COMPACT));
        }else{
            builder.setMessage("Enter some text.");
        }
        txtMessage.setTextColor(dialogTextColor.toArgb());
        txtMessage.setTextSize(18f);
        txtMessage.setLayoutParams(tlp);

        TextView txtTitle = new TextView(context);
        txtTitle.setLayoutParams(tlp);
        txtTitle.setPadding(m/2,m/2,m/2,m/2);
        txtTitle.setText(title);
        txtTitle.setTextColor(dialogTextColor.toArgb());
        txtTitle.setTypeface(txtTitle.getTypeface(),android.graphics.Typeface.BOLD);
        txtTitle.setTextSize(20f);

        builder.setCustomTitle(txtTitle);
        dlgLayout.addView(txtMessage);
        dlgLayout.addView(userInput);
        builder.setView(dlgLayout);



        //create the button callbacks
        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface _dialog, int _which) {
                //_inputDialogOkClick(userInput.getText().toString());
                callback.onOkClick(userInput.getText().toString());
            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface _dialog, int _which) {
                //_inputDlgCancelClick();
                callback.onCancelClick();
            }
        });
        dlg = builder.create();
        dlg.show();
        gd = new android.graphics.drawable.GradientDrawable();
        gd.setCornerRadius(20);
        gd.setColor(dialogBgColor.toArgb());
        gd.setStroke(2,Color.BLACK);
        dlg.getWindow().getDecorView().getBackground().setColorFilter(new android.graphics.LightingColorFilter(0xff000000,dialogBgColor.toArgb()));
        dlg.getWindow().getDecorView().setBackground(gd);
        dlg.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(dialogTextColor.toArgb());
        dlg.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(dialogTextColor.toArgb());
    }

}
