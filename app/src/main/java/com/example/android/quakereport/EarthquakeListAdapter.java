package com.example.android.quakereport;

import android.app.Activity;
import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class EarthquakeListAdapter extends ArrayAdapter<Earthquake> {

    public EarthquakeListAdapter(Activity context, ArrayList<Earthquake> earthquakes) {
        super(context, 0, earthquakes);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View earthquakeListItem = convertView;

        if (earthquakeListItem == null) {
            earthquakeListItem = LayoutInflater.from(getContext()).inflate(R.layout.earthquake_list_item, parent, false);
        }

        Earthquake earthquake = getItem(position);

        assert earthquake != null;

        TextView magnitudeTextView = earthquakeListItem.findViewById(R.id.magnitude);

        // Setting the color of magnitude circle
        GradientDrawable magnitudeCircle = (GradientDrawable) magnitudeTextView.getBackground();
        magnitudeCircle.setColor(getMagnitudeColor(earthquake.getMagnitude()));

        // Show only one decimal point for magnitude
        DecimalFormat decimalFormat = new DecimalFormat("0.0");
        magnitudeTextView.setText(decimalFormat.format(earthquake.getMagnitude()));

        TextView locationTextView = earthquakeListItem.findViewById(R.id.location);
        TextView regionTextView = earthquakeListItem.findViewById(R.id.region);

        // Split the location and region from a single string.
        String locationString = earthquake.getLocation();
        String location;
        String region;

        if (locationString.contains("km")) {
            location = locationString.substring(0, locationString.indexOf("of") + 2);
            region = locationString.substring(location.indexOf("of") + 3);
        } else {
            location = "Near the";
            region = locationString;
        }

        locationTextView.setText(location);
        regionTextView.setText(region);

        TextView dateTextView = earthquakeListItem.findViewById(R.id.date);
        TextView timeTextView = earthquakeListItem.findViewById(R.id.time);

        //Converting UNIX time to human readable form
        Date dateObject = new Date(Long.parseLong(earthquake.getDate()));

        SimpleDateFormat dateFormatter = new SimpleDateFormat("MMM dd yyyy");
        String dateToDisplay = dateFormatter.format(dateObject);

        dateTextView.setText(dateToDisplay);

        SimpleDateFormat timeFormatter = new SimpleDateFormat("hh:mm a");
        String timeToDisplay = timeFormatter.format(dateObject);

        timeTextView.setText(timeToDisplay);

        return earthquakeListItem;
    }

    private int getMagnitudeColor(double magnitude) {
        if (magnitude >= 0 && magnitude < 2) {
            return ContextCompat.getColor(getContext(), R.color.magnitude1);
        } else if (magnitude >= 2 && magnitude < 3) {
            return ContextCompat.getColor(getContext(), R.color.magnitude2);
        } else if (magnitude >= 3 && magnitude < 4) {
            return ContextCompat.getColor(getContext(), R.color.magnitude3);
        } else if (magnitude >= 4 && magnitude < 5) {
            return ContextCompat.getColor(getContext(), R.color.magnitude4);
        } else if (magnitude >= 5 && magnitude < 6) {
            return ContextCompat.getColor(getContext(), R.color.magnitude5);
        } else if (magnitude >= 6 && magnitude < 7) {
            return ContextCompat.getColor(getContext(), R.color.magnitude6);
        } else if (magnitude >= 7 && magnitude < 8) {
            return ContextCompat.getColor(getContext(), R.color.magnitude7);
        } else if (magnitude >= 8 && magnitude < 9) {
            return ContextCompat.getColor(getContext(), R.color.magnitude8);
        } else {
            return ContextCompat.getColor(getContext(), R.color.magnitude9);
        }
    }
}
