package com.example.cw2.views;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;
import android.view.View;

import com.example.cw2.AppState;
import com.example.cw2.R;
import com.example.cw2.databinding.ActivitySettingsBinding;
import com.example.cw2.viewmodels.SettingsViewModel;

import dagger.hilt.android.AndroidEntryPoint;
import dagger.hilt.android.lifecycle.HiltViewModel;

@AndroidEntryPoint
public class Settings extends AppCompatActivity {

    SettingsViewModel settingsViewModel;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        // Set data binding
        ActivitySettingsBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_settings);
        settingsViewModel = new ViewModelProvider(this).get(SettingsViewModel.class);
        binding.setSettingsViewModel(settingsViewModel);
        binding.setLifecycleOwner(this);

        //Setup viewmodel resources.
        settingsViewModel.setColorResources(((AppState) getApplication()).getColorResources());
    }

    public void finish(View v){
        finish();
    }
}