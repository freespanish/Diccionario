package org.freespanish.diccionario.fragments.search;

import android.app.SearchManager;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.support.v7.widget.SearchView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.freespanish.diccionario.R;
import org.freespanish.diccionario.database.models.Definition;
import org.freespanish.diccionario.main.MainActivity;
import org.freespanish.diccionario.utils.Constants;
import org.freespanish.diccionario.utils.RegExUtils;
import org.freespanish.diccionario.views.ObservableWebView;

import java.util.Calendar;

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

public class SearchFragment extends Fragment implements SearchFragmentView {

    @BindView(R.id.search_webview) ObservableWebView searchWebview;
    @BindView(R.id.progress_bar) ProgressBar progressBar;
    @BindView(R.id.search_info_container) RelativeLayout searchInfoLayout;
    @BindView(R.id.search_info_text) TextView searchInfoText;
    @BindView(R.id.search_info_image) ImageView searchInfoImage;

    private SearchPresenter searchPresenter;
    private Menu menu;
    private String currentWord;

    private static String pendingWordToSearch;
    private String word;

    public SearchFragment() {
    }

    public static SearchFragment newInstance() {
        return new SearchFragment();
    }

    public static String getPendingWordToSearch() {
        return pendingWordToSearch;
    }

    public static void setPendingWordToSearch(String pendingWordToSearch) {
        SearchFragment.pendingWordToSearch = pendingWordToSearch;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragmentº
        View view = inflater.inflate(R.layout.fragment_search, container, false);
        ButterKnife.bind(this, view);
        setHasOptionsMenu(true);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setupViews();
        setListeners();
    }

    @Override
    public void setupViews() {
        this.searchPresenter = new SearchPresenterImpl(this);
        this.searchWebview.setWebViewClient(new WebViewClient() {

            void handleLink(String url) {
                url = url.substring(url.lastIndexOf("/") + 1, url.length());
                SearchFragment.this.searchPresenter.prepareDefinition(url, true);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                setProgressBarVisibility(View.GONE);
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                if (url.contains("fetch"))
                    handleLink(url);
                return true;
            }

            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                if (request.getUrl().toString().contains("fetch"))
                    handleLink(request.getUrl().toString());
                return true;
            }
        });

        if (pendingWordToSearch != null) {
            this.searchInfoLayout.setVisibility(View.GONE);
            onWordSearched(pendingWordToSearch);
            pendingWordToSearch = null;
        }
    }

    @Override
    public void setListeners() {
        if (!getResources().getBoolean(R.bool.tablet_mode)) {
            this.searchWebview.setOnScrollChangedCallback(new ObservableWebView.OnScrollChangedCallback() {

                @Override
                public void onScroll(int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                    if (scrollY > oldScrollY)
                        ((MainActivity)getActivity()).hideBottomBar();
                    else
                        ((MainActivity)getActivity()).showBottomBar();
                }

                @Override
                public void onOverScroll(int scrollX, int scrollY, boolean clampedX, boolean clampedY) {
                    if (clampedY)
                        if (scrollY != 0)
                            ((MainActivity)getActivity()).hideBottomBar();
                        else
                            ((MainActivity)getActivity()).showBottomBar();
                }

            });
        }
    }

    @Override
    public void populateWebView(final String htmlContent) {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (SearchFragment.this.searchInfoLayout.getVisibility() == View.VISIBLE) {
                    SearchFragment.this.searchInfoLayout.setVisibility(View.GONE);
                }
                if (SearchFragment.this.searchWebview.getVisibility() != View.VISIBLE)
                    SearchFragment.this.searchWebview.setVisibility(View.VISIBLE);
                SearchFragment.this.searchWebview.loadDataWithBaseURL
                        ("file:///android_asset/", htmlContent, "text/html", "UTF-8", null);
                setFavState(htmlContent);
                ((MainActivity) getActivity()).getBottomBar().invalidate();
            }
        });
    }

    @Override
    public void handleError() {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                SearchFragment.this.progressBar.setVisibility(View.GONE);
                SearchFragment.this.searchWebview.setVisibility(View.GONE);
                SearchFragment.this.searchInfoLayout.setVisibility(View.VISIBLE);
                SearchFragment.this.searchInfoImage.setBackgroundResource(R.drawable.internet_error);
                SearchFragment.this.searchInfoText.setText(R.string.internet_error);
            }
        });
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.search_menu, menu);
        SearchManager searchManager =
                (SearchManager) getActivity().getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView =
                (SearchView) menu.findItem(R.id.action_search).getActionView();
        searchView.setSearchableInfo(
                searchManager.getSearchableInfo(getActivity().getComponentName()));
        searchView.setMaxWidth(Integer.MAX_VALUE);
        this.menu = menu;

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_fav) {
            Definition definition = this.searchPresenter
                    .notifyDefinitionToRetrieve("word = ?", this.currentWord, getActivity());

            if (definition != null) {
                definition.isFavorite = !definition.isFavorite;
                item.setIcon(definition.isFavorite ? R.drawable.ic_favorite_white_24dp : R.drawable.ic_favorite_border_white_24dp);
                item.setTitle(definition.isFavorite ? getString(R.string.add_fav_text) : getString(R.string.remove_fav_text));
                this.searchPresenter.notifyDefinitionToStore(definition, getActivity());
            }

        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (this.searchPresenter != null)
            this.searchPresenter.onDestroy();
    }

    @Override
    public void onWordSearched(String word) {
        this.word = word;
        this.searchPresenter.prepareDefinition(word, false);
    }

    @Override
    public void setProgressBarVisibility(int visibility) {
        this.progressBar.setVisibility(visibility);
    }

    @Override
    public void setFavState(String htmlContent) {
        this.currentWord = RegExUtils.getStringFromRegex(htmlContent, Constants.REGEX_GET_HEADER)
                .replaceAll(Constants.REGEX_REMOVE_SUPS, "")
                .replaceAll(Constants.REGEX_REMOVE_COMMAS, "")
                .replace(".", "")
                .replace("</i>", "")
                .replace("<i>", "");

        if (this.currentWord.length() > 0) {
            if (menu.findItem(R.id.action_fav) == null)
                return;
            menu.findItem(R.id.action_fav).setVisible(true);
            Definition definition = this.searchPresenter
                    .notifyDefinitionToRetrieve("word = ?", this.currentWord, getActivity());
            if (definition != null && definition.isFavorite) {
                menu.findItem(R.id.action_fav).setIcon(R.drawable.ic_favorite_white_24dp);
                menu.findItem(R.id.action_fav).setTitle(R.string.remove_fav_text);
            } else {
                menu.findItem(R.id.action_fav).setIcon(R.drawable.ic_favorite_border_white_24dp);
                menu.findItem(R.id.action_fav).setTitle(R.string.add_fav_text);
            }

            if (definition == null)
                definition = new Definition(this.currentWord, Calendar.getInstance().getTimeInMillis());
            else {
                definition.historyTimestamp = Calendar.getInstance().getTimeInMillis();
                definition.isHistory = true;
            }

            this.searchPresenter.notifyDefinitionToStore(definition, getActivity());
        } else {
            menu.findItem(R.id.action_fav).setVisible(false);
        }

    }

}
