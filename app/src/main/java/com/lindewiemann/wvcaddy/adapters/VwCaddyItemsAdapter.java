package com.lindewiemann.wvcaddy.adapters;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.lindewiemann.wvcaddy.LwVwCaddyDbDict;
import com.lindewiemann.wvcaddy.R;

import androidx.cursoradapter.widget.CursorAdapter;

public class VwCaddyItemsAdapter extends VwCaddyCursorAdapter {
    public VwCaddyItemsAdapter(Context context, Cursor cursor) {
        super(context, cursor, 0);
    }

    // The newView method is used to inflate a new view and return it,
    // you don't bind any data to the view at this point.
    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.vw_caddy_item, parent, false);
    }

    // The bindView method is used to bind all data to a given view
    // such as setting the text on a TextView.
    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        // Find fields to populate in inflated template
        LinearLayout llWrapper = (LinearLayout) view.findViewById(R.id.llWrapper);
        TextView tvDatum = (TextView) view.findViewById(R.id.tvDatum);
        TextView tvTime = (TextView) view.findViewById(R.id.tvTime);
        TextView tvShift = (TextView) view.findViewById(R.id.tvShift);
        TextView tvPcs = (TextView) view.findViewById(R.id.tvPcs);
        TextView tvCode = (TextView) view.findViewById(R.id.tvCode);
        TextView tvSubCode = (TextView) view.findViewById(R.id.tvSubCode);

        // Extract properties from cursor
        String strDateTime = cursor.getString(cursor.getColumnIndexOrThrow(LwVwCaddyDbDict.WvCaddyEntry.COLUMN_NAME_DATE));
        int iShift = cursor.getInt(cursor.getColumnIndexOrThrow(LwVwCaddyDbDict.WvCaddyEntry.COLUMN_NAME_SHIFT));
        int iPcs = cursor.getInt(cursor.getColumnIndexOrThrow(LwVwCaddyDbDict.WvCaddyEntry.COLUMN_NAME_PCS));
        String strCode = cursor.getString(cursor.getColumnIndexOrThrow(LwVwCaddyDbDict.WvCaddyEntry.COLUMN_NAME_CODE));
        String strSubCode = cursor.getString(cursor.getColumnIndexOrThrow(LwVwCaddyDbDict.WvCaddyEntry.COLUMN_NAME_SUBCODE));

        String strSubCodeLines = "";
        if(strSubCode != null) {
            String[] strSubcodes = strSubCode.split(";");
            for (int i = 0; i < strSubcodes.length; i++) {
                if (strSubCodeLines.length() > 0) strSubCodeLines += "\r\n";
                strSubCodeLines += strSubcodes[i];
            }
        }

        String[] strDateItems = strDateTime.split(" ");
        String strDate = strDateItems[0];
        String strTime = strDateItems[1];
        String strShift = String.valueOf(LwVwCaddyDbDict.getShiftName(iShift));
        String strPcs = String.valueOf(iPcs) + " ks";

        // Populate fields with extracted properties
        tvDatum.setText(strDate);
        tvTime.setText(strTime);
        tvShift.setText(strShift);
        tvPcs.setText(strPcs);
        tvCode.setText(strCode);
        tvSubCode.setText(strSubCodeLines);
    }
}
