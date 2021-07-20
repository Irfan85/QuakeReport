package com.example.android.quakereport;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.ArrayList;

public class EarthquakeViewModel extends ViewModel {
    private MutableLiveData<ArrayList<Earthquake>> earthquakeListLiveData;
    private MutableLiveData<Boolean> isLoading = new MutableLiveData<>();


    public EarthquakeViewModel(){
        // Start fetching data from internet since that process will run on separate thread.
        EarthquakeRepository.extractEarthquakes(new EarthquakeRepository.UpdateUICallback() {
            @Override
            public void onCompleted(ArrayList<Earthquake> earthquakes) {
                getEarthquakeLiveData().postValue(earthquakes);
                isLoading.postValue(false);
            }
        });
    }

    public MutableLiveData<ArrayList<Earthquake>> getEarthquakeLiveData() {
        if (earthquakeListLiveData == null) {
            earthquakeListLiveData = new MutableLiveData<>();
            earthquakeListLiveData.setValue(new ArrayList<Earthquake>());
        }

        return earthquakeListLiveData;
    }

    public MutableLiveData<Boolean> getIsLoading() {
        // Since isLoading is still null, onCompleted() must not have been called yet.
        // Which means the thread is still working.
        if (isLoading == null){
            isLoading = new MutableLiveData<>();
            isLoading.setValue(true);
        }

        return isLoading;
    }
}
