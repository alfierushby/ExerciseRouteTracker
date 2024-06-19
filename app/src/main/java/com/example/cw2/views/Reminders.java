package com.example.cw2.views;

import static com.example.cw2.config.Configuration.LOG_TAG;

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
import android.util.Log;
import android.view.View;

import com.example.cw2.AppState;
import com.example.cw2.Events;
import com.example.cw2.R;
import com.example.cw2.databinding.ActivityRemindersBinding;
import com.example.cw2.databinding.ActivitySettingsBinding;
import com.example.cw2.viewmodels.RemindersViewModel;
import com.example.cw2.viewmodels.SettingsViewModel;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class Reminders extends AppCompatActivity {
    RemindersViewModel remindersViewModel;

    private ActivityResultLauncher<String> requestPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {});
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reminders);

        // Set data binding
        ActivityRemindersBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_reminders);
        remindersViewModel = new ViewModelProvider(this).get(RemindersViewModel.class);
        binding.setRemindersViewModel(remindersViewModel);
        binding.setLifecycleOwner(this);

        //Setup viewmodel resources.
        remindersViewModel.setColorResources(((AppState) getApplication()).getColorResources());

        // Setup recycler view
        RecyclerView recyclerView = findViewById(R.id.ReminderRecycler);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        RemindersRecylcerViewAdapter adapter = new RemindersRecylcerViewAdapter(this
                , remindersViewModel.getReminderRepo().getAllLive().getValue());
        recyclerView.setAdapter(adapter);

        // Observe the live data and update the reminder repo
        remindersViewModel.getReminderRepo().getAllLive().observe(this, adapter::setData);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        perm(Manifest.permission.ACCESS_BACKGROUND_LOCATION, R.string.Permission_Explain_Background, (a,b)->{
            requestPermissionLauncher.launch(Manifest.permission.ACCESS_BACKGROUND_LOCATION);
        });
        perm(Manifest.permission.ACCESS_COARSE_LOCATION, R.string.Permission_Explain, (a,b)->{
            requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION);
        });
    }

    /**
     * Checks and requests permission if not granted.
     * @param permCheck Permission to check.
     * @param message   Message to display if permission not granted.
     * @param listener  Listener for positive button click.
     */
    public void perm(String permCheck, int message,android.content.DialogInterface.OnClickListener listener){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        if(ContextCompat.checkSelfPermission(this, permCheck) != PackageManager.PERMISSION_GRANTED) {
            // Ask for permissions for location
            builder.setMessage(message).setTitle(R.string.Permission_Title);
            builder.setPositiveButton("OK", listener);
            AlertDialog alertDialog = builder.create();
            alertDialog.show();
        }
    }

    /**
     * Get the RemindersViewModel instance.
     * @return The RemindersViewModel instance.
     */
    public RemindersViewModel getRemindersViewModel() {
        return remindersViewModel;
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
     * Open the view for a specific reminder (editing and viewing its data)
     * @param v The view triggering the method.
     */
    public void newReminder(View v){
        // Set editing to false, for a new Movement
        getRemindersViewModel().getStateRepo().setEditing(false);

        Intent intent = new Intent(Reminders.this,ReminderCreator.class);
        startActivity(intent);
    }

    /**
     * Event handler for opening the view of a reminder.
     * @param event The OpenReminderView event.
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void eventOpenView(Events.OpenReminderView event){
        Log.d(LOG_TAG,"testinit1");
        Intent intent = new Intent(Reminders.this,ReminderCreator.class);
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