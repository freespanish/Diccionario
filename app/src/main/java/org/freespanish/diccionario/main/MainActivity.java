package org.freespanish.diccionario.main;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.SearchManager;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.SpannableString;
import android.text.method.LinkMovementMethod;
import android.text.util.Linkify;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.roughike.bottombar.BottomBar;
import com.roughike.bottombar.OnTabSelectListener;

import org.freespanish.diccionario.R;
import org.freespanish.diccionario.fragments.fav.FavFragment;
import org.freespanish.diccionario.fragments.history.HistoryFragment;
import org.freespanish.diccionario.fragments.search.SearchFragment;
import org.freespanish.diccionario.utils.UnitHelper;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
*    This file is part of Diccionario.
*
*    Diccionario is free software: you can redistribute it and/or modify
*    it under the terms of the GNU General Public License as published by
*    the Free Software Foundation, either version 3 of the License, or
*    (at your option) any later version.
*
*    Diccionario is distributed in the hope that it will be useful,
*    but WITHOUT ANY WARRANTY; without even the implied warranty of
*    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
*    GNU General Public License for more details.
*
*    You should have received a copy of the GNU General Public License
*    along with Diccionario.  If not, see <http://www.gnu.org/licenses/>.
*/

public class MainActivity extends AppCompatActivity implements MainView {

    @BindView(R.id.bottom_bar) BottomBar bottomBar;
    @BindView(R.id.content_container) FrameLayout contentContainer;
    //@BindView(R.id.toolbar) Toolbar toolbar;

    private SearchFragment searchFragment;
    private FavFragment favFragment;
    private HistoryFragment historyFragment;
    private boolean canAnimate = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setupViews();
        setListeners();

    }

    @Override
    public void setupViews() {
        ButterKnife.bind(this);
        /*if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Window w = getWindow(); // in Activity's onCreate() for instance
            w.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        }*/

        if (!getResources().getBoolean(R.bool.tablet_mode)) {
            bottomBar.setY(UnitHelper.convertDpToPixel(56f));
            bottomBar.animate()
                    .translationY(0)
                    .setListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationStart(Animator animation) {
                            super.onAnimationEnd(animation);
                            bottomBar.setVisibility(View.VISIBLE);
                        }

                        @Override
                        public void onAnimationEnd(Animator animation) {
                            super.onAnimationEnd(animation);
                        }
                    });
        }
        this.searchFragment = SearchFragment.newInstance();
        this.favFragment = FavFragment.newInstance();
        this.historyFragment = HistoryFragment.newInstance();

    }

    @Override
    public void setListeners() {
        this.bottomBar.setOnTabSelectListener(new OnTabSelectListener() {
            @Override
            public void onTabSelected(@IdRes int tabId) {
                FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                fragmentTransaction.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out);

                switch (tabId) {
                    case R.id.tab_search:
                        fragmentTransaction
                                .replace(R.id.content_container, searchFragment, SearchFragment.class.toString())
                                .commitNow();
                        break;
                    case R.id.tab_fav:
                        fragmentTransaction
                                .replace(R.id.content_container, favFragment, FavFragment.class.toString())
                                .commitNow();
                        break;
                    case R.id.tab_history:
                        fragmentTransaction
                                .replace(R.id.content_container, historyFragment, HistoryFragment.class.toString())
                                .commitNow();
                        break;
                }
            }
        });
    }

    @Override
    public void showBottomBar() {
        if (canAnimate)
            bottomBar.animate()
                    .translationY(0)
                    .setListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationStart(Animator animation) {
                            super.onAnimationEnd(animation);
                            bottomBar.setVisibility(View.VISIBLE);
                            canAnimate = false;
                        }

                        @Override
                        public void onAnimationEnd(Animator animation) {
                            super.onAnimationEnd(animation);
                            canAnimate = true;
                        }
                    });
    }

    @Override
    public void hideBottomBar() {
        if (canAnimate)
            bottomBar.animate()
                    .translationY(UnitHelper.convertDpToPixel(56) + UnitHelper.getNavigationBarHeightPx(this))
                    .setListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationStart(Animator animation) {
                            super.onAnimationStart(animation);
                            canAnimate = false;
                        }

                        @Override
                        public void onAnimationEnd(Animator animation) {
                            super.onAnimationEnd(animation);
                            bottomBar.setVisibility(View.INVISIBLE);
                            canAnimate = true;
                        }
                    });
    }

    @Override
    protected void onNewIntent(Intent intent) {
        handleIntent(intent);
    }

    @Override
    public void handleIntent(Intent intent) {
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);
            searchFragment.onWordSearched(query);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_about:
                showAboutDialog();
                return true;
        }
        return false;
    }

    @Override
    public void showAboutDialog() {

        final SpannableString spannableString = new SpannableString(getString(R.string.about_msg));
        Linkify.addLinks(spannableString, Linkify.ALL);

        final AlertDialog aboutDialog = new AlertDialog.Builder(this)
                .setPositiveButton(android.R.string.ok, null)
                .setTitle(getString(R.string.app_name) + " " + getString(R.string.app_version))
                .setMessage(spannableString)
                .create();

        aboutDialog.show();

        ((TextView) aboutDialog.findViewById(android.R.id.message)).setMovementMethod(LinkMovementMethod.getInstance());
    }

    @Override
    public BottomBar getBottomBar() {
        return this.bottomBar;
    }

}
