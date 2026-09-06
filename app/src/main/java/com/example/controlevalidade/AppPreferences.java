package com.example.controlevalidade;

import android.content.Context;
import android.content.SharedPreferences;

public final class AppPreferences {

    private static final String PREFERENCES_FILE = "app_settings";
    private static final String KEY_SORT_ORDER = "sort_order";

    public static final String SORT_EXPIRATION = "expiration";
    public static final String SORT_NAME = "name";
    public static final String SORT_CATEGORY = "category";

    private final SharedPreferences preferences;

    public AppPreferences(Context context) {
        preferences = context.getSharedPreferences(PREFERENCES_FILE, Context.MODE_PRIVATE);
    }

    public String getSortOrder() {
        String sortOrder = preferences.getString(KEY_SORT_ORDER, SORT_EXPIRATION);
        return isValidSortOrder(sortOrder) ? sortOrder : SORT_EXPIRATION;
    }

    public void setSortOrder(String sortOrder) {
        if (!isValidSortOrder(sortOrder)) {
            throw new IllegalArgumentException("Invalid sort order");
        }
        preferences.edit().putString(KEY_SORT_ORDER, sortOrder).apply();
    }

    public void restoreDefaultSortOrder() {
        preferences.edit().remove(KEY_SORT_ORDER).apply();
    }

    private boolean isValidSortOrder(String sortOrder) {
        return SORT_EXPIRATION.equals(sortOrder)
                || SORT_NAME.equals(sortOrder)
                || SORT_CATEGORY.equals(sortOrder);
    }
}
