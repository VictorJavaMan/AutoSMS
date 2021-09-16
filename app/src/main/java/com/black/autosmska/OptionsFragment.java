package com.black.autosmska;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.method.ScrollingMovementMethod;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Scroller;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.view.ContextThemeWrapper;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import java.math.RoundingMode;
import java.text.DecimalFormat;

import static com.black.autosmska.MainActivity.db;
import static com.black.autosmska.MainActivity.mainAct;

public class OptionsFragment extends Fragment {

    private Button button_phone_time, button_sms_time, button_info, button_on_off, button_manual;
    private View root;
    private String button_phone_time_text, button_phone_sms_text;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.fragment_options, container, false);
        root.setOnTouchListener(new OnSwipeTouchListener(getContext()) {
            public void onSwipeRight() {
                NavHostFragment.findNavController(OptionsFragment.this).navigate(R.id.action_Option_to_Story);
            }
            public void onSwipeLeft() {
                NavHostFragment.findNavController(OptionsFragment.this).navigate(R.id.action_Option_to_List);
            }
        });
        button_phone_time = root.findViewById(R.id.button_phone_time);
        button_sms_time = root.findViewById(R.id.button_sms_time);
        button_info = root.findViewById(R.id.button_info);
        button_on_off = root.findViewById(R.id.button_on_off);
        button_manual = root.findViewById(R.id.button_manual);
        Cursor query = db.rawQuery("SELECT * FROM 'OptionsTable';", null);
        if (query.moveToFirst()) {
            do {
                if (query.getString(3).equals("0")) {
                    button_on_off.setTextColor(Color.RED);
                    button_on_off.setText(getString(R.string.block_off_text));
                }
                else {
                    button_on_off.setTextColor(Color.GREEN);
                    button_on_off.setText(getString(R.string.block_on_text));
                }
                break;
            }
            while (query.moveToNext());
        }
        button_phone_time_text = button_phone_time.getText().toString();
        button_phone_sms_text = button_sms_time.getText().toString();
        updateTime();
        button_phone_time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(getContext(), R.style.AlertDialogCustom));
                builder.setTitle(getString(R.string.select_time));
                String[] animals = {"0.5 " + getString(R.string.time), "1 " + getString(R.string.time), "3 " + getString(R.string.time), "5 " + getString(R.string.time), "10 " + getString(R.string.time)};
                builder.setItems(animals, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        int millsec = 0;
                        switch (which) {
                            case 0: {
                                millsec = 500;
                                break;
                            }
                            case 1: {
                                millsec = 1000;
                                break;
                            }
                            case 2: {
                                millsec = 3000;
                                break;
                            }
                            case 3: {
                                millsec = 5000;
                                break;
                            }
                            case 4: {
                                millsec = 10000;
                                break;
                            }
                        }
                        Cursor query = db.rawQuery("SELECT * FROM 'OptionsTable';", null);
                        if (query.moveToFirst()) {
                            do {
                                db.execSQL("UPDATE 'OptionsTable' SET time_phone = '" + millsec + "';");
                                break;
                            }
                            while (query.moveToNext());
                        }
                        updateTime();
                    }
                });
                builder.create().show();
            }
        });

        button_sms_time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(getContext(), R.style.AlertDialogCustom));
                builder.setTitle(getString(R.string.select_time));
                String[] animals = {"0.5 " + getString(R.string.time), "1 " + getString(R.string.time), "3 " + getString(R.string.time), "5 " + getString(R.string.time), "10 " + getString(R.string.time)};
                builder.setItems(animals, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        int millsec = 0;
                        switch (which) {
                            case 0: {
                                millsec = 500;
                                break;
                            }
                            case 1: {
                                millsec = 1000;
                                break;
                            }
                            case 2: {
                                millsec = 3000;
                                break;
                            }
                            case 3: {
                                millsec = 5000;
                                break;
                            }
                            case 4: {
                                millsec = 10000;
                                break;
                            }
                        }
                        Cursor query = db.rawQuery("SELECT * FROM 'OptionsTable';", null);
                        if (query.moveToFirst()) {
                            do {
                                db.execSQL("UPDATE 'OptionsTable' SET time_sms = '" + millsec + "';");
                                break;
                            }
                            while (query.moveToNext());
                        }
                        updateTime();
                    }
                });
                builder.create().show();
            }
        });

        button_manual.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog dialog = new AlertDialog.Builder(new ContextThemeWrapper(getContext(), R.style.AlertDialogCustom)).setTitle(getString(R.string.users_manual)).setMessage(getString(R.string.users_body_manual)).setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).show();
                TextView textView = (TextView) dialog.findViewById(android.R.id.message);
                textView.setMaxLines(15);
                textView.setScroller(new Scroller(getContext()));
                textView.setVerticalScrollBarEnabled(true);
                textView.setMovementMethod(new ScrollingMovementMethod());
