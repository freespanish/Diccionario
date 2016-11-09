package org.freespanish.diccionario.adapters;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.amulyakhare.textdrawable.util.ColorGenerator;

import org.freespanish.diccionario.R;
import org.freespanish.diccionario.database.models.Definition;
import org.freespanish.diccionario.fragments.search.SearchFragment;
import org.freespanish.diccionario.main.MainActivity;
import org.freespanish.diccionario.utils.UnitHelper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Random;

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

public class FavAdapter extends RecyclerView.Adapter<FavAdapter.ViewHolder> {

    private final ArrayList<Definition> definitions;
    private final MainActivity activityCaller;
    private int contextMenuSelectedPosition;

    public FavAdapter(ArrayList<Definition> definitions, MainActivity activityCaller) {
        this.definitions = definitions;
        Collections.sort(this.definitions, new Comparator<Definition>() {
            @Override
            public int compare(Definition o1, Definition o2) {
                return o1.word.toLowerCase().compareTo(o2.word.toLowerCase());
            }
        });
        this.activityCaller = activityCaller;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new FavAdapter.ViewHolder(
                LayoutInflater.from(parent.getContext()).inflate(R.layout.fav_list_item, parent, false),
                new FavAdapter.ViewHolder.OnFavItemClickListener() {
                    @Override
                    public void onFavItemClick(View caller) {
                        SearchFragment.setPendingWordToSearch(((TextView)caller
                                .findViewById(R.id.fav_word_textview)).getText().toString());
                        activityCaller.getBottomBar().selectTabWithId(R.id.tab_search);
                    }
                }
        );
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.favLitItemContainer
                .setCardBackgroundColor(ColorGenerator.MATERIAL.getColor(Character.toLowerCase(definitions.get(position).word.charAt(0))));
        holder.favLitItemContainer.setMinimumHeight((int) getRandomHeightInPx());
        holder.favWordTextView.setText(definitions.get(position).word);
        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                setContextMenuSelectedPosition(holder.getAdapterPosition());
                return false;
            }
        });
    }

    @Override
    public void onViewRecycled(ViewHolder holder) {
        holder.itemView.setOnLongClickListener(null);
        super.onViewRecycled(holder);
    }

    private float getRandomHeightInPx() {
        final int minHeightDp = 48;
        final int maxHeightDp = 168;
        return UnitHelper.convertDpToPixel(new Random().nextInt(maxHeightDp - minHeightDp + 1) + minHeightDp);
    }

    @Override
    public int getItemCount() {
        return this.definitions.size();
    }

    public void removeAll() {
        this.definitions.clear();
        this.notifyDataSetChanged();
    }

    public void removeItemAtPos(int position) {
        this.definitions.remove(position);
        this.notifyItemRemoved(position);
    }

    public Definition getDefinitionAtPos(int position) {
        return this.definitions.get(position);
    }

    public int getContextMenuSelectedPosition() {
        return contextMenuSelectedPosition;
    }

    public void setContextMenuSelectedPosition(int contextMenuSelectedPosition) {
        this.contextMenuSelectedPosition = contextMenuSelectedPosition;
    }

    static class ViewHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener, View.OnCreateContextMenuListener {

        @BindView(R.id.fav_list_item_container) CardView favLitItemContainer;
        @BindView(R.id.fav_word_textview) TextView favWordTextView;

        private OnFavItemClickListener onFavItemClickListener;

        ViewHolder(View itemView, OnFavItemClickListener onHistoryItemClickListener) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            this.onFavItemClickListener = onHistoryItemClickListener;
            this.favLitItemContainer.setOnClickListener(this);
            itemView.setOnCreateContextMenuListener(this);
        }

        @Override
        public void onClick(View v) {
            if (v instanceof CardView) {
                onFavItemClickListener.onFavItemClick(v);
            }
        }

        @Override
        public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
            menu.add(Menu.NONE, R.id.contextual_fav_menu, Menu.NONE, R.string.remove_fac_cont_menu);
        }

        interface OnFavItemClickListener {
            void onFavItemClick(View caller);
        }
    }
}
