package org.freespanish.diccionario.fragments.search;

import android.content.Context;

import org.freespanish.diccionario.database.models.Definition;

import java.util.concurrent.ExecutionException;

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

public interface SearchInteractor {

    interface OnDefinitionRetrievedListener {
        void onDefinitionReceived(String htmlContent);
        void onDefinitionReceivedError();
    }

    void getDefinition(OnDefinitionRetrievedListener onDefinitionRetrievedListener, String word,
                       boolean isAnId);
    void storeDefinition(Definition definition, Context context);
    Definition retrieveDefinition(String query, String matchQuery, Context context);

}
