package com.cs160.fall13.MeatUp;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Checkable;
import android.widget.CheckedTextView;
import android.widget.LinearLayout;

/*
 * This class is useful for using inside of ListView that needs to have checkable items.
 * Taken from http://tokudu.com/post/50023900640/android-checkable-linear-layout
 */
public class CheckableLinearLayout extends LinearLayout implements Checkable {
    private CheckedTextView checkbox;

    public CheckableLinearLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        // find checked text view
        int childCount = getChildCount();
        for (int i = 0; i < childCount; ++i) {
            View v = getChildAt(i);
            if (v instanceof CheckedTextView) {
                checkbox = (CheckedTextView) v;
                break;
            }
        }
    }

    @Override
    public boolean isChecked() {
        return checkbox != null && checkbox.isChecked();
    }

    @Override
    public void setChecked(boolean checked) {
        if (checkbox != null) {
            checkbox.setChecked(checked);
        }
    }

    @Override
    public void toggle() {
        if (checkbox != null) {
            checkbox.toggle();
        }
    }
}