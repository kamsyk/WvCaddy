package com.lindewiemann.wvcaddy;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;

import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;

import android.util.DisplayMetrics;
import android.view.View;

import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.lindewiemann.wvcaddy.databinding.ActivityMainBinding;

import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private final String SELECTED_IMG_BUTTON_ID = "SelectedimgButtonId";
    private final String SHIFT_BUTTON_ID = "ShiftButtonId";
    private final String SHIFT_ID = "ShiftId";
    private final String CODE_ID = "Code";
    private final String SUBCODE_ID = "SubCode";

    ContainerDbHelper dbHelper = new ContainerDbHelper(this);
   // private AppBarConfiguration appBarConfiguration;
    //private ActivityMainBinding binding;

    private int _iShift = -1;
    private String _strCode = null;
    private String _strSubcode = null;
    private int _btnPicId = -1;
    private int _btnShiftId = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //loadInit();
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        loadInit();
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {

        super.onRestoreInstanceState(savedInstanceState);

        // Restore UI state from the savedInstanceState.
        // This bundle has also been passed to onCreate.

        _btnPicId = savedInstanceState.getInt(SELECTED_IMG_BUTTON_ID);
        _btnShiftId = savedInstanceState.getInt(SHIFT_BUTTON_ID);
        _iShift = savedInstanceState.getInt(SHIFT_ID);
        _strCode = savedInstanceState.getString(CODE_ID);
        _strSubcode = savedInstanceState.getString(SUBCODE_ID);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        // Make sure to call the super method so that the states of our views are saved
        super.onSaveInstanceState(outState);
        // Save our own state now
        outState.putInt(SELECTED_IMG_BUTTON_ID, _btnPicId);
        outState.putInt(SHIFT_BUTTON_ID, _btnShiftId);
    }

    private void loadInit() {
        setDefaultImages();

        if(_btnPicId > -1) {
            hideParts();
        }
        if(_btnShiftId > -1) {
            hideShiftButtons();
        }
    }

    private void setDefaultImages() {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        ViewGroup root = (ViewGroup) findViewById(android.R.id.content);
        List<ImageButton> btns = find(root, ImageButton.class);
        Button btnSave = (Button) findViewById(R.id.btnSave);
        //android.view.ViewGroup.LayoutParams btnSaveParams = btnSave.getLayoutParams();
        int iScreenHeighDelta = 3 * btnSave.getHeight();

        int iHeight = (displayMetrics.heightPixels - iScreenHeighDelta - 150) / 2;
        if(iHeight < 200) iHeight = 200;
        if(iHeight > 800) iHeight = 800;
        int iWidth = displayMetrics.widthPixels / 5 ;
        if(iWidth < 150) iWidth = 150;
        if(iWidth > 600) iWidth = 600;

        for (int i=0; i<btns.size(); i++) {

            if(_btnPicId != btns.get(i).getId() && btns.get(i).getId() != R.id.btnList) {
                android.view.ViewGroup.LayoutParams params = btns.get(i).getLayoutParams();
                params.height = iHeight;
                params.width = iWidth;

                btns.get(i).setLayoutParams(params);
            }
        }
    }

    public void removeClick(View view) {
        ImageButton btn = (ImageButton)view;
        _btnPicId = btn.getId();

        hideParts();
    }

    public void shiftClick(View view) {
        Button btn = (Button)view;
        _btnShiftId = btn.getId();

        hideShiftButtons();
    }

    private void hideShiftButtons() {
        Button btnMorning = (Button) findViewById(R.id.btnMorning);
        Button btnNight = (Button) findViewById(R.id.btnNight);
        Button btnAfternoon = (Button) findViewById(R.id.btnAfternoon);

        switch(_btnShiftId) {
            case R.id.btnAfternoon:
                btnMorning.setVisibility(View.GONE);
                btnNight.setVisibility(View.GONE);
                _iShift = LwWvCaddyDbDict.SHIFT_AFTERNOON;
                break;
            case R.id.btnNight:
                btnMorning.setVisibility(View.GONE);
                btnAfternoon.setVisibility(View.GONE);
                _iShift = LwWvCaddyDbDict.SHIFT_NIGHT;
                break;
            case R.id.btnMorning:
                btnNight.setVisibility(View.GONE);
                btnAfternoon.setVisibility(View.GONE);
                _iShift = LwWvCaddyDbDict.SHIFT_MORNING;
                break;
        }
    }

    public void save(View view) {
        //hideParts();
        if(_iShift <= 0) {
            AlertDialog.Builder builder1 = new AlertDialog.Builder(this);
            builder1.setTitle("Upozornění");
            builder1.setMessage("Vyberte směnu");
            builder1.setCancelable(true);
            builder1.setNeutralButton("Zavřít",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                        }
                    });

            AlertDialog alert11 = builder1.create();
            alert11.show();
            //getWindow().getDecorView().findViewById(android.R.id.content).invalidate();
            return;
        }

        if(_strCode == null || _strSubcode == null) {
            AlertDialog.Builder builder1 = new AlertDialog.Builder(this);
            builder1.setTitle("Upozornění");
            builder1.setMessage("Vyberte podsestavu");
            builder1.setCancelable(true);
            builder1.setNeutralButton("Zavřít",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                        }
                    });

            AlertDialog alert11 = builder1.create();
            alert11.show();
            //getWindow().getDecorView().findViewById(android.R.id.content).invalidate();
            return;
        }

        try {
            long newRowId = saveToDb();
            Toast.makeText(
                    getApplicationContext(),
                    "Data byla uložena (id " + newRowId + ") " + getShiftName() + " " + _strCode,
                    Toast.LENGTH_SHORT).show();
            reset();
        } catch(Exception ex) {
            AlertDialog.Builder builder1 = new AlertDialog.Builder(this);
            builder1.setTitle("Chyba");
            builder1.setMessage("Při ukládání došlo k chybě");
            builder1.setCancelable(true);
            builder1.setNeutralButton("Zavřít",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                        }
                    });

            AlertDialog alert11 = builder1.create();
            alert11.show();
        }

    }

    public void back(View view) {
        reset();
    }

    private void reset() {
        _iShift = -1;
        _strCode = null;
        _strSubcode = null;
        _btnShiftId = -1;
        displayShiftButtons();
        displayImages();
    }

    private String getShiftName() {
        switch(_iShift) {
            case LwWvCaddyDbDict.SHIFT_NIGHT:
                return "Noční";
            case LwWvCaddyDbDict.SHIFT_MORNING:
                return "Ranní";
            case LwWvCaddyDbDict.SHIFT_AFTERNOON:
                return "Odpolední";
        }

        return "";
    }

    private long saveToDb() {
        Date date = new Date();
        DateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
        String strDate = dateFormat.format(date);

        SQLiteDatabase db = dbHelper.getWritableDatabase();

        // Create a new map of values, where column names are the keys
        ContentValues values = new ContentValues();
        values.put(LwWvCaddyDbDict.WvCaddyEntry.COLUMN_NAME_SHIFT, _iShift);
        values.put(LwWvCaddyDbDict.WvCaddyEntry.COLUMN_NAME_CODE, _strCode);
        values.put(LwWvCaddyDbDict.WvCaddyEntry.COLUMN_NAME_SUBCODE, _strSubcode);
        values.put(LwWvCaddyDbDict.WvCaddyEntry.COLUMN_NAME_PCS, 1);
        values.put(LwWvCaddyDbDict.WvCaddyEntry.COLUMN_NAME_DATE, strDate);

        // Insert the new row, returning the primary key value of the new row
        long newRowId = db.insert(LwWvCaddyDbDict.WvCaddyEntry.TABLE_NAME, null, values);

        return newRowId;
    }

    private void displayShiftButtons() {
        Button btnMorning = (Button) findViewById(R.id.btnMorning);
        Button btnNight = (Button) findViewById(R.id.btnNight);
        Button btnAfternoon = (Button) findViewById(R.id.btnAfternoon);

        btnNight.setVisibility(View.VISIBLE);
        btnAfternoon.setVisibility(View.VISIBLE);
        btnMorning.setVisibility(View.VISIBLE);
    }

    private void displayImages() {
        _btnPicId = -1;
        setDefaultImages();

        LinearLayout llUzsb1l = (LinearLayout) findViewById(R.id.llUzsb1l);
        LinearLayout llUzsb2l = (LinearLayout) findViewById(R.id.llUzsb2l);
        LinearLayout llUzsb3l = (LinearLayout) findViewById(R.id.llUzsb3l);
        LinearLayout llUzsb4l = (LinearLayout) findViewById(R.id.llUzsb4l);
        LinearLayout llUzsb1r = (LinearLayout) findViewById(R.id.llUzsb1r);
        LinearLayout llUzsb2r = (LinearLayout) findViewById(R.id.llUzsb2r);
        LinearLayout llUzsb3r = (LinearLayout) findViewById(R.id.llUzsb3r);
        LinearLayout llUzsb4r = (LinearLayout) findViewById(R.id.llUzsb4r);

        llUzsb1l.setVisibility(View.VISIBLE);
        llUzsb2l.setVisibility(View.VISIBLE);
        llUzsb3l.setVisibility(View.VISIBLE);
        llUzsb4l.setVisibility(View.VISIBLE);
        llUzsb1r.setVisibility(View.VISIBLE);
        llUzsb2r.setVisibility(View.VISIBLE);
        llUzsb3r.setVisibility(View.VISIBLE);
        llUzsb4r.setVisibility(View.VISIBLE);

    }

    public static <T extends View> List<T> find(ViewGroup root, Class<T> type) {
        FinderByType<T> finderByType = new FinderByType<T>(type);
        LayoutTraverser.build(finderByType).traverse(root);
        return finderByType.getViews();
    }

    private void hideParts() {
        Button btnSave = (Button) findViewById(R.id.btnSave);
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int iScreenHeighDelta = 3 * btnSave.getHeight();
        int iHeight = (displayMetrics.heightPixels - iScreenHeighDelta - 150) / 1;

        if(iHeight < 200) iHeight = 200;
        if(iHeight > 1200) iHeight = 1200;
        int iWidth = displayMetrics.widthPixels - 150 ;
        if(iWidth < 150) iWidth = 150;
        if(iWidth > 900) iWidth = 900;

        List<LinearLayout> lls = new ArrayList<LinearLayout>();
        lls.add((LinearLayout) findViewById(R.id.llUzsb1l));
        lls.add((LinearLayout) findViewById(R.id.llUzsb2l));
        lls.add((LinearLayout) findViewById(R.id.llUzsb3l));
        lls.add((LinearLayout) findViewById(R.id.llUzsb4l));
        lls.add((LinearLayout) findViewById(R.id.llUzsb1r));
        lls.add((LinearLayout) findViewById(R.id.llUzsb2r));
        lls.add((LinearLayout) findViewById(R.id.llUzsb3r));
        lls.add((LinearLayout) findViewById(R.id.llUzsb4r));

        ImageButton btn = (ImageButton) findViewById(_btnPicId);
        android.view.ViewGroup.LayoutParams params = btn.getLayoutParams();
        params.height = iHeight;
        params.width = iWidth;
        btn.setLayoutParams(params);

        switch(_btnPicId) {
            case R.id.btnUzsb1l:
                _strCode = "UZSB 1";
                _strSubcode = "810 2-4525;020 2-4529";
                hideLls(R.id.llUzsb1l, lls);
                break;
            case R.id.btnUzsb2l:
                _strCode = "UZSB 2";
                _strSubcode ="810 2-4525;020 2-4529;810 2-4533A";
                hideLls(R.id.llUzsb2l, lls);
                break;
            case R.id.btnUzsb3l:
                _strCode = "UZSB 3";
                _strSubcode ="810 2-4525;020 2-4529;810 2-4533A;020 2-4531;820 8-0263";
                hideLls(R.id.llUzsb3l, lls);
                break;
            case R.id.btnUzsb4l:
                _strCode = "UZSB 4";
                _strSubcode = "810 2-4525;020 2-4529;810 2-4533A;020 2-4531;820 8-0263;810 2-4537A";
                hideLls(R.id.llUzsb4l, lls);
                break;
            case R.id.btnUzsb1r:
                _strCode = "UZSB 1";
                _strSubcode = "810 2-4536B;020 2-4530";
                hideLls(R.id.llUzsb1r, lls);
                break;
            case R.id.btnUzsb2r:
                _strCode = "UZSB 2";
                _strSubcode = "810 2-4536B;020 2-4530;820 8-0398A";
                hideLls(R.id.llUzsb2r, lls);
                break;
            case R.id.btnUzsb3r:
                _strCode = "UZSB 3";
                _strSubcode = "810 2-4536B;020 2-4530;820 8-0398A;020 2-4532;820 8-0343";
                hideLls(R.id.llUzsb3r, lls);
                break;
            case R.id.btnUzsb4r:
                _strCode = "UZSB 4";
                _strSubcode = "810 2-4536B;020 2-4530;820 8-0398A;020 2-4532;820 8-0343;81024534A";
                hideLls(R.id.llUzsb4r, lls);
                break;
        }

        //getWindow().getDecorView().findViewById(android.R.id.content).invalidate();
    }

    private void hideLls(int selectedLlId, List<LinearLayout> lls) {

        for(int i=0; i<lls.size(); i++) {
            if(lls.get(i).getId() == selectedLlId) {
                //lls.get(i).invalidate();
                //lls.get(i).requestLayout();
                //lls.get(i).setVisibility(View.VISIBLE);
            } else {
                lls.get(i).setVisibility(View.GONE);
            }
        }
    }

    private static class FinderByType<T extends View> implements LayoutTraverser.Processor {
        private final Class<T> type;
        private final List<T> views;

        private FinderByType(Class<T> type) {
            this.type = type;
            views = new ArrayList<T>();
        }

        @Override
        @SuppressWarnings("unchecked")
        public void process(View view) {
            if (type.isInstance(view)) {
                views.add((T) view);
            }
        }

        public List<T> getViews() {
            return views;
        }
    }

    public void displayList(View view) {
        Intent intent = new Intent(this, CaddyItemList.class);
        startActivity(intent);
    }
}