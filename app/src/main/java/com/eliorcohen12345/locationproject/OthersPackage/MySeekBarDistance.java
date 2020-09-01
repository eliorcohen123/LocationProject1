package com.eliorcohen12345.locationproject.OthersPackage;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.PreferenceManager;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

import com.eliorcohen12345.locationproject.R;

// The main class of the SeekBar
public class MySeekBarDistance extends Preference implements OnSeekBarChangeListener, OnPreferenceChangeListener {

    private SeekBar seekBar;
    private SharedPreferences sharedPreferences;
    private TextView txtSummary;
    private String units;

    public MySeekBarDistance(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    protected View onCreateView(ViewGroup parent) {
        super.onCreateView(parent);
        View view = LayoutInflater.from(getContext()).inflate(R.layout.my_seekbar_preference, parent, false);
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        seekBar = view.findViewById(R.id.seekBar);
        txtSummary = view.findViewById(R.id.summary);
        units = this.sharedPreferences.getString(getContext().getString(R.string.key_units), getContext().getResources().getStringArray(R.array.distanceValues)[0]);
        int max = 50000;
        seekBar.setMax(max);
        int currentProgress = this.sharedPreferences.getInt(getKey(), 0);
        String stringBuilder = currentProgress + units;
        setSummary(stringBuilder);
        seekBar.setProgress(currentProgress);
        seekBar.setOnSeekBarChangeListener(this);
        findPreferenceInHierarchy(getContext().getString(R.string.key_units)).setOnPreferenceChangeListener(this);
        return view;
    }

    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        txtSummary.setText(String.valueOf(progress));
        Editor editor = sharedPreferences.edit();
        editor.putInt(getKey(), progress).apply();
    }

    public void onStartTrackingTouch(SeekBar seekBar) {
    }

    public void onStopTrackingTouch(SeekBar seekBar) {
    }

    public boolean onPreferenceChange(Preference preference, Object newValue) {
        String value = String.valueOf(newValue);
        int currentProgress = this.seekBar.getProgress();
        if ((value.equals(getContext().getResources().getStringArray(R.array.distanceValues)[0]) && currentProgress != 0) ||
                (value.equals(getContext().getResources().getStringArray(R.array.distanceValues)[1]) && currentProgress != 0)) {
            seekBar.setProgress((int) (currentProgress * 0.9144d));
        }
        return true;
    }

}
