package com.sinyuk.jianyimaterial.utils;

import android.content.SearchRecentSuggestionsProvider;

/**
 * Created by Sinyuk on 16.2.29.
 */
public class SuggestionProvider extends SearchRecentSuggestionsProvider {
    public final static String AUTHORITY = "com.sinyuk.jianyimaterial.utils.SuggestionProvider";
    public final static int MODE = DATABASE_MODE_QUERIES;

    public SuggestionProvider() {
        setupSuggestions(AUTHORITY, MODE);
    }

}
