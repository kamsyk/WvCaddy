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
        loadInit();
    }

    private void loadInit() {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        ViewGroup root = (ViewGroup) findViewById(android.R.id.content);
        List<ImageButton> btns = find(root, ImageButton.class);

        for (int i=0; i<btns.size(); i++) {
            //if(btns.get(i).getTag() != null) {
                android.view.ViewGroup.LayoutParams params = btns.get(i).getLayoutParams();
                params.height = displayMetrics.heightPixels / 4;
                params.width = displayMetrics.widthPixels / 5 ;

                btns.get(i).setLayoutParams(params);
            //}
        }
    }

    public void removeClick(View view) {
        ImageButton btn = (ImageButton)view;
        int btnId = btn.getId();
        String strTag = btn.getTag().toString();

        hideParts();
    }

    public static <T extends View> List<T> find(ViewGroup root, Class<T> type) {
        FinderByType<T> finderByType = new FinderByType<T>(type);
        LayoutTraverser.build(finderByType).traverse(root);
        return finderByType.getViews();
    }

    private void hideParts() {
        ViewGroup root = (ViewGroup) findViewById(android.R.id.content);
        List<LinearLayout> lls = find(root, LinearLayout.class);
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