package com.coderzf1.colordropper.ui.colorpicker;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.LightingColorFilter;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.text.Html;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.ColorInt;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;

import com.coderzf1.colordropper.R;

public class InputDialog {
    @SuppressWarnings("unused")
    interface InputDialogCallback{
        void onOkClick(String result);
        void onCancelClick();
    }

    private InputDialogCallback callback;
    private final Context context;

    public InputDialog(Context context){
        this.context = context;
    }
    public void setInputDialogCallback(InputDialogCallback callback){
        this.callback = callback;
    }


    @SuppressLint("ClickableViewAccessibility")
    public void showInputDialog (final String message, final String title, final String default_text, @ColorInt final int dialogBgColor, @ColorInt final int dialogTextColor) {
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
        dlgLayout.setBackgroundColor(dialogBgColor);
        final EditText userInput = new EditText(context);
        if (!TextUtils.isEmpty(default_text)){
            userInput.setText(default_text);
        }
        //_SetCompoundDrawable(userInput, 0, "url");

        userInput.setCompoundDrawables(ContextCompat.getDrawable(context,R.drawable.ic_baseline_language_24),null,ContextCompat.getDrawable(context,R.drawable.clear),null);
        //_SetCompoundDrawable(userInput, 2, "clear");
        userInput.setCompoundDrawablePadding(16);
        userInput.setOnTouchListener((v, event) -> {
            final int DRAWABLE_RIGHT = 2;
            if(event.getAction() == MotionEvent.ACTION_UP) {
                if(event.getRawX() >= (userInput.getRight() - userInput.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width()-userInput.getPaddingRight())) {
                    userInput.setText("");
                    return true;
                }
            }
            return false;
        });
        LinearLayout.LayoutParams tlp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        int m = 32 * (int) Resources.getSystem().getDisplayMetrics().density;
        tlp.setMargins(m,m/4,m,m/4);
        userInput.setLayoutParams(tlp);
        userInput.setPadding(m/2,m/2,m/2,m/2);
        userInput.setTextSize(18);
        GradientDrawable gd = new GradientDrawable();
        gd.setCornerRadius(8);
        gd.setColor(Color.parseColor("#ffffffff"));
        gd.setStroke(2,Color.BLACK);
        userInput.setBackground(gd);
        userInput.setHint(message);

        final TextView txtMessage = new TextView(context);
        if (!message.equals("")){
            int r = Color.red(dialogTextColor);
            int g = Color.green(dialogTextColor);
            int b = Color.blue(dialogTextColor);
            String hex = String.format("#%02x%02x%02x", r, g, b);
            String messageHtml = "<font color='" + hex + "'>" + message + "</font>";
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                txtMessage.setText(Html.fromHtml(messageHtml, Html.FROM_HTML_MODE_COMPACT));
            } else {
                txtMessage.setTextColor(dialogTextColor);
                txtMessage.setText(message);
            }
        }else{
            builder.setMessage("Enter some text.");
        }
        txtMessage.setTextColor(dialogTextColor);
        txtMessage.setTextSize(18f);
        txtMessage.setLayoutParams(tlp);

        TextView txtTitle = new TextView(context);
        txtTitle.setLayoutParams(tlp);
        txtTitle.setPadding(m/2,m/2,m/2,m/2);
        txtTitle.setText(title);
        txtTitle.setTextColor(dialogTextColor);
        txtTitle.setTypeface(txtTitle.getTypeface(), Typeface.BOLD);
        txtTitle.setTextSize(20f);

        builder.setCustomTitle(txtTitle);
        dlgLayout.addView(txtMessage);
        dlgLayout.addView(userInput);
        builder.setView(dlgLayout);



        //create the button callbacks
        builder.setPositiveButton("Ok", (_dialog, _which) -> {
            //_inputDialogOkClick(userInput.getText().toString());
            callback.onOkClick(userInput.getText().toString());
        });

        builder.setNegativeButton("Cancel", (_dialog, _which) -> {
            //_inputDlgCancelClick();
            callback.onCancelClick();
        });
        dlg = builder.create();
        dlg.show();
        gd = new GradientDrawable();
        gd.setCornerRadius(20);
        gd.setColor(dialogBgColor);
        gd.setStroke(2,Color.BLACK);
        dlg.getWindow().getDecorView().getBackground().setColorFilter(new LightingColorFilter(0xff000000,dialogBgColor));
        dlg.getWindow().getDecorView().setBackground(gd);
        dlg.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(dialogTextColor);
        dlg.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(dialogTextColor);
    }

}
