package com.example.priya.keepremember;
/**
 * Created by Priyank Verma on 5/17/2017.
 */

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;

import static android.support.v7.recyclerview.R.attr.layoutManager;

public class KeepNotesActivity extends AppCompatActivity {

    Button add;
    EditText note;
    TextView  name;
    ImageView img;
    String noteText, id, authId;
    Note n;
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    ProgressDialog mProgressDialog;
    Uri imgUri;
    private ArrayList<Note> noteList=new ArrayList<>();
    DatabaseReference myRef, getRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_keep_notes);
        setTitle("To Do Tasks");
        add=(Button) findViewById(R.id.buttonAdd);
        note=(EditText) findViewById(R.id.editTextNote);
        name=(TextView) findViewById(R.id.textView7);
        note=(EditText) findViewById(R.id.editTextNote);
        img=(ImageView)findViewById(R.id.imageViewU);
        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setMessage("Loading...");
        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        mProgressDialog.setCancelable(false);
        mProgressDialog.show();
        mRecyclerView = (RecyclerView) findViewById(R.id.RecyclerView);
        Intent i = getIntent();
        final String userName = i.getStringExtra("ID");
        id=i.getStringExtra("IDONE");
        authId=i.getStringExtra("AUTH");
        imgUri= Uri.parse(i.getStringExtra("URI"));
        name.setText(userName);
        name.setTextColor(Color.BLACK);
        Log.d("autID", authId.toString());
        Log.d("image", imgUri.toString());
        Picasso.with(KeepNotesActivity.this)
                .load(imgUri)
                .resize(600,600 ) // resizes the image to these dimensions (in pixel)
                .centerCrop()
                .into(img);
        ArrayAdapter<CharSequence> genereSpinner = ArrayAdapter.createFromResource(KeepNotesActivity.this, R.array.priorities, android.R.layout.simple_spinner_item);
        genereSpinner.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        final Spinner sp = (Spinner) findViewById(R.id.spinnerPriority);
        sp.setAdapter(genereSpinner);
        sp.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                noteText=note.getText().toString();
                if(noteText.equals("")){
                    Toast.makeText(KeepNotesActivity.this, "Please Write something!", Toast.LENGTH_SHORT).show();
                    return;
                }
                Date date = new Date(); //Sun Mar 19 14:21:25 EDT 2017
                SimpleDateFormat fmtOut = new SimpleDateFormat("EEE MMM dd HH:mm:ss z yyyy");
                SimpleDateFormat fmt = new SimpleDateFormat("EEE MMM dd HH:mm:ss z yyyy");
                Log.d("FMTFOUT Before", fmtOut.format(date));
                String dateString = fmtOut.format(date);
                try {
                    date = fmt.parse(dateString);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                Log.d("FMTFOUT", fmtOut.format(date));
                Log.d("DATE", new Date().toString());
                Log.d("Sub", noteText);
                n=new Note();
                n.subject=note.getText().toString();
                n.priority=sp.getSelectedItem().toString();
                n.update_time=new Date().toString();
                n.status="pending";
                n.id=id;
                myRef= FirebaseDatabase.getInstance().getReference("Notes");
                DatabaseReference mUserRef = myRef.child(id).child(noteText);
                DatabaseReference userreference = mUserRef.push();
                mUserRef.setValue(n);
                note.setText("");
            }
        });
        getRef = FirebaseDatabase.getInstance().getReference();
        getRef.child("Notes").child(id).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                noteList = new ArrayList<>();
                Log.d("snapTrip", dataSnapshot.toString());
                for (DataSnapshot d : dataSnapshot.getChildren()) {
                    n = d.getValue(Note.class);
                    noteList.add(n);
                }
                Log.d("List", noteList.toString());
                if(noteList.size()>0) {
                    display();
                }
                else{
                    mProgressDialog.dismiss();
                    Toast.makeText(KeepNotesActivity.this, "You can now start creating your Todo list" , Toast.LENGTH_LONG).show();
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle presses on the action bar items
        switch (item.getItemId()) {
            case R.id.logout:
                FirebaseAuth.getInstance().signOut();
                Toast.makeText(KeepNotesActivity.this, "Click one more time to Sign Out!", Toast.LENGTH_LONG).show();
                finish();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void display() {
            mAdapter = new NotesAdapter(id, noteList, KeepNotesActivity.this);
            RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
            mRecyclerView.setLayoutManager(mLayoutManager);
            mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        //add ItemDecoration
        DividerItemDecoration verticalDecoration = new DividerItemDecoration(mRecyclerView.getContext(),
                DividerItemDecoration.VERTICAL);
        Drawable verticalDivider = ContextCompat.getDrawable(this, R.drawable.vertical_divider);
        verticalDecoration.setDrawable(verticalDivider);
        mRecyclerView.addItemDecoration(verticalDecoration);
        mRecyclerView.setAdapter(mAdapter);
        mProgressDialog.dismiss();
        mAdapter.notifyDataSetChanged();
    }
}
