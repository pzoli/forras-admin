/*
 * Copyright 2013 Integrity Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * 
 * @author Szilveszter Drozdik
 * 
 */
package hu.infokristaly.utils;

import java.util.Locale;
import java.util.ResourceBundle;

/**
 * The I18n class. Used for user interface and message translations.
 */
public class I18n {

    /** The Constant MARK_UNKNOWN. */
    private static final String MARK_UNKNOWN = "??";

    /** The Constant I18N. */
    private static final String I18N = "i18n";

    /** The currentbundle. */
    private static ResourceBundle CURRENTBUNDLE;

    /** The currentlocale. */
    private static Locale CURRENTLOCALE;

    /**
     * Gets the string by key.
     * 
     * @param key
     *            the key in i18n_[lc].properties file
     * @param locale
     *            the locale
     * @return the string
     */

    public static String getString(String key, Locale locale) {
        String text = null;
        if (!locale.equals(I18n.CURRENTLOCALE)) {
            try {
                I18n.CURRENTLOCALE = locale;
                CURRENTBUNDLE = ResourceBundle.getBundle(I18N, locale);
            } catch (Exception e) {
                // skip
            }
        }

        try {
            if (CURRENTBUNDLE != null) {
                text = CURRENTBUNDLE.getString(key);
            }
        } catch (Exception e) {
            // skip
        }
        return text == null ? MARK_UNKNOWN + key + MARK_UNKNOWN : text;
    }
}