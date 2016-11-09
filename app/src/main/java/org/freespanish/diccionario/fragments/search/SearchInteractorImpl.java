package org.freespanish.diccionario.fragments.search;

import android.content.Context;
import android.os.AsyncTask;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import org.freespanish.diccionario.DiccionarioApp;
import org.freespanish.diccionario.database.DiccionarioDBHelper;
import org.freespanish.diccionario.database.models.Definition;
import org.freespanish.diccionario.utils.Constants;
import org.freespanish.diccionario.utils.InternetUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Interceptor;
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
 *    Diccionario is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *    GNU General Public License for more details.
 *
 *    You should have received a copy of the GNU General Public License
 *    along with Diccionario.  If not, see <http://www.gnu.org/licenses/>.
 */

public class SearchInteractorImpl implements SearchInteractor {

    @Override
    public void getDefinition(final OnDefinitionRetrievedListener onDefinitionRetrievedListener,
                              String word, boolean isAnId) {

        /*OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url(isAnId ? Constants.BASE_URL + word : Constants.SEARCH_URL + word)
                .build();

        Call call = client.newCall(request);
        call.enqueue(new Callback() {

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                onDefinitionRetrievedListener.onDefinitionReceived(response.body().string());
            }

            @Override
            public void onFailure(Call call, IOException e) {
                onDefinitionRetrievedListener.onDefinitionReceivedError();
            }

        });*/

        if (!InternetUtils.isInternetAvailable()) {
            onDefinitionRetrievedListener.onDefinitionReceivedError();
            return;
        }

        final WebView scraper = new WebView(DiccionarioApp.getAppContext());
        scraper.getSettings().setJavaScriptEnabled(true);
        scraper.addJavascriptInterface(new JSInterfaceInterceptor(onDefinitionRetrievedListener), "HtmlScraper");
        scraper.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                scraper.loadUrl("javascript:window.HtmlScraper.processHTML('<html>'+document.getElementsByTagName('html')[0].innerHTML+'</html>');");
            }
        });
        scraper.loadUrl(isAnId ? Constants.BASE_URL + word : Constants.SEARCH_URL + word);
    }

    private class JSInterfaceInterceptor {

        private OnDefinitionRetrievedListener onDefinitionRetrievedListener;

        JSInterfaceInterceptor (OnDefinitionRetrievedListener onDefinitionRetrievedListener) {
            this.onDefinitionRetrievedListener = onDefinitionRetrievedListener;
        }

        @android.webkit.JavascriptInterface
        @SuppressWarnings("unused")
        public void processHTML(String html) {
            if (html.contains("Please enable JavaScript to view the page content."))
                return;
            this.onDefinitionRetrievedListener.onDefinitionReceived(html);
            this.onDefinitionRetrievedListener = null;
        }
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
