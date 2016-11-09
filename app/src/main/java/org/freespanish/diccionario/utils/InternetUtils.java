package org.freespanish.diccionario.utils;

import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import org.freespanish.diccionario.DiccionarioApp;

import static android.content.Context.CONNECTIVITY_SERVICE;

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

public class InternetUtils {

    public static boolean isInternetAvailable() {
        NetworkInfo ni = ((ConnectivityManager) DiccionarioApp.getAppContext().
                getSystemService(CONNECTIVITY_SERVICE)).getActiveNetworkInfo();
        return ni != null && ni.isConnected() && ni.isAvailable();
    }
}
