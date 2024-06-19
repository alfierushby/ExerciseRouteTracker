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
import com.example.cw2.models.MovementModel;
import com.example.cw2.repos.MovementRepo;
import com.example.cw2.repos.StateRepo;

import org.greenrobot.eventbus.EventBus;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import dagger.hilt.EntryPoint;
import dagger.hilt.InstallIn;
import dagger.hilt.android.EntryPointAccessors;
import dagger.hilt.components.SingletonComponent;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.internal.schedulers.NewThreadScheduler;

public class MovementsRecylerViewAdapter extends RecyclerView.Adapter<MovementsRecylerViewAdapter.ViewHolder> {
    private final LayoutInflater inflater;
    private final Context context;
    private final List<MovementModel> data = new ArrayList<>();
    private final MovementsRecylerViewAdapter.RepoEntryPoint entryPoint;

    private final List<String> types;

    /**
     * Important for the individual elements to communicate with the repo, without referncing
     * any Model specifically.
     */
    @EntryPoint
    @InstallIn(SingletonComponent.class)
    interface RepoEntryPoint {
        public MovementRepo getMovementRepo();
        public StateRepo getStateRepo();
    }

    /**
     * @return A list of pre-specified types.
     */
    public String getType(int index) {
        return types.get(index);
    }

    private List<MovementModel> getData() {
        return data;
    }
    /**
     * Sets new data for the adapter.
     * @param data The new list of MovementModel data.
     */
    public void setData(List<MovementModel> data) {
        Log.d(LOG_TAG, "New movement data");
        if (data == null)
            return;
        getData().clear();
        getData().addAll(data);
        // Fine for small amounts of data
        notifyDataSetChanged();
    }


    /**
     * Constructs the adapter with the provided context and data.
     * @param context The context.
     * @param data    The list of MovementModel data.
     */
    public MovementsRecylerViewAdapter(Context context, List<MovementModel> data) {
        this.inflater = LayoutInflater.from(context);
        this.context = context;
        this.entryPoint = EntryPointAccessors.fromApplication(context.getApplicationContext()
                , MovementsRecylerViewAdapter.RepoEntryPoint.class);
        // Set types from the given string values.
        types =  Arrays.asList(getContext().getResources().getString(R.string.Walking), getContext().getResources().getString(R.string.Running), getContext().getResources().getString(R.string.Cycling));
        // Set initial data
        setData(data);
    }

    @Override
    public MovementsRecylerViewAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.movement_card, parent, false);
        return new MovementsRecylerViewAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MovementsRecylerViewAdapter.ViewHolder holder, int position) {
        holder.bind(data.get(position));
    }

    /**
     * @return A repo entry point giving the reminder repository from Hilt.
     */
    public MovementsRecylerViewAdapter.RepoEntryPoint getEntryPoint() {
        return entryPoint;
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public Context getContext() {
        return context;
    }

    // stores and recycles views as they are scrolled off screen
    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView name, type, date;
        MovementModel model;

        ViewHolder(View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.MovementName);
            type = itemView.findViewById(R.id.MovementType);
            date = itemView.findViewById(R.id.MovementDate);

           itemView.findViewById(R.id.MovementDelete).setOnClickListener((e) -> {
                Completable exec = getEntryPoint().getMovementRepo().deleteMovement(model.getMid());
                exec.subscribeOn(new NewThreadScheduler()).subscribe();
            });

           itemView.findViewById(R.id.MovementView).setOnClickListener((e)->{
               EventBus.getDefault().post(new Events.OpenMovementView());

               // Set global variables
               getEntryPoint().getStateRepo().setEditing(true);
               getEntryPoint().getStateRepo().setMId(model.getMid());

           });


        }

        /**
         * Binds the data to the ViewHolder.
         * @param model The MovementModel to bind.
         */
        void bind(final MovementModel model) {
            this.model = model;
            name.setText(String.format(getContext().getResources().getString(R.string.NameVal), model.getName()));
            type.setText(String.format(getContext().getResources().getString(R.string.TypeVal), getType(model.getType())));
            Date dateTime = new Date(model.getDate());
            DateFormat dateFormat = new SimpleDateFormat("EEEE dd/MM/yy", Locale.ENGLISH);
            date.setText(dateFormat.format(dateTime));
        }


    }
}