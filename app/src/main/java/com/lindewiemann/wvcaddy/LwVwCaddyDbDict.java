package com.lindewiemann.wvcaddy;

import android.provider.BaseColumns;

public final class LwVwCaddyDbDict {
    public static final int SHIFT_NIGHT = 1;
    public static final int SHIFT_MORNING = 2;
    public static final int SHIFT_AFTERNOON = 3;

    public static final int CODE_LEFT = 1;
    public static final int CODE_RIGHT = 2;

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
        public static final String COLUMN_NAME_LR = "lr";
    }

    public static class WvCaddySubcodeEntry implements BaseColumns {
        public static final String TABLE_NAME = "wvcaddysubcode";
        public static final String COLUMN_NAME_DATE = "datum";
        public static final String COLUMN_NAME_CODE = "code";
        public static final String COLUMN_NAME_SUBCODE = "subcode";
        public static final String COLUMN_NAME_PCS = "ks";
        public static final String COLUMN_NAME_USER_CODE = "user_code";
        public static final String COLUMN_NAME_SHIFT = "shift";
        public static final String COLUMN_NAME_LR = "lr";
    }

    public static class WvCaddySettings implements BaseColumns {
        public static final String TABLE_NAME = "wvcaddysettings";
        public static final String COLUMN_NAME_MAILJET_API_KEY = "mailjet_api_key";
        public static final String COLUMN_NAME_MAILJET_SECRET_KEY = "mailjet_secret_key";
        public static final String COLUMN_NAME_MAIL_SENDER = "mail_sender";
        public static final String COLUMN_NAME_MAIL_RECIPIENTS = "mail_recipients";
        public static final String COLUMN_NAME_PASSWORD = "mail_password";
        public static final String COLUMN_NAME_GMAILPASSWORD = "mail_gmailpassword";
        public static final String COLUMN_NAME_HOUR = "mail_hour";
        public static final String COLUMN_NAME_MAIL_DATE = "mail_sent_date";
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

    public static String getLeftRightText(int iLr) {
        switch(iLr) {
            case LwVwCaddyDbDict.CODE_LEFT:
                return "LEVÉ";
            case LwVwCaddyDbDict.CODE_RIGHT:
                return "PRAVÉ";

        }

        return "";
    }

}
