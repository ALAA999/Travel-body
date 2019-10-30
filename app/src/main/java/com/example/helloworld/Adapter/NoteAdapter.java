package com.example.helloworld.Adapter;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.helloworld.DataBase.AppDatabase;
import com.example.helloworld.Model.Note;
import com.example.helloworld.R;

import java.util.List;

public class NoteAdapter extends RecyclerView.Adapter<NoteAdapter.MyViewHolder> {
    private Context context;
    private List<Note> mNoteList;

    public NoteAdapter(Context context) {
        this.context = context;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(context).inflate(R.layout.note_item, viewGroup, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NoteAdapter.MyViewHolder myViewHolder, int i) {
        myViewHolder.tvTitle.setText(mNoteList.get(i).getTitle());
        myViewHolder.tvDescription.setText(mNoteList.get(i).getDescription());
        myViewHolder.tvLatLng.setText("" + mNoteList.get(i).getLat() + ", " + mNoteList.get(i).getLng());
        myViewHolder.tvTime.setText(mNoteList.get(i).getTime());
    }

    @Override
    public int getItemCount() {
        if (mNoteList == null) {
            return 0;
        }
        return mNoteList.size();

    }

    public void setTasks(List<Note> noteList) {
        mNoteList = noteList;
        notifyDataSetChanged();
    }

    public List<Note> getTasks() {

        return mNoteList;
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle, tvDescription, tvLatLng, tvTime;
        ImageView image;
        AppDatabase mDb;

        MyViewHolder(@NonNull final View itemView) {
            super(itemView);
            mDb = AppDatabase.getInstance(context);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvDescription = itemView.findViewById(R.id.tvDescription);
            tvLatLng = itemView.findViewById(R.id.tvLatLng);
            tvTime = itemView.findViewById(R.id.tvTime);
            image = itemView.findViewById(R.id.image);
        }
    }
}