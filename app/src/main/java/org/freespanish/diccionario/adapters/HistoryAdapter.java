package org.freespanish.diccionario.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.amulyakhare.textdrawable.TextDrawable;
import com.amulyakhare.textdrawable.util.ColorGenerator;

import org.freespanish.diccionario.R;
import org.freespanish.diccionario.database.models.Definition;
import org.freespanish.diccionario.fragments.search.SearchFragment;
import org.freespanish.diccionario.main.MainActivity;
import org.freespanish.diccionario.utils.DateUtils;

import java.util.ArrayList;
import java.util.Collections;

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
 *    Foobar is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *    GNU General Public License for more details.
 *
 *    You should have received a copy of the GNU General Public License
 *    along with Foobar.  If not, see <http://www.gnu.org/licenses/>.
 */

public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.ViewHolder> {

    private final ArrayList<Definition> definitions;
    private final MainActivity activityCaller;

    public HistoryAdapter(ArrayList<Definition> definitions, MainActivity activityCaller) {
        this.definitions = definitions;
        Collections.sort(this.definitions);
        this.activityCaller = activityCaller;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new HistoryAdapter.ViewHolder(
                LayoutInflater.from(parent.getContext()).inflate(R.layout.history_list_item, parent, false),
                new ViewHolder.OnHistoryItemClickListener() {
                    @Override
                    public void onHistoryItemClick(View caller) {
                        SearchFragment.setPendingWordToSearch(((TextView)caller
                                .findViewById(R.id.word_text)).getText().toString());
                        activityCaller.getBottomBar().selectTabWithId(R.id.tab_search);
                    }
                }
        );
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.letterImage.setImageDrawable(TextDrawable.builder()
                .buildRound(String.valueOf(definitions.get(position).word.charAt(0)).toUpperCase(),
                        ColorGenerator.MATERIAL.getColor(Character.toLowerCase(definitions.get(position).word.charAt(0)))));
        holder.wordText.setText(definitions.get(position).word);
        holder.timestampText.setText(DateUtils.getDate(definitions.get(position).historyTimestamp,
                DateUtils.SIMPLE_DATE_FORMAT));
    }

    @Override
    public int getItemCount() {
        return this.definitions.size();
    }

    public void removeAll() {
        this.definitions.clear();
        this.notifyDataSetChanged();
    }

    static class ViewHolder extends RecyclerView.ViewHolder
        implements View.OnClickListener {

        @BindView(R.id.history_item_container) RelativeLayout historyItemContainer;
        @BindView(R.id.history_letter_image) ImageView letterImage;
        @BindView(R.id.word_text) TextView wordText;
        @BindView(R.id.timestamp_text) TextView timestampText;

        private OnHistoryItemClickListener onHistoryItemClickListener;

        ViewHolder(View itemView, OnHistoryItemClickListener onHistoryItemClickListener) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            this.onHistoryItemClickListener = onHistoryItemClickListener;
            historyItemContainer.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (v instanceof RelativeLayout) {
                onHistoryItemClickListener.onHistoryItemClick(v);
            }
        }

        interface OnHistoryItemClickListener {
            void onHistoryItemClick(View caller);
        }
    }
}
