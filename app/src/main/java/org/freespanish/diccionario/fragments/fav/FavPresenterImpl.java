package org.freespanish.diccionario.fragments.fav;

import android.content.Context;

import org.freespanish.diccionario.database.models.Definition;

import java.util.ArrayList;

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

public class FavPresenterImpl implements FavPresenter {

    private FavView favView;
    private FavInteractor favInteractor;

    public FavPresenterImpl() {
        this.favInteractor = new FavInteractorImpl();
    }

    public FavPresenterImpl (FavView favView) {
        this.favView = favView;
        this.favInteractor = new FavInteractorImpl();
    }

    @Override
    public ArrayList<Definition> notifyRetrieveFavorites(Context context) {
        return favInteractor.retrieveFavorites(context);
    }

    @Override
    public void notifyRemoveFavorite(Context context, Definition definition) {
        this.favInteractor.removeFavorite(context, definition);
    }

    @Override
    public void onDestroy() {
        this.favView = null;
    }
}
