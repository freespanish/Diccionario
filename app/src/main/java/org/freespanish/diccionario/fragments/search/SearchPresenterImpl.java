package org.freespanish.diccionario.fragments.search;

import android.content.Context;
import android.view.View;

import org.freespanish.diccionario.database.models.Definition;
import org.freespanish.diccionario.utils.Constants;

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

public class SearchPresenterImpl implements SearchPresenter, SearchInteractorImpl.OnDefinitionRetrievedListener {

    private SearchFragmentView searchFragmentView;
    private SearchInteractor searchInteractor;

    public SearchPresenterImpl(SearchFragmentView searchFragmentView) {
        this.searchFragmentView = searchFragmentView;
        this.searchInteractor = new SearchInteractorImpl();
    }

    @Override
    public void onDestroy() {
        this.searchFragmentView = null;
    }

    @Override
    public void prepareDefinition(String word, boolean isAnId) {
        this.searchFragmentView.setProgressBarVisibility(View.VISIBLE);
        this.searchInteractor.getDefinition(this, word, isAnId);
    }

    @Override
    public void notifyDefinitionToStore(Definition definition, Context context) {
        this.searchInteractor.storeDefinition(definition, context);
    }

    @Override
    public Definition notifyDefinitionToRetrieve(String query, String matchQuery, Context context) {
        return this.searchInteractor.retrieveDefinition(query, matchQuery, context);
    }


    @Override
    public void onDefinitionReceived(String htmlContent) {
        if (searchFragmentView != null) {
            htmlContent = htmlContent
                    .replace("/css/dile.css.pagespeed.ce.3ZIszsKm5U.css", "file:///android_asset/style.css")
                    .replace("Real Academia Española © Todos los derechos reservados",
                            "Luchemos contra la RAE por una cultura libre.")
                    .replace("<ul>", "<h2>Quizá quieres decir...</h2><ul>")
                    .replace(" ◆ ", "<br>")
                    .replaceAll(Constants.REGEX_REMOVE_SCRIPTS, "");
            this.searchFragmentView.populateWebView(htmlContent);
        }
    }

    @Override
    public void onDefinitionReceivedError() {
        if (searchFragmentView != null)
            this.searchFragmentView.handleError();
    }
}
