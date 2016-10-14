package org.freespanish.diccionario.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

public class RegExUtils {

    public static String getStringFromRegex(String fullString, String regex) {
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(fullString);
        return matcher.find() ? matcher.group(0) : "";
    }

}
