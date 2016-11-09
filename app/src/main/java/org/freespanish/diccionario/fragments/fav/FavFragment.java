package org.freespanish.diccionario.fragments.fav;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import org.freespanish.diccionario.R;
import org.freespanish.diccionario.adapters.FavAdapter;
import org.freespanish.diccionario.main.MainActivity;

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

public class FavFragment extends Fragment implements FavView {

    @BindView(R.id.fav_recyclerview) RecyclerView favRecyclerView;

    private FavPresenter favPresenter;

    public FavFragment() {
        // Required empty public constructor
    }

    public static FavFragment newInstance() {
        return new FavFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_fav, container, false);
        ButterKnife.bind(this, view);
        setupViews();
        setListeners();
        return  view;
    }

    @Override
    public void setupViews() {
        this.favPresenter = new FavPresenterImpl();
        this.favRecyclerView.setLayoutManager(new StaggeredGridLayoutManager(getResources().getBoolean(R.bool.tablet_mode) ? 4 : 2, GridLayoutManager.VERTICAL) {
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
        this.favRecyclerView.setAdapter
                (new FavAdapter(favPresenter.notifyRetrieveFavorites(getActivity()), (MainActivity)getActivity()));
        registerForContextMenu(this.favRecyclerView);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.contextual_fav_menu) {
            int position = ((FavAdapter) this.favRecyclerView.getAdapter()).getContextMenuSelectedPosition();
            this.favPresenter.notifyRemoveFavorite(getActivity(),
                    ((FavAdapter) this.favRecyclerView.getAdapter()).getDefinitionAtPos(position));
            ((FavAdapter) this.favRecyclerView.getAdapter()).removeItemAtPos(position);
        }
        return super.onContextItemSelected(item);
    }

    @Override
    public void setListeners() {
        if (!getResources().getBoolean(R.bool.tablet_mode)) {
            this.favRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {

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

}
