package org.freespanish.diccionario.fragments.search;

import android.content.Context;

import org.freespanish.diccionario.database.DiccionarioDBHelper;
import org.freespanish.diccionario.database.models.Definition;
import org.freespanish.diccionario.utils.Constants;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import static nl.qbusict.cupboard.CupboardFactory.cupboard;

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

public class SearchInteractorImpl implements SearchInteractor {

    @Override
    public void getDefinition(final OnDefinitionRetrievedListener onDefinitionRetrievedListener,
                              String word, boolean isAnId) {
        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url(isAnId ? Constants.BASE_URL + word : Constants.SEARCH_URL + word)
                .build();

        Call call = client.newCall(request);
        call.enqueue(new Callback() {

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String htmlContent = response.body().string()
                        .replace("/css/dile.css.pagespeed.ce.3ZIszsKm5U.css", "file:///android_asset/style.css")
                        .replace("Real Academia Espa&#x00F1;ola &copy; Todos los derechos reservados",
                                "Luchemos contra la RAE por una cultura libre.")
                        .replace("<ul>", "<h2>Quizá quieres decir...</h2><ul>")
                        .replace(" ◆ ", "<br>")
                        .replaceAll(Constants.REGEX_REMOVE_SCRIPTS, "");
                onDefinitionRetrievedListener.onDefinitionReceived(htmlContent);
            }

            @Override
            public void onFailure(Call call, IOException e) {
                onDefinitionRetrievedListener.onDefinitionReceivedError();
            }

        });

    }

    @Override
    public void storeDefinition(Definition definition, Context context) {
        cupboard().withDatabase(DiccionarioDBHelper.getInstance(context)
                .getWritableDatabase()).put(definition);
    }

    @Override
    public Definition retrieveDefinition(String query, String matchQuery, Context context) {
        return cupboard().withDatabase(DiccionarioDBHelper
                .getInstance(context).getReadableDatabase()).query(Definition.class)
                .withSelection(query, matchQuery).get();
    }
}
