package org.freespanish.diccionario.fragments.fav;

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

public class FavInteractorImpl implements FavInteractor {

    @Override
    public ArrayList<Definition> retrieveFavorites(Context context) {
        ArrayList<Definition> definitions = new ArrayList<>();
        Cursor history = cupboard().withDatabase(DiccionarioDBHelper.getInstance(context)
                .getReadableDatabase())
                .query(Definition.class).getCursor();
        try {
            QueryResultIterable<Definition> itr = cupboard().withCursor(history).iterate(Definition.class);
            for (Definition def : itr) {
                if (def.isFavorite)
                    definitions.add(def);
            }
        } finally {
            history.close();
        }

        return definitions;
    }

    @Override
    public void removeFavorite(Context context, Definition definition) {
        if (!definition.isHistory)
            cupboard().withDatabase(DiccionarioDBHelper.getInstance(context)
                    .getWritableDatabase()).delete(Definition.class, "word = ? and isHistory = ? and isFavorite = ?", definition.word, "0", "1");
        else {
            definition.isFavorite = false;
            cupboard().withDatabase(DiccionarioDBHelper.getInstance(context)
                    .getWritableDatabase()).put(definition);
        }
    }
}
