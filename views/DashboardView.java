package com.healbe.healbe_example_andorid.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.healbe.healbe_example_andorid.R;

import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;

@SuppressWarnings("unused")
public class DashboardView extends ConstraintLayout {

    CardView round;
    ImageView mainIcon;
    ProgressBar progress;
    TextView title;
    TextView value;
    TextView text;

    public DashboardView(Context context) {
        super(context);
        init();
    }

    public DashboardView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
        initParams(attrs);
    }

    public DashboardView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
        initParams(attrs);

    }

    void init() {
        inflate(getContext(), R.layout.dash_view, this);
        round = findViewById(R.id.round);
        mainIcon = findViewById(R.id.icon);
        progress = findViewById(R.id.progress);
        title = findViewById(R.id.title);
        value = findViewById(R.id.value);
        text = findViewById(R.id.text);
    }

    public void setMainIconResource(int res) {
        mainIcon.setImageResource(res);
    }


    public void setProgressVisible(boolean b) {
        progress.setVisibility(b ? VISIBLE : GONE);
    }

    public void setProgressIndeterminate() {
        progress.setIndeterminate(true);
    }

    public void setProgressNonIndeterminate(){
        progress.setIndeterminate(false);
    }

    public void setProgress(int prog) {
        progress.setProgress(prog);
    }


    public void setTitleText(int res) {
        title.setText(res);
    }

    public void setTitleText(CharSequence s) {
        title.setText(s);
    }


    public void setText(int res) {
        text.setText(res);
        text.requestLayout();
    }

    public void setText(CharSequence s) {
        text.setText(s);
    }

    public void setTextVisible(boolean b) {
        text.setVisibility(b ? VISIBLE : GONE);
    }


    public void setValueText(int res) {
        value.setText(res);
    }

    public void setValueText(CharSequence s) {
        value.setText(s);
    }

    public void setValueVisible(boolean b) {
        value.setVisibility(b ? VISIBLE : GONE);
    }

    void initParams(AttributeSet attrs) {
        TypedArray params = getContext().obtainStyledAttributes(attrs, R.styleable.DashboardView);

        try {
            // Icon params
            int iconId = params.getResourceId(R.styleable.DashboardView_icon, -1);
            if (iconId != -1)
                mainIcon.setImageResource(iconId);

            int defCol = ContextCompat.getColor(getContext(), R.color.light_black_38);
            int iconCol = params.getColor(R.styleable.DashboardView_round_color, defCol);
            round.setCardBackgroundColor(iconCol);

            // progress params
            boolean progressVisible = params.getBoolean(R.styleable.DashboardView_progress_visible, false);
            progress.setVisibility(progressVisible ? VISIBLE : GONE);

            //header params
            CharSequence headerStr = params.getString(R.styleable.DashboardView_title_string);

            //noinspection ConstantConditions
            if (headerStr != null)
                title.setText(headerStr);

            //center text params
            CharSequence centerStr = params.getString(R.styleable.DashboardView_text_string);

            if (centerStr != null)
                text.setText(centerStr);

            boolean centerVisible = params.getBoolean(R.styleable.DashboardView_text_visible, true);
            text.setVisibility(centerVisible ? VISIBLE : GONE);


            //bottom text params
            CharSequence bottomStr = params.getString(R.styleable.DashboardView_value_string);
            if (bottomStr != null)
                value.setText(bottomStr);

            boolean bottomVisible = params.getBoolean(R.styleable.DashboardView_value_visible, true);
            value.setVisibility(bottomVisible ? VISIBLE : GONE);
        }
        finally {
            params.recycle();
        }

    }

    public CardView getRound() {
        return round;
    }

    public ImageView getMainIcon() {
        return mainIcon;
    }

    public ProgressBar getProgress() {
        return progress;
    }

    public TextView getTitle() {
        return title;
    }

    public TextView getValue() {
        return value;
    }

    public TextView getText() {
        return text;
    }
}
