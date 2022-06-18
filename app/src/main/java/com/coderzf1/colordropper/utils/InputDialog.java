package com.coderzf1.colordropper.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Build;
import android.text.Html;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.webkit.URLUtil;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.ColorInt;
import androidx.appcompat.app.AlertDialog;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;

import com.coderzf1.colordropper.R;
import com.coderzf1.colordropper.databinding.InputDialogBinding;
import com.coderzf1.colordropper.ui.colorpicker.listeners.CompoundDrawableClickListener;
import com.coderzf1.colordropper.ui.colorpicker.utils.Utils;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

public class InputDialog {
    @SuppressWarnings("unused")
    public interface InputDialogCallback{
        void onOkClick(String result);
        void onCancelClick();
    }

    private final InputDialogBinding binding;

    private InputDialogCallback callback;
    private final Context context;

    public InputDialog(Context context){
        this.context = context;
        binding = InputDialogBinding.inflate(LayoutInflater.from(context));
    }
    public void setInputDialogCallback(InputDialogCallback callback){
        this.callback = callback;
    }


    @SuppressLint("ClickableViewAccessibility")
    public void showInputDialog (final String message , final String title, final String default_text, @ColorInt final int dialogBgColor, @ColorInt final int dialogTextColor) {
        AlertDialog dlg;
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(context);
        if (!title.equals("")){
            builder.setTitle(title);
        }else{
            builder.setTitle(context.getApplicationInfo().name);
        }
        //create the layout
        ConstraintLayout dlgLayout = binding.getRoot();
        dlgLayout.setBackgroundColor(dialogBgColor);
        final EditText userInput = binding.textViewUserInput;
        if (!TextUtils.isEmpty(default_text)){
            userInput.setText(default_text);
        }
        userInput.setOnTouchListener(new CompoundDrawableClickListener() {
            @Override
            protected void onDrawableClick(View v, int drawableIndex) {
                if (drawableIndex == 2) {
                    userInput.setText("");
                }
            }
        });
        userInput.setHint(message);
        final TextView txtMessage = binding.textViewMessage;
        if (!message.equals("")){
            String hex = Utils.colorIntToHexString(dialogTextColor);
            String messageHtml = "<font color='" + hex + "'>" + message + "</font>";
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                txtMessage.setText(Html.fromHtml(messageHtml, Html.FROM_HTML_MODE_COMPACT));
            } else {
                txtMessage.setTextColor(dialogTextColor);
                txtMessage.setText(message);
            }
        }else{
            txtMessage.setText(R.string.input_dialog_default_text);
        }
        txtMessage.setTextColor(dialogTextColor);
        TextView txtTitle = binding.textViewTitle;
        txtTitle.setText(title);
        builder.setTitle("");
        builder.setView(dlgLayout);
        //create the button callbacks

        builder.setPositiveButton("Ok", (_dialog, _which) -> callback.onOkClick(userInput.getText().toString()));
        builder.setPositiveButton("Ok", (dialogInterface, i) -> {
            if(!isValidUrl(userInput.getText().toString())){
                Toast.makeText(context,"Please enter a valid url",Toast.LENGTH_SHORT).show();
                return;
            }
            callback.onOkClick(userInput.getText().toString());
        });
        builder.setNegativeButton("Cancel", (_dialog, _which) -> callback.onCancelClick());
        dlg = builder.create();
        dlg.show();
        dlg.getWindow().getDecorView().setBackground(ContextCompat.getDrawable(context,R.drawable.rounded_rect));
        dlg.getWindow().getDecorView().setPadding(20,20,20,20);
        dlg.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(dialogTextColor);
        dlg.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(dialogTextColor);
    }

    private boolean isValidUrl(String url){
        return URLUtil.isValidUrl(url)&& Patterns.WEB_URL.matcher(url).matches();
    }

}
