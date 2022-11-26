package com.lindewiemann.wvcaddy;

import android.provider.BaseColumns;

public final class LwVwCaddyDbDict {
    public static final int SHIFT_NIGHT = 1;
    public static final int SHIFT_MORNING = 2;
    public static final int SHIFT_AFTERNOON = 3;

    private LwVwCaddyDbDict() {}

    /* Inner class that defines the table contents */
    public static class WvCaddyEntry implements BaseColumns {
        public static final String TABLE_NAME = "wvcaddy";
        public static final String COLUMN_NAME_DATE = "datum";
        public static final String COLUMN_NAME_CODE = "code";
        public static final String COLUMN_NAME_SUBCODE = "subcode";
        public static final String COLUMN_NAME_PCS = "ks";
        public static final String COLUMN_NAME_USER_CODE = "user_code";
        public static final String COLUMN_NAME_SHIFT = "shift";
    }

    public static String getShiftName(int iShift) {
        switch(iShift) {
            case LwVwCaddyDbDict.SHIFT_NIGHT:
                return "Noční";
            case LwVwCaddyDbDict.SHIFT_MORNING:
                return "Ranní";
            case LwVwCaddyDbDict.SHIFT_AFTERNOON:
                return "Odpolední";
        }

        return "";
    }

}