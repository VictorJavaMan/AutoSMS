package com.black.autosmska;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.res.ColorStateList;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;

import androidx.appcompat.view.ContextThemeWrapper;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import android.text.InputFilter;
import android.text.InputType;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.function.Predicate;

import static com.black.autosmska.MainActivity.db;
import static com.black.autosmska.MainActivity.key_empty;

public class AddPhoneFragment extends Fragment {

    private Button button_execute, button_delete, button_appendphone, button_appendsms;
    private View root;
    private LinearLayout linearLayoutList, linearLayoutList_;
    private int radio_index = 0;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.fragment_addphone, container, false);
        try {
            root.setOnTouchListener(new OnSwipeTouchListener(getContext()) {
                public void onSwipeRight() {
                    NavHostFragment.findNavController(AddPhoneFragment.this).navigate(R.id.action_Add_to_List);
                }
                public void onSwipeLeft() {
                    NavHostFragment.findNavController(AddPhoneFragment.this).navigate(R.id.action_Add_to_Story);
                }
            });
            button_execute = root.findViewById(R.id.button_execute);
            button_delete = root.findViewById(R.id.button_delete);
            button_appendphone = root.findViewById(R.id.button_appendphone);
            button_appendsms = root.findViewById(R.id.button_appendsms);
            linearLayoutList = root.findViewById(R.id.numLayout);
            linearLayoutList_ = root.findViewById(R.id.numLayout_);

            button_appendphone.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    try {
                        if (linearLayoutList.getChildCount() < 5) {
                            final LinearLayout linearLayout = new LinearLayout(getContext());
                            linearLayout.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT));
                            linearLayout.setOrientation(LinearLayout.HORIZONTAL);
                            linearLayout.setGravity(View.TEXT_ALIGNMENT_CENTER);

                            final EditText editTextPhone = new EditText(view.getContext(), null);
                            editTextPhone.setTextAlignment(View.TEXT_ALIGNMENT_TEXT_START);
                            editTextPhone.setTextSize(20);
                            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT, 0.5f);
                            params.setMargins(0, 5, 0, 5);
                            editTextPhone.setLayoutParams(params);
                            editTextPhone.setHint(getString(R.string.hint_phone_number));
                            editTextPhone.setInputType(InputType.TYPE_CLASS_PHONE);
                            editTextPhone.setFilters(new InputFilter[]{new InputFilter.LengthFilter(14)});
                            editTextPhone.setHintTextColor(getResources().getColor(R.color.colorAccent));
                            editTextPhone.getBackground().setTint(getResources().getColor(R.color.colorAccent));
                            editTextPhone.setTextColor(Color.WHITE);
                            //registerForContextMenu(editText);
                            linearLayout.addView(editTextPhone);

                            final EditText editTextName = new EditText(view.getContext(), null);
                            editTextName.setTextAlignment(View.TEXT_ALIGNMENT_TEXT_START);
                            editTextName.setTextSize(20);
                            LinearLayout.LayoutParams params_ = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT, 0.5f);
                            params_.setMargins(0, 5, 0, 5);
                            editTextName.setLayoutParams(params_);
                            editTextName.setHint(getString(R.string.hint_phone_name));
                            editTextName.setInputType(InputType.TYPE_CLASS_TEXT);
                            editTextName.setFilters(new InputFilter[]{new InputFilter.LengthFilter(30)});
                            editTextName.setHintTextColor(getResources().getColor(R.color.colorAccent));
                            editTextName.getBackground().setTint(getResources().getColor(R.color.colorAccent));
                            editTextName.setTextColor(getResources().getColor(R.color.colorAccent));
                            editTextName.setFocusableInTouchMode(false);
                            editTextName.clearFocus();
                            linearLayout.addView(editTextName);

                            final Button bt_more = new Button(view.getContext());
                            LinearLayout.LayoutParams dparams = new LinearLayout.LayoutParams(50, 50, 0);
                            dparams.gravity = Gravity.CENTER_VERTICAL; //dparams.setMargins(0, 30, 10, 0);
                            bt_more.setLayoutParams(dparams);
                            bt_more.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.ic_baseline_more_vert_24, null));
                            bt_more.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view_) {
                                    showPopupMenu(view_, linearLayout);
                                }
                            });
                            linearLayout.addView(bt_more);

                            linearLayoutList.addView(linearLayout);
                        }
                    }
                    catch (Exception e) {
                        e.fillInStackTrace();
                    }
                }
            });

            button_appendsms.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    try {
                        if (linearLayoutList_.getChildCount() < 5) {
                            final LinearLayout linearLayout = new LinearLayout(getContext());
                            linearLayout.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT));
                            linearLayout.setOrientation(LinearLayout.HORIZONTAL);
                            linearLayout.setGravity(View.TEXT_ALIGNMENT_CENTER);

                            final RadioButton radioButton = new RadioButton(view.getContext(), null);
                            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT, 0.65f);
                            params.setMargins(10, 5, 0, 5);
                            radioButton.setLayoutParams(params);
                            radioButton.setId(linearLayoutList_.getChildCount());
                            ColorStateList colorStateList = new ColorStateList(
                                    new int[][]{
                                            new int[]{-android.R.attr.state_enabled},
                                            new int[]{android.R.attr.state_enabled}
                                    },
                                    new int[] {
                                            getResources().getColor(R.color.colorAccent),
                                            getResources().getColor(R.color.colorAccent)
                                    }
                            );
                            radioButton.setButtonTintList(colorStateList);
                            radioButton.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    for (int i = 0; i < linearLayoutList_.getChildCount(); i++) {
                                        ((RadioButton)((LinearLayout)linearLayoutList_.getChildAt(i)).getChildAt(0)).setChecked(false);
                                    }
                                    radioButton.setChecked(true);
                                    radio_index = radioButton.getId();
                                }
                            });
                            if (linearLayoutList_.getChildCount() <= 0) {
                                radioButton.setChecked(true);
                                radio_index = 0;
                            }

                            final EditText editTextSms = new EditText(view.getContext(), null);
                            editTextSms.setTextAlignment(View.TEXT_ALIGNMENT_TEXT_START);
                            editTextSms.setTextSize(20);
                            LinearLayout.LayoutParams params_ = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT, 0.14f);
                            params_.setMargins(0, 5, 0, 5);
                            editTextSms.setLayoutParams(params_);
                            editTextSms.setHint(getString(R.string.hint_sms));
                            editTextSms.setFilters(new InputFilter[]{new InputFilter.LengthFilter(60)});
                            editTextSms.setHintTextColor(getResources().getColor(R.color.colorAccent));
                            editTextSms.getBackground().setTint(getResources().getColor(R.color.colorAccent));
                            editTextSms.setTextColor(Color.WHITE);

                            final Button bt_del = new Button(view.getContext());
                            LinearLayout.LayoutParams dparams = new LinearLayout.LayoutParams(50, 50, 0);
                            dparams.gravity = Gravity.CENTER_VERTICAL; //dparams.setMargins(0, 30, 10, 0);
                            bt_del.setLayoutParams(dparams);
                            bt_del.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.ic_baseline_close_24, null));
                            bt_del.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view_) {
                                    try {
                                        if (linearLayoutList_.getChildCount() > 1) {
                                            linearLayoutList_.removeView((LinearLayout)bt_del.getParent());
                                            if (radioButton.isChecked())
                                                ((RadioButton)((LinearLayout)linearLayoutList_.getChildAt(0)).getChildAt(0)).setChecked(true);
                                        }
                                        else {
                                            radioButton.setChecked(true);
                                            radio_index = radioButton.getId();
                                            editTextSms.setText("");
                                        }
                                    }
                                    catch (Exception e) {
                                        e.fillInStackTrace();
                                    }
                                }
                            });

                            linearLayout.addView(radioButton);
                            linearLayout.addView(editTextSms);
                            linearLayout.addView(bt_del);

                            linearLayoutList_.addView(linearLayout);
                        }
                    }
                    catch (Exception e) {
                        e.fillInStackTrace();
                    }
                }
            });

            if (getArguments().getString("button") != null) {
                button_execute.setText(getArguments().getString("button"));
                if (!getArguments().getString("phone").equals("")) {
                    for (String item : getArguments().getString("phone").split("\n")) {
                        button_appendphone.performClick();
                        EditText et = (EditText)((LinearLayout)linearLayoutList.getChildAt(linearLayoutList.getChildCount() - 1)).getChildAt(0);
                        et.setText(item);
                    }
                }
                else {
                    button_appendphone.performClick();
                }
                if (!getArguments().getString("name").equals("")) {
                    String[] array = getArguments().getString("name").split("\n");
                    for (int i = 0; i < array.length; i++) {
                        if (((LinearLayout)linearLayoutList).getChildCount() <= i) {
                            button_appendphone.performClick();
                        }
                        EditText et = (EditText)((LinearLayout)linearLayoutList.getChildAt(i)).getChildAt(1);
                        String array_ = array[i].substring(3);
                        if (!array_.equals(((EditText)((LinearLayout)linearLayoutList.getChildAt(i)).getChildAt(0)).getText().toString()))
                            et.setText(array_);
                    }
                }
                if (!getArguments().getString("sms").equals("")) {
                    for (String item : getArguments().getString("sms").split("\n")) {
                        button_appendsms.performClick();
                        EditText et = (EditText)((LinearLayout)linearLayoutList_.getChildAt(linearLayoutList_.getChildCount() - 1)).getChildAt(1);
                        String item_ = item.substring(3);
                        et.setText((item_.equals(getString(R.string.toast_empty_mess))) ? "" : item_);
                    }
                }
                else {
                    button_appendsms.performClick();
                }
                if (!getArguments().getString("check").equals("")) {
                    for (int i = 0; i < linearLayoutList_.getChildCount(); i++) {
                        if (Integer.parseInt(getArguments().getString("check")) == i) {
                            ((RadioButton) ((LinearLayout) linearLayoutList_.getChildAt(i)).getChildAt(0)).setChecked(true);
                            radio_index = i;
                        }
                        else {
                            ((RadioButton) ((LinearLayout) linearLayoutList_.getChildAt(i)).getChildAt(0)).setChecked(false);
                        }
                    }
                }
                button_delete.setVisibility(View.VISIBLE);
            }
            else {
                linearLayoutList.removeAllViews();
                linearLayoutList_.removeAllViews();
                button_appendphone.performClick();
                button_appendsms.performClick();
            }

            button_execute.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    try {
                        StringBuilder edit_phone = new StringBuilder();
                        StringBuilder edit_name = new StringBuilder();
                        for (int i = 0; i < linearLayoutList.getChildCount(); i++) {
                            EditText et_p = (EditText)((LinearLayout)linearLayoutList.getChildAt(i)).getChildAt(0);
                            EditText et_n = (EditText)((LinearLayout)linearLayoutList.getChildAt(i)).getChildAt(1);
                            if (!et_p.getText().toString().equals("")) {
                                edit_phone.append(et_p.getText().toString()).append(MainActivity.key_split);
                                edit_name.append(i + 1).append(". ").append((!et_n.getText().toString().equals("")) ? et_n.getText().toString() : et_p.getText().toString()).append(MainActivity.key_split);
                            }
                        }
                        StringBuilder edit_sms = new StringBuilder();
                        for (int i = 0; i < linearLayoutList_.getChildCount(); i++) {
                            EditText et = (EditText)((LinearLayout)linearLayoutList_.getChildAt(i)).getChildAt(1);
                            edit_sms.append(i + 1).append(". ").append((!et.getText().toString().equals("")) ? et.getText().toString() : key_empty).append(MainActivity.key_split);
                        }
                        if (!edit_phone.toString().equals("")) {
                            if (getArguments().getString("id") != null) {
                                db.execSQL("UPDATE 'MainTable' SET phone = '" + edit_phone + "', name = '" + edit_name + "', sms = '" + edit_sms + "', chec = " + radio_index + " WHERE id = " + getArguments().getString("id") + ";");
                            } else {
                                if (true) { //(MainActivity.CT < 2) {
                                    Cursor query = db.rawQuery("SELECT * FROM 'MainTable' WHERE phone = '" + edit_phone + "';", null);
                                    if (!query.moveToFirst()) {
                                        db.execSQL("INSERT INTO 'MainTable' ('phone', 'name', 'sms', 'chec', 'block') VALUES ('" + edit_phone + "', '" + edit_name + "', '" + edit_sms + "', " + radio_index + ", 1);");
                                    } else {
                                        Toast.makeText(getContext(), getString(R.string.toast_error_base), Toast.LENGTH_SHORT).show();
                                    }
                                }
                                else {
                                    AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(getContext(), R.style.AlertDialogCustom));
                                    builder.setTitle(getString(R.string.alert_error_title)).setMessage("$").setPositiveButton("ОК", new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            dialog.dismiss();
                                        }
                                    });
                                    builder.create().show();
                                }
                            }
                            requireActivity().onBackPressed();
                        }
                        else {
                            Toast.makeText(getContext(), getString(R.string.toast_error_input), Toast.LENGTH_SHORT).show();
                        }
                    }
                    catch (Exception e) {
                        e.fillInStackTrace();
                    }
                }
            });

            button_delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    try {
                        if (getArguments().getString("id") != null) {
                            db.execSQL("DELETE FROM 'MainTable' WHERE id = " + getArguments().getString("id") + ";");
                        }
                    } catch (Exception e) {
                    }
                    requireActivity().onBackPressed();
                }
            });
        }
        catch (Exception ignored) { }
        return root;
    }

    private void showPopupMenu(final View v, final LinearLayout lLayout) {
        PopupMenu popupMenu = new PopupMenu(v.getContext(), v);
        popupMenu.inflate(R.menu.popupmenu);
        final EditText et1 = (EditText)lLayout.getChildAt(0);
        final EditText et2 = (EditText)lLayout.getChildAt(1);

        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @SuppressLint("NonConstantResourceId")
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.menu1:
                                MainActivity.mainAct.addContactToEdit(et1, et2);
                                return true;
                            case R.id.menu2:
                                try {
                                    if (linearLayoutList.getChildCount() > 1) {
                                        linearLayoutList.removeView(lLayout);
                                    }
                                    else {
                                        et1.setText("");
                                        et2.setText("");
                                    }
                                }
                                catch (Exception e) {
                                    e.fillInStackTrace();
                                }
                                return true;
                            default:
                                return false;
                        }
                    }
                });

        popupMenu.setOnDismissListener(new PopupMenu.OnDismissListener() {
            @Override
            public void onDismiss(PopupMenu menu) { }
        });
        popupMenu.show();
    }
}