package com.boscotec.medmanager.interfaces;

/**
 * Created by Johnbosco on 24-Mar-18.
 */
public interface RecyclerItem {
    int TYPE_MONTH = 1;
    int TYPE_MED = 2;

    int getRecyclerItemType();
}
