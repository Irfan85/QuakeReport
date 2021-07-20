/*
 * Copyright (C) 2016 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.android.quakereport;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import java.util.ArrayList;

public class EarthquakeActivity extends AppCompatActivity {

    private static final String LOG_TAG = EarthquakeActivity.class.getName();

    private EarthquakeViewModel viewModel;

    private EarthquakeListAdapter earthquakeListAdapter;

    private boolean isConnectionAvailable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.earthquake_activity);

        // Checking whether internet connection exists or not
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            connectivityManager.registerDefaultNetworkCallback(new ConnectivityManager.NetworkCallback(){
                @Override
                public void onAvailable(@NonNull Network network) {
                    super.onAvailable(network);
                    isConnectionAvailable = true;
                }

                @Override
                public void onUnavailable() {
                    super.onUnavailable();
                    isConnectionAvailable = false;
                }
            });
        }else{
            NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
            isConnectionAvailable = (networkInfo != null) && networkInfo.isConnected();
        }

        // Get the ViewModel
        viewModel = new ViewModelProvider(this).get(EarthquakeViewModel.class);

        // Find a reference to the {@link ListView} in the layout
        final ListView earthquakeListView = (ListView) findViewById(R.id.list);

        final TextView emptyListTextView = findViewById(R.id.emptyListText);

        // Show the progress bar if data is still loading.
        // If data has been loaded, hide the progress bar and enable the empty list text view.
        final ProgressBar quakeProgressBar = findViewById(R.id.quakeProgressBar);
        viewModel.getIsLoading().observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean isLoading) {
                if (!isLoading) {
                    quakeProgressBar.setVisibility(View.GONE);
                    earthquakeListView.setEmptyView(emptyListTextView);
                    if (isConnectionAvailable){
                    emptyListTextView.setText(R.string.empty_list_message);
                    }else{
                        emptyListTextView.setText(R.string.no_network_message);
                    }
                }
            }
        });

        // Set up earthquake list live data observer
        Observer<ArrayList<Earthquake>> earthquakeListObserver = new Observer<ArrayList<Earthquake>>() {
            @Override
            public void onChanged(ArrayList<Earthquake> earthquakes) {
                if (earthquakes != null) {
                    earthquakeListAdapter = new EarthquakeListAdapter(EarthquakeActivity.this, earthquakes);
                    earthquakeListView.setAdapter(earthquakeListAdapter);
                }

                earthquakeListAdapter.notifyDataSetChanged();
            }
        };

        // Bind the observer to the earthquake list live data
        viewModel.getEarthquakeLiveData().observe(this, earthquakeListObserver);

        earthquakeListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Earthquake currentQuake = earthquakeListAdapter.getItem(i);

                assert currentQuake != null;
                Uri earthquakeUri = Uri.parse(currentQuake.getUrl());
                Intent websiteIntent = new Intent(Intent.ACTION_VIEW, earthquakeUri);

                startActivity(websiteIntent);
            }
        });
    }

}
