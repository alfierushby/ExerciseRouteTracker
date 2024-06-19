package com.example.cw2.views;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;
import android.view.View;

import com.example.cw2.AppState;
import com.example.cw2.R;
import com.example.cw2.databinding.ActivityDataOverviewBinding;
import com.example.cw2.databinding.ActivityHomeBinding;
import com.example.cw2.viewmodels.DataOverviewViewModel;
import com.example.cw2.viewmodels.HomeViewModel;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class DataOverview extends AppCompatActivity {
    // ViewModel for managing state
    DataOverviewViewModel dataOverviewViewModel;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Set the content view to the layout resource
        setContentView(R.layout.activity_data_overview);

        // Set data binding
        ActivityDataOverviewBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_data_overview);
        // Initialize DataOverviewViewModel using ViewModelProvider
        dataOverviewViewModel = new ViewModelProvider(this).get(DataOverviewViewModel.class);
        // Bind the ViewModel to the layout
        binding.setDataOverviewViewModel(dataOverviewViewModel);
        binding.setLifecycleOwner(this);

        //Setup viewmodel resources.
        dataOverviewViewModel.setColorResources(((AppState) getApplication()).getColorResources());
    }

    public void finish(View v){
        finish();
    }
}