
package com.mydictionaryapp.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by AliMert on 31.10.2014.
 */
public class SearchHistoryBeans implements Serializable {

    ArrayList<String> historyList = new ArrayList<String>();

    public ArrayList<String> getHistoryList() {
        return historyList;
    }

    public void setHistoryList(ArrayList<String> historyList) {
        this.historyList = historyList;
    }

}
