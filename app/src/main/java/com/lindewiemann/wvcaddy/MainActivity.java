package com.lindewiemann.wvcaddy;

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

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private AppBarConfiguration appBarConfiguration;
    private ActivityMainBinding binding;

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

    private void loadInit() {
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

            android.view.ViewGroup.LayoutParams params = btns.get(i).getLayoutParams();
            params.height = iHeight;
            params.width = iWidth;

            btns.get(i).setLayoutParams(params);

        }
    }

    public void removeClick(View view) {
        ImageButton btn = (ImageButton)view;
        int btnId = btn.getId();

        hideParts(btnId);
    }

    public void shiftClick(View view) {
        Button btn = (Button)view;
        int btnId = btn.getId();

        Button btnMorning = (Button) findViewById(R.id.btnMorning);
        Button btnNight = (Button) findViewById(R.id.btnNight);
        Button btnAfternoon = (Button) findViewById(R.id.btnAfternoon);

        switch(btnId) {
            case R.id.btnAfternoon:
                btnMorning.setVisibility(View.GONE);
                btnNight.setVisibility(View.GONE);
                break;
            case R.id.btnNight:
                btnMorning.setVisibility(View.GONE);
                btnAfternoon.setVisibility(View.GONE);
                break;
            case R.id.btnMorning:
                btnNight.setVisibility(View.GONE);
                btnAfternoon.setVisibility(View.GONE);
                break;
        }

    }

    public void save(View view) {
        displayShiftButtons();
    }

    private void displayShiftButtons() {
        Button btnMorning = (Button) findViewById(R.id.btnMorning);
        Button btnNight = (Button) findViewById(R.id.btnNight);
        Button btnAfternoon = (Button) findViewById(R.id.btnAfternoon);

        btnNight.setVisibility(View.VISIBLE);
        btnAfternoon.setVisibility(View.VISIBLE);
        btnMorning.setVisibility(View.VISIBLE);
    }

    public static <T extends View> List<T> find(ViewGroup root, Class<T> type) {
        FinderByType<T> finderByType = new FinderByType<T>(type);
        LayoutTraverser.build(finderByType).traverse(root);
        return finderByType.getViews();
    }

    private void hideParts(int btnId) {
        Button btnSave = (Button) findViewById(R.id.btnSave);
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int iScreenHeighDelta = 3 * btnSave.getHeight();
        int iHeight = (displayMetrics.heightPixels - iScreenHeighDelta - 150) / 1;

        if(iHeight < 200) iHeight = 200;
        if(iHeight > 1000) iHeight = 1000;
        int iWidth = displayMetrics.widthPixels / 2 ;
        if(iWidth < 150) iWidth = 150;
        if(iWidth > 800) iWidth = 800;

        LinearLayout llUzsb1l = (LinearLayout) findViewById(R.id.llUzsb1l);
        LinearLayout llUzsb2l = (LinearLayout) findViewById(R.id.llUzsb2l);
        LinearLayout llUzsb3l = (LinearLayout) findViewById(R.id.llUzsb3l);
        LinearLayout llUzsb4l = (LinearLayout) findViewById(R.id.llUzsb4l);
        LinearLayout llUzsb1r = (LinearLayout) findViewById(R.id.llUzsb1r);
        LinearLayout llUzsb2r = (LinearLayout) findViewById(R.id.llUzsb2r);
        LinearLayout llUzsb3r = (LinearLayout) findViewById(R.id.llUzsb3r);
        LinearLayout llUzsb4r = (LinearLayout) findViewById(R.id.llUzsb4r);

        ImageButton btnUzsb1l = (ImageButton) findViewById(R.id.btnUzsb1l);

        switch(btnId) {
            case R.id.btnUzsb1l:
                android.view.ViewGroup.LayoutParams params = btnUzsb1l.getLayoutParams();
                params.height = iHeight;
                params.width = iWidth;
                btnUzsb1l.setLayoutParams(params);

                llUzsb2l.setVisibility(View.GONE);
                llUzsb3l.setVisibility(View.GONE);
                llUzsb4l.setVisibility(View.GONE);
                llUzsb1r.setVisibility(View.GONE);
                llUzsb2r.setVisibility(View.GONE);
                llUzsb3r.setVisibility(View.GONE);
                llUzsb4r.setVisibility(View.GONE);
                break;
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
}