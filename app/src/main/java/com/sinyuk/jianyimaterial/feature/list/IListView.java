package com.sinyuk.jianyimaterial.feature.list;

import android.support.annotation.NonNull;

/**
 * Created by Sinyuk on 16.3.27.
 */
public interface IListView {
    void loadMore(int pageIndex);

    void refresh();

    void hintVolleyError(@NonNull String message);

    void hintParseError(@NonNull String message);
}
