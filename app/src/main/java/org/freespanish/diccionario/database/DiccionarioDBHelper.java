package org.freespanish.diccionario.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import org.freespanish.diccionario.database.models.Definition;

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

public class DiccionarioDBHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "diccionario.db";
    private static final int DATABASE_VERSION = 1;
    private static DiccionarioDBHelper diccionarioDBHelper;

    private DiccionarioDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public static DiccionarioDBHelper getInstance(Context context) {
        if (diccionarioDBHelper == null)
            return new DiccionarioDBHelper(context.getApplicationContext());
        return diccionarioDBHelper;
    }

    static {
        cupboard().register(Definition.class);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        cupboard().withDatabase(db).createTables();
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        cupboard().withDatabase(db).upgradeTables();
    }
}
