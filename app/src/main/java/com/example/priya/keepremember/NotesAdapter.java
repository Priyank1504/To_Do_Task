package com.example.priya.keepremember;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import org.ocpsoft.prettytime.PrettyTime;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Priyank Verma on 5/17/2017.
 */


public class NotesAdapter extends RecyclerView.Adapter<NotesAdapter.MyViewHolder> {
    private ArrayList<Note> list;
    PrettyTime pt;
    KeepNotesActivity activity;
    String Userid;
    DatabaseReference Ref=FirebaseDatabase.getInstance().getReference("Notes");
    DatabaseReference myRef;
    Map<String, Object> notesUpdates = new HashMap<String, Object>();
    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView subject, priority, time;
        public ImageView completed;
        public MyViewHolder(View view) {
            super(view);
            subject = (TextView) view.findViewById(R.id.textViewSubject);
            priority = (TextView) view.findViewById(R.id.textViewPriority);
            time = (TextView) view.findViewById(R.id.textViewTime);
            completed = (ImageView) view.findViewById(R.id.checkBoxCompleted);
        }
    }
    public NotesAdapter(String id, ArrayList<Note> list, KeepNotesActivity activity) {
        this.list = list;
        this.activity= activity;
        this.Userid=id;
    }
    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.notes_recycler_adapter, parent, false);

        return new MyViewHolder(itemView);
    }
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        final Note note = list.get(position);
        holder.subject.setText(note.getSubject());
        holder.priority.setText(note.getPriority());
        holder.time.setTag(new Integer(list.size()));
        SimpleDateFormat fmt = new SimpleDateFormat("EEE MMM dd HH:mm:ss z yyyy");
        Date date = null;
        try {
            Log.d("check", note.toString());
            date = fmt.parse(note.getUpdate_time());
        } catch (ParseException e) {
            date = new Date();
            e.printStackTrace();
        }
        pt = new PrettyTime();
        holder.time.setText(pt.format(date));
        if(note.getStatus().equals("Completed")){
            holder.completed.setImageResource(R.drawable.mark);
            holder.itemView.setBackgroundColor(Color.parseColor("#ADD8E6"));
        } else {
            holder.completed.setImageResource(R.drawable.unmark);
        }
        if(note.getPriority().equals("High")){
            holder.priority.setTextColor(Color.parseColor("#FFE4C4"));
        }
        if(note.getPriority().equals("Medium")){
            holder.priority.setTextColor(Color.parseColor("#FAEBD7"));
        }
        if(note.getPriority().equals("Low")){
            holder.priority.setTextColor(Color.parseColor("#F5F5DC"));
        }

        if(!holder.completed.isClickable() && note.getStatus().equals("pending")){
            holder.completed.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(holder.itemView.getContext());
                    builder.setTitle("Do you really want to mark it as complete?").setCancelable(false);
                    builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            note.setStatus("Completed");
                            note.setUpdate_time(new Date().toString());
                            myRef= Ref.child(Userid).child(note.getSubject());
                            notesUpdates.put("/status","Completed");
                            myRef.updateChildren(notesUpdates);
                            notifyDataSetChanged();
                            Toast.makeText(holder.itemView.getContext(), note.getSubject()+ " is marked as completed !", Toast.LENGTH_LONG).show();

                        }
                    }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Toast.makeText(holder.itemView.getContext(), note.getSubject()+" is still pending with priority " +note.getPriority() , Toast.LENGTH_LONG).show();
                        }
                    });
                    AlertDialog alert = builder.create();
                    alert.show();

                }
            });
        }
        holder.itemView.setLongClickable(true);
        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Toast.makeText(holder.itemView.getContext(),note.getSubject() + " is being removed", Toast.LENGTH_LONG).show();
                list.remove(note);
                notifyItemRemoved(position);
                notifyItemRangeChanged(position, list.size());
                myRef= Ref.child(Userid).child(note.getSubject());
                myRef.removeValue();
                return true;
            }
        });
    }
    @Override
    public int getItemCount() {
        return list.size();
    }
}