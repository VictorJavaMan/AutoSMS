package com.black.autosmska;

import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.black.autosmska.MainActivity.db;
import static com.black.autosmska.MainActivity.key_empty;
import static com.black.autosmska.MainActivity.key_split;

public class ListPhonesFragment extends Fragment {

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_listphones, container, false);
        root.setOnTouchListener(new OnSwipeTouchListener(getContext()) {
            public void onSwipeLeft() {
                NavHostFragment.findNavController(ListPhonesFragment.this).navigate(R.id.action_List_to_Add);
            }
            public void onSwipeRight() {
                NavHostFragment.findNavController(ListPhonesFragment.this).navigate(R.id.action_List_to_Option);
            }
        });
        refreshItems(root);
        return root;
    }

    public void refreshItems(final View view) {
        try {
            boolean flag_v = false;
            MainActivity.CT = 0;
            LinearLayout linearLayoutList = view.findViewById(R.id.linearLayout_);
            linearLayoutList.removeAllViews();
            Cursor query = db.rawQuery("SELECT * FROM 'MainTable';", null);
            if (query.moveToFirst()) {
                do {
                    final Button button = new Button(view.getContext(), null, R.attr.borderlessButtonStyle);
                    button.setTextAlignment(View.TEXT_ALIGNMENT_TEXT_START);
                    LinearLayout.LayoutParams mparams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT, 1);
                    button.setLayoutParams(mparams);
                    button.setTextSize(20);
                    button.setAllCaps(false);
                    registerForContextMenu(button);
                    button.setTextAppearance(getContext(), R.style.RobotoButtonStyle);
                    button.setId(query.getInt(0));
                    EditText editText = new EditText(getContext());
                    editText.setText(query.getString(5));
                    int color = 0;
                    String mess = "";
                    if (!query.getString(2).equals("")) {
                        mess = query.getString(2);
                        color = getResources().getColor(R.color.colorAccent);
                    }
                    String str = editText.getText().toString().substring(0, editText.getText().length() - 1) + "\r\n" + mess.substring(0, mess.length() - 1);
                    int index_last = mess.substring(0, mess.length() - 1).split("\n")[Integer.parseInt(query.getString(3))].length();
                    str = str.replaceAll(key_empty, getString(R.string.toast_empty_mess));
                    if (!str.equals("")) {
                        Spannable spans = new SpannableString(str);
                        spans.setSpan(new ForegroundColorSpan(Color.WHITE), 0, editText.getText().length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                        spans.setSpan(new ForegroundColorSpan(color), editText.getText().length(), str.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                        int in = str.lastIndexOf((Integer.parseInt(query.getString(3)) + 1) + ". ");
                        spans.setSpan(new ForegroundColorSpan(Color.GREEN), (in == -1) ? str.length() : in, (in == -1) ? str.length() : in + index_last, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                        Matcher m = Pattern.compile("(?=(" + getString(R.string.toast_empty_mess) + "))").matcher(str);
                        while (m.find())
                        {
                            spans.setSpan(new ForegroundColorSpan(Color.RED), m.start() - 3, m.start() + getString(R.string.toast_empty_mess).length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                        }
                        button.setText(spans);
                    }
                    button.setTag(query.getString(3));
                    final String phones = query.getString(1);
                    button.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Bundle bundle = new Bundle();
                            String[] strm = button.getText().toString().split("\r\n");
                            bundle.putString("phone", phones);
                            bundle.putString("name", strm[0]);
                            bundle.putString("sms", (strm[1].equals(getString(R.string.toast_empty_mess)) ? "" : strm[1]));
                            bundle.putString("check", button.getTag().toString());
                            bundle.putString("button", getString(R.string.edit_button_text));
                            bundle.putString("id", Integer.toString(button.getId()));
                            NavHostFragment.findNavController(ListPhonesFragment.this).navigate(R.id.action_List_to_Add, bundle);
                        }
                    });
                    final Button bt_del = new Button(view.getContext());
                    LinearLayout.LayoutParams dparams = new LinearLayout.LayoutParams(70, 70, 0);
                    dparams.gravity = Gravity.CENTER_VERTICAL; //dparams.setMargins(0, 30, 10, 0);
                    bt_del.setLayoutParams(dparams);
                    bt_del.setTag(query.getInt(0));
                    bt_del.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.ic_baseline_delete_24, null));
                    bt_del.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view_) {
                            db.execSQL("DELETE FROM 'MainTable' WHERE id = " + bt_del.getTag().toString() + ";");
                            refreshItems(view);
                        }
                    });
                    final Button bt_label = new Button(view.getContext());
                    LinearLayout.LayoutParams dlparams = new LinearLayout.LayoutParams(70, 70, 0);
                    dlparams.gravity = Gravity.CENTER_VERTICAL;
                    bt_label.setLayoutParams(dlparams);
                    bt_label.setTag(query.getInt(0));
                    bt_label.setBackground(ResourcesCompat.getDrawable(getResources(), ((query.getInt(4) == 1) ? R.drawable.ic_baseline_check_circle_outline_24 : R.drawable.ic_baseline_remove_circle_outline_24), null));
                    bt_label.getBackground().setTint(getResources().getColor((query.getInt(4) == 1) ? R.color.colorYes : R.color.colorNo));
                    LinearLayout linearLayout_new = new LinearLayout(view.getContext());
                    linearLayout_new.setOrientation(LinearLayout.HORIZONTAL);
                    LinearLayout.LayoutParams params_new = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
                    linearLayout_new.setLayoutParams(params_new);
                    linearLayout_new.addView(button);
                    linearLayout_new.addView(bt_label);
                    linearLayout_new.addView(bt_del);
                    linearLayoutList.addView(linearLayout_new);

                    View view1 = new View(getContext());
                    RelativeLayout.LayoutParams params1 = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, (int)(getResources().getDisplayMetrics().density * 1));
                    view1.setLayoutParams(params1);
                    view1.setBackgroundColor(getResources().getColor(R.color.colorAccent));

                    linearLayoutList.addView(view1);
                    flag_v = true;
                    MainActivity.CT++;
                }
                while (query.moveToNext());
            }

            TextView tv = view.findViewById(R.id.textlist_);
            tv.setVisibility(flag_v ? View.INVISIBLE : View.VISIBLE);

            View newView = new View(view.getContext());
            newView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, (int) getResources().getDimension(R.dimen.dimen_entry_in_dp)));
            linearLayoutList.addView(newView);
        }
        catch (Exception e) {}
    }
}