package com.example.cw2.views;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;

import com.example.cw2.AppState;
import com.example.cw2.Events;
import com.example.cw2.R;
import com.example.cw2.databinding.ActivityMovementsBinding;
import com.example.cw2.viewmodels.MovementsViewModel;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class Movements extends AppCompatActivity {
    private MovementsViewModel movementsViewModel;

    // Activity result launcher for requesting permissions
    private final ActivityResultLauncher<String> requestPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), (isGranted) -> {});
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movements);

        // Set data binding
        ActivityMovementsBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_movements);
        movementsViewModel = new ViewModelProvider(this).get(MovementsViewModel.class);
        binding.setMovementsViewModel(movementsViewModel);
        binding.setLifecycleOwner(this);

        //Setup viewmodel resources.
        movementsViewModel.setColorResources(((AppState) getApplication()).getColorResources());

        // Setup recycler view
        RecyclerView recyclerView = findViewById (R.id.MovementRecycler) ;

        recyclerView . setLayoutManager ( new LinearLayoutManager( this ) ) ;
        MovementsRecylerViewAdapter adapter = new MovementsRecylerViewAdapter ( this
                , movementsViewModel.getMovementRepo().getAllLive().getValue()) ;
        recyclerView . setAdapter ( adapter ) ;

        // Observe the live data and update the reminder repo
        movementsViewModel.getMovementRepo().getAllLive().observe(this, adapter::setData);
        // Request background location permission
        perm(Manifest.permission.ACCESS_BACKGROUND_LOCATION, R.string.Permission_Explain_Background, (a,b)->{
            requestPermissionLauncher.launch(Manifest.permission.ACCESS_BACKGROUND_LOCATION);
        });
        // Request coarse location permission
        perm(Manifest.permission.ACCESS_COARSE_LOCATION, R.string.Permission_Explain, (a,b)->{
            requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION);
        });
    }

    /**
     * Check and request permission if not granted.
     * @param permCheck Permission to check.
     * @param message   Message to display if permission not granted.
     * @param listener  Listener for positive button click.
     */
    public void perm(String permCheck, int message, AlertDialog.OnClickListener listener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        if (ContextCompat.checkSelfPermission(this, permCheck) != PackageManager.PERMISSION_GRANTED) {
            // Ask for permissions for location
            builder.setMessage(message).setTitle(R.string.Permission_Title);
            builder.setPositiveButton("OK", listener);
            AlertDialog alertDialog = builder.create();
            alertDialog.show();
        }
    }

    /**
     * Get the MovementsViewModel instance.
     * @return The MovementsViewModel instance.
     */
    public MovementsViewModel getMovementsViewModel() {
        return movementsViewModel;
    }

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        EventBus.getDefault().unregister(this);
        super.onStop();
    }

    /**
     * Start a new movement creation.
     * @param v The view triggering the method.
     */
    public void newMovement(View v){
        // Set editing to false, for a new Movement
        getMovementsViewModel().getStateRepo().setEditing(false);

        Intent intent = new Intent(Movements.this,MovementCreator.class);
        startActivity(intent);
    }
    /**
     * Open the MovementCreator view.
     * @param event The event to open the MovementCreator view.
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void eventOpenView(Events.OpenMovementView event){
        Intent intent = new Intent(Movements.this,MovementCreator.class);
        startActivity(intent);
    }

    /**
     * Finish the activity.
     * @param v The view triggering the method.
     */
    public void finish(View v){
        finish();
    }

}