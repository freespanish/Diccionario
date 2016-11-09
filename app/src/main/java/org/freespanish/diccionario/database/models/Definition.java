package org.freespanish.diccionario.database.models;

import android.support.annotation.NonNull;

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

public class Definition implements Comparable<Definition> {

    public Long _id;
    public String word;
    public Boolean isFavorite;
    public Boolean isHistory;
    public Long historyTimestamp;

    public Definition() {
    }

    public Definition(String word, Boolean isFavorite, Boolean isHistory, Long historyTimestamp) {
        this.word = word;
        this.isFavorite = isFavorite;
        this.isHistory = isHistory;
        this.historyTimestamp = historyTimestamp;
    }

    public Definition(String word, Boolean isFavorite) {
        this.word = word;
        this.isFavorite = isFavorite;
        this.isHistory = false;
        this.historyTimestamp = 0L;
    }

    public Definition(String word, Long historyTimestamp) {
        this.word = word;
        this.isHistory = true;
        this.historyTimestamp = historyTimestamp;
        this.isFavorite = false;
    }

    @Override
    public int compareTo(@NonNull Definition o) {
        if (this.historyTimestamp < o.historyTimestamp)
            return 1;
        else if (this.historyTimestamp > o.historyTimestamp)
            return -1;
        else
            return 0;
    }
}
