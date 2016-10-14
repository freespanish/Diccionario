package org.freespanish.diccionario.fragments.history;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import org.freespanish.diccionario.R;
import org.freespanish.diccionario.adapters.HistoryAdapter;
import org.freespanish.diccionario.main.MainActivity;

import butterknife.BindView;
import butterknife.ButterKnife;
import tyrantgit.explosionfield.ExplosionField;

/**
 *    This file is part of Diccionario.
 *
 *    Diccionario is free software: you can redistribute it and/or modify
 *    it under the terms of the GNU General Public License as published by
 *    the Free Software Foundation, either version 3 of the License, or
 *    (at your option) any later version.
 *
 *    Foobar is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *    GNU General Public License for more details.
 *
 *    You should have received a copy of the GNU General Public License
 *    along with Foobar.  If not, see <http://www.gnu.org/licenses/>.
 */

public class HistoryFragment extends Fragment implements HistoryView {

    @BindView(R.id.history_recyclerview) RecyclerView historyRecyclerView;

    private HistoryPresenter historyPresenter;
    private ExplosionField explosionField;

    public HistoryFragment() {
        // Required empty public constructor
    }

    public static HistoryFragment newInstance() {
        return new HistoryFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_history, container, false);
        ButterKnife.bind(this, view);
        setHasOptionsMenu(true);
        return  view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setupViews();
        setListeners();
    }

    @Override
    public void setupViews() {
        this.historyPresenter = new HistoryPresenterImpl();
        this.historyRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()) {
            @Override
            public int scrollVerticallyBy(int dy, RecyclerView.Recycler recycler, RecyclerView.State state) {
                int scrollRange = super.scrollVerticallyBy(dy, recycler, state);
                if (!getResources().getBoolean(R.bool.tablet_mode)) {
                    int overScroll = dy - scrollRange;
                    if (overScroll > 0) {
                        ((MainActivity)getActivity()).hideBottomBar();
                    } else if (overScroll < 0) {
                        ((MainActivity)getActivity()).showBottomBar();
                    }
                }
                return scrollRange;
            }
        });
        this.historyRecyclerView.setAdapter(new HistoryAdapter
                (this.historyPresenter.notifyRetrieveHistory(getActivity()), (MainActivity)getActivity()));
        this.explosionField = ExplosionField.attach2Window(getActivity());
    }

    @Override
    public void setListeners() {
        if (!getResources().getBoolean(R.bool.tablet_mode)) {
            this.historyRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {

                @Override
                public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                    super.onScrolled(recyclerView, dx, dy);
                    if (dy > 0)
                        ((MainActivity)getActivity()).hideBottomBar();
                    else
                        ((MainActivity)getActivity()).showBottomBar();

                }
            });
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        if (this.explosionField != null)
            explosionField.clear();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_history, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == item.getItemId()) {
            this.historyPresenter.notifyClearHistory(getActivity());
            explosionField.explode(historyRecyclerView);
            ((HistoryAdapter)historyRecyclerView.getAdapter()).removeAll();
        }
        return true;
    }
}
