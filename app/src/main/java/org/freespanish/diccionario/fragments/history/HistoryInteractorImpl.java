package org.freespanish.diccionario.fragments.history;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import org.freespanish.diccionario.database.DiccionarioDBHelper;
import org.freespanish.diccionario.database.models.Definition;

import java.util.ArrayList;

import nl.qbusict.cupboard.QueryResultIterable;

import static nl.qbusict.cupboard.CupboardFactory.cupboard;

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

public class HistoryInteractorImpl implements HistoryInteractor {

    @Override
    public ArrayList<Definition> retrieveHistory(Context context) {
        ArrayList<Definition> definitions = new ArrayList<>();
        Cursor history = cupboard().withDatabase(DiccionarioDBHelper.getInstance(context)
                .getReadableDatabase())
                .query(Definition.class).getCursor();
        try {
            QueryResultIterable<Definition> itr = cupboard().withCursor(history).iterate(Definition.class);
            for (Definition def : itr) {
                if (def.isHistory)
                    definitions.add(def);
            }
        } finally {
            history.close();
        }

        return definitions;

    }

    @Override
    public void clearHistory(Context context) {
        cupboard().withDatabase(DiccionarioDBHelper.getInstance(context)
                .getWritableDatabase()).delete(Definition.class, "isHistory = ? and isFavorite = ?", "1", "0");
        ContentValues values = new ContentValues(1);
        values.put("isHistory", 0);
        cupboard().withDatabase(DiccionarioDBHelper.getInstance(context)
                .getWritableDatabase()).update(Definition.class, values, "isHistory = ?", "1");
    }
}
