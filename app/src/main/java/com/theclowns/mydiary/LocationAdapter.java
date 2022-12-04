package com.theclowns.mydiary;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.util.Vector;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;

public class LocationAdapter extends RecyclerView.Adapter<LocationAdapter.ViewHolder>
{
    private Context context;
    private Vector<Location> locationVector;
    private ClickListener clickListener;

    class ViewHolder extends RecyclerView.ViewHolder
    {
        TextView textViewLocationName;
        Button buttonDelete;

        public ViewHolder(@NonNull View itemView)
        {
            super(itemView);
            textViewLocationName = itemView.findViewById(R.id.text_view_location);
            buttonDelete = itemView.findViewById(R.id.button_delete);

            itemView.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View view)
                {
                    clickListener.onItemClick(locationVector.get(getBindingAdapterPosition()).getLocationId());
                }
            });
        }
    }

    public LocationAdapter(Context context)
    {
        this.context = context;
    }

    public void setLocationVector(Vector<Location> locationVector)
    {
        this.locationVector = locationVector;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        View v = LayoutInflater.from(context).inflate(R.layout.item_location, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position)
    {
        Location item = locationVector.get(position);
        holder.textViewLocationName.setText(item.getLocationName());

        holder.buttonDelete.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                clickListener.onItemDeletedClick(item.getLocationId());
                Toast.makeText(context, "Location removed.", Toast.LENGTH_LONG).show();
                locationVector.remove(position);
                notifyItemRemoved(position);
                notifyItemRangeChanged(position, locationVector.size());
            }
        });
    }

    @Override
    public int getItemCount()
    {
        return locationVector.size();
    }

    public void setOnItemClickListener(ClickListener clickListener)
    {
        this.clickListener = clickListener;
    }

    public interface ClickListener
    {
        void onItemClick(String locationId);

        void onItemDeletedClick(String locationId);
    }
}
