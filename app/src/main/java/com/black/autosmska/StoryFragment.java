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

import static com.black.autosmska.MainActivity.db;

public class StoryFragment extends Fragment {

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_story, container, false);
        root.setOnTouchListener(new OnSwipeTouchListener(getContext()) {
            public void onSwipeRight() {
                NavHostFragment.findNavController(StoryFragment.this).navigate(R.id.action_Story_to_Add);
            }
            public void onSwipeLeft() {
                NavHostFragment.findNavController(StoryFragment.this).navigate(R.id.action_Story_to_Option);
            }
        });
        refreshItems(root);
        return root;
    }

    public void refreshItems(final View view) {
        boolean flag_v = false;
        LinearLayout linearLayoutList = view.findViewById(R.id.linearLayout_);
        linearLayoutList.removeAllViews();
        Cursor query = db.rawQuery("SELECT * FROM 'HistoryTable';", null);
        if (query.moveToFirst()) {
            do {
                final Button button = new Button(view.getContext(), null, R.attr.borderlessButtonStyle);
                button.setTextAlignment(View.TEXT_ALIGNMENT_TEXT_START);
                button.setTextSize(20);
                button.setAllCaps(false);
                LinearLayout.LayoutParams mparams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT, 1);
                button.setLayoutParams(mparams);
                registerForContextMenu(button);
                button.setTextAppearance(getContext(), R.style.RobotoButtonStyle);
                button.setId(query.getInt(0));
                EditText editText = new EditText(getContext());
                editText.setText(query.getString(1));
                String str = editText.getText() + "\n" + query.getString(2);
                if (!str.equals("")) {
                    Spannable spans = new SpannableString(str);
                    spans.setSpan(new ForegroundColorSpan(Color.WHITE), 0, str.indexOf("\n"), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    spans.setSpan(new ForegroundColorSpan(Color.rgb(0, 247, 255)), str.indexOf("\n") + 1, str.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    button.setText(spans);
                }
                final Button bt_del = new Button(view.getContext());
                LinearLayout.LayoutParams dparams = new LinearLayout.LayoutParams(70, 70, 0);
                dparams.gravity = Gravity.CENTER_VERTICAL; //dparams.setMargins(0, 30, 10, 0);
                bt_del.setLayoutParams(dparams);
                bt_del.setTag(query.getInt(0));
                bt_del.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.ic_baseline_delete_24, null));
                bt_del.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view_) {
                        db.execSQL("DELETE FROM 'HistoryTable' WHERE id = " + bt_del.getTag().toString() + ";");
                        refreshItems(view);
                    }
                });
                LinearLayout linearLayout_new = new LinearLayout(view.getContext());
                linearLayout_new.setOrientation(LinearLayout.HORIZONTAL);
                LinearLayout.LayoutParams params_new = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
                linearLayout_new.setLayoutParams(params_new);
                linearLayout_new.addView(button);
                linearLayout_new.addView(bt_del);
                linearLayoutList.addView(linearLayout_new);

                View view1 = new View(getContext());
                RelativeLayout.LayoutParams params1 = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, (int)(getResources().getDisplayMetrics().density * 1));
                view1.setLayoutParams(params1);
                view1.setBackgroundColor(getResources().getColor(R.color.colorAccent));

                linearLayoutList.addView(view1);
                flag_v = true;
            }
            while (query.moveToNext());
        }

        TextView tv = view.findViewById(R.id.textstory_);
        tv.setVisibility(flag_v ? View.INVISIBLE : View.VISIBLE);

        View newView = new View(view.getContext());
        newView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, (int) getResources().getDimension(R.dimen.dimen_entry_in_dp)));
        linearLayoutList.addView(newView);
    }
}