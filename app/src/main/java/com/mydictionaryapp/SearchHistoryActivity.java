
package com.mydictionaryapp;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.mydictionaryapp.adapter.SearchHistoryAdapter;
import com.mydictionaryapp.model.SearchHistoryBeans;

public class SearchHistoryActivity extends Activity {
    SearchHistoryBeans searchHistory = new SearchHistoryBeans();
    Gson gson = new Gson();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_history);

        searchHistory = (SearchHistoryBeans) getIntent().getSerializableExtra("SearchHistoryList");

        ListView listView = (ListView) findViewById(R.id.lvSearchHistory);
        SearchHistoryAdapter adapter = new SearchHistoryAdapter(getApplicationContext(), searchHistory.getHistoryList());
        listView.setAdapter(adapter);
        TextView emptyText = (TextView)findViewById(android.R.id.empty);
        listView.setEmptyView(emptyText);


    }

}