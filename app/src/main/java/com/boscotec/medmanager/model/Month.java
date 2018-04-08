package com.boscotec.medmanager.model;

import com.boscotec.medmanager.interfaces.RecyclerItem;

/**
 * Created by Johnbosco on 24-Mar-18.
 */

public class Month implements RecyclerItem {
    private long id;
    private String name;

    public Month(){}

    //getter
    public long getId() {
        return id;
    }
    public String getName() {
        return name;
    }

    //setter
    public void setId(long id) {
        this.id = id;
    }
    public void setName(String name) {this.name = name;}

    @Override
    public int getRecyclerItemType() {
        return RecyclerItem.TYPE_MONTH;
    }
}
