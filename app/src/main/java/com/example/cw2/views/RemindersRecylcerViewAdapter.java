package com.example.cw2.views;

import static com.example.cw2.config.Configuration.LOG_TAG;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cw2.Events;
import com.example.cw2.R;
import com.example.cw2.models.ReminderModel;
import com.example.cw2.repos.ReminderRepo;
import com.example.cw2.repos.StateRepo;

import org.greenrobot.eventbus.EventBus;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import dagger.hilt.EntryPoint;
import dagger.hilt.InstallIn;
import dagger.hilt.android.EntryPointAccessors;
import dagger.hilt.components.SingletonComponent;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.internal.schedulers.NewThreadScheduler;

public class RemindersRecylcerViewAdapter extends RecyclerView.Adapter<RemindersRecylcerViewAdapter.ViewHolder> {
    private final LayoutInflater inflater;
    private final Context context;
    private final List<ReminderModel> data = new ArrayList<>();
    private final RepoEntryPoint entryPoint;

    /**
     * Important for the individual elements to communicate with the repo, without referncing
     * any Model specifically.
     */
    @EntryPoint
    @InstallIn(SingletonComponent.class)
    interface RepoEntryPoint {
        public ReminderRepo getReminderRepo();
        public StateRepo getStateRepo();
    }

    /**
     * Get the data list.
     * @return The list of ReminderModel data.
     */
    private List<ReminderModel> getData() {

        return data;
    }

    /**
     * Set the data for the adapter and notify data set changed.
     * @param data The list of ReminderModel data.
     */
    public void setData(List<ReminderModel> data) {
        Log.d(LOG_TAG,"New reminder data");
        if(data == null)
            return;
        getData().clear();
        getData().addAll(data);
        // Fine for small amounts of data
        notifyDataSetChanged();
    }

    /**
     * Constructor for the adapter.
     * @param context The context.
     * @param data    The list of ReminderModel data.
     */
    public RemindersRecylcerViewAdapter(Context context,List<ReminderModel> data) {
        this.inflater = LayoutInflater.from(context);
        this.context = context;
        this.entryPoint = EntryPointAccessors.fromApplication(context.getApplicationContext()
                ,RepoEntryPoint.class);
        // Set initial data
        setData(data);
    }

    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.reminder_card, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(data.get(position));
    }

    /**
     * @return A repo entrypoint giving the reminder repository from Hilt
     */
    public RepoEntryPoint getEntryPoint() {
        return entryPoint;
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    /**
     * Get the context.
     * @return The context.
     */
    public Context getContext() {
        return context;
    }

    // stores and recycles views as they are scrolled off screen
    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView name, radius, date;
        ReminderModel model;

        /**
         * ViewHolder constructor.
         * @param itemView The item view.
         */
        ViewHolder(View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.MovementName);
            radius = itemView.findViewById(R.id.ReminderRadius);
            date = itemView.findViewById(R.id.ReminderDate);

            itemView.findViewById(R.id.MovementDelete).setOnClickListener((e)->{
                // Delete the reminder
                Completable exec = getEntryPoint().getReminderRepo().deleteReminder(model.getEid());
                exec.subscribeOn(new NewThreadScheduler()).subscribe();
            });

            itemView.findViewById(R.id.MovementView).setOnClickListener((e)->{
                // Open the view for the reminder
                EventBus.getDefault().post(new Events.OpenReminderView());

                // Set global variables
                getEntryPoint().getStateRepo().setEditing(true);
                getEntryPoint().getStateRepo().setEId(model.getEid());

            });

        }

        /**
         * Bind data to the views.
         * @param model The ReminderModel to bind.
         */
        void bind ( final ReminderModel model )
        {
            this.model = model;
            name.setText(String.format(getContext().getResources().getString(R.string.NameVal),model.getName()));
            radius.setText(String.format(getContext().getResources().getString(R.string.RadiusVal),String.valueOf(model.getRadius())));
            Date dateTime = new Date(model.getDate());
            DateFormat dateFormat = new SimpleDateFormat("EEEE dd/MM/yy", Locale.ENGLISH);
            date.setText(dateFormat.format(dateTime));
        }



    }
}
