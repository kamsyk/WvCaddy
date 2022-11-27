package com.lindewiemann.wvcaddy.adapters;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;

import androidx.cursoradapter.widget.CursorAdapter;

public abstract class VwCaddyCursorAdapter extends CursorAdapter {


    public VwCaddyCursorAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = super.getView(position, convertView, parent);
        if (position % 2 == 1) {
            view.setBackgroundColor(Color.argb(255,220,220,220));
        } else {
            view.setBackgroundColor(Color.WHITE);
        }

        return view;
    }
}
