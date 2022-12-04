package com.theclowns.mydiary;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.Vector;

public class DiaryAdapter extends RecyclerView.Adapter<DiaryAdapter.ViewHolder>
{
    private Context context;
    private Vector<DiaryHeader> diaryHeaderVector;
    private ClickListener clickListener;

    public void setDiaryHeaderVector(Vector<DiaryHeader> diaryHeaderVector)
    {
        this.diaryHeaderVector = diaryHeaderVector;
        notifyDataSetChanged();
    }

    class ViewHolder extends RecyclerView.ViewHolder
    {
        TextView textViewTitle;
        TextView textViewDate;
        Button buttonDelete;

        public ViewHolder(@NonNull View itemView)
        {
            super(itemView);
            textViewTitle = itemView.findViewById(R.id.text_view_title);
            textViewDate = itemView.findViewById(R.id.text_view_date);
            buttonDelete = itemView.findViewById(R.id.button_delete);

            itemView.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View view)
                {
                    clickListener.onItemClick(diaryHeaderVector.get(getBindingAdapterPosition()).getDiaryId());
                }
            });
        }
    }

    public DiaryAdapter(Context context)
    {
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        View v = LayoutInflater.from(context).inflate(R.layout.item_diary, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull DiaryAdapter.ViewHolder holder, int position)
    {
        DiaryHeader item = diaryHeaderVector.get(position);
        holder.textViewTitle.setText(item.getDiaryTitle());
        holder.textViewDate.setText(item.getDiaryDate());

        holder.buttonDelete.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                clickListener.onItemDeletedClick(item.getDiaryId());
                Toast.makeText(context, "Diary removed.", Toast.LENGTH_LONG).show();
                diaryHeaderVector.remove(position);
                notifyItemRemoved(position);
                notifyItemRangeChanged(position, diaryHeaderVector.size());
            }
        });
    }

    @Override
    public int getItemCount()
    {
        return diaryHeaderVector.size();
    }

    public void setOnItemClickListener(DiaryAdapter.ClickListener clickListener)
    {
        this.clickListener = clickListener;
    }

    public interface ClickListener
    {
        void onItemClick(int diaryId);

        void onItemDeletedClick(int diaryId);
    }
}