//                AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(getContext(), R.style.AlertDialogCustom));
//                builder.setView(myScrollView).setTitle(getString(R.string.users_manual)).setMessage(getString(R.string.users_body_manual)).setPositiveButton("ОК", new DialogInterface.OnClickListener() {
//                    public void onClick(DialogInterface dialog, int id) {
//                        dialog.cancel();
//                    }
//                });
//                builder.create().show();
            }
        });

        button_info.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(getContext(), R.style.AlertDialogCustom));
                builder.setTitle(getString(R.string.about_text)).setMessage(getString(R.string.about_body_text)).setPositiveButton("ОК", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                    }
                });
                builder.create().show();
            }
        });

        button_on_off.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Cursor query = db.rawQuery("SELECT * FROM 'OptionsTable';", null);
                if (query.moveToFirst()) {
                    do {
                        if (query.getString(3).equals("1")) {
                            db.execSQL("UPDATE 'OptionsTable' SET block_state = '" + 0 + "';");
                            button_on_off.setTextColor(Color.RED);
                            button_on_off.setText(getString(R.string.block_off_text));
                        }
                        else {
                            db.execSQL("UPDATE 'OptionsTable' SET block_state = '" + 1 + "';");
                            button_on_off.setTextColor(Color.GREEN);
                            button_on_off.setText(getString(R.string.block_on_text));
                        }
                        break;
                    }
                    while (query.moveToNext());
                }
                ((MainActivity)getActivity()).stopService(new Intent((MainActivity)getActivity(), SmsBlockService.class));
                ((MainActivity)getActivity()).startService(new Intent((MainActivity)getActivity(), SmsBlockService.class));
            }
        });

        return root;
    }

    private void updateTime() {
        Cursor query = db.rawQuery("SELECT * FROM 'OptionsTable';", null);
        if (query.moveToFirst()) {
            do {
                DecimalFormat format = new DecimalFormat("0.##");
                format.setRoundingMode(RoundingMode.DOWN);
                String str = button_phone_time_text + "\n" + getString(R.string.current_time_text) + " " + format.format(Float.parseFloat(query.getString(1)) / 1000) + " " + getString(R.string.time);                if (!str.equals("")) {
                    Spannable spans = new SpannableString(str);
                    spans.setSpan(new ForegroundColorSpan(Color.WHITE), 0, str.indexOf("\n"), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    spans.setSpan(new ForegroundColorSpan(Color.GREEN), str.indexOf("\n") + 1, str.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    button_phone_time.setText(spans);
                }
                str = button_phone_sms_text + "\n" + getString(R.string.current_time_text) + " " + format.format(Float.parseFloat(query.getString(2)) / 1000) + " " + getString(R.string.time);
                if (!str.equals("")) {
                    Spannable spans = new SpannableString(str);
                    spans.setSpan(new ForegroundColorSpan(Color.WHITE), 0, str.indexOf("\n"), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    spans.setSpan(new ForegroundColorSpan(Color.GREEN), str.indexOf("\n") + 1, str.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    button_sms_time.setText(spans);
                }
                break;
            }
            while (query.moveToNext());
        }
    }
}