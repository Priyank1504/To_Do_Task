package com.example.priya.keepremember;
/**
 * Created by Priyank Verma on 5/17/2017.
 */

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    EditText email, password;
    Button signUp, login;
    FirebaseUser user;
    DatabaseReference myRef;
    User u;
    String userEmail, userPass;
    private ArrayList<User> userList=new ArrayList<>();
    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }
    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setTitle("To Do Tasks");
        email=(EditText)findViewById(R.id.editTextEmail);
        password=(EditText)findViewById(R.id.editTextPassword);
        signUp=(Button) findViewById(R.id.buttonSign);
        login=(Button) findViewById(R.id.buttonLogin);
        mAuth = FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                user = firebaseAuth.getCurrentUser();
                if (user != null) {
                myRef = FirebaseDatabase.getInstance().getReference();
                myRef.child("User").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        userList = new ArrayList<>();
                        Log.d("snapTrip", dataSnapshot.toString());
                        for (DataSnapshot d : dataSnapshot.getChildren()) {
                            u = d.getValue(User.class);
                            userList.add(u);
                        }
                        for(int i=0; i<userList.size(); i++) {
                            u=null;
                            u=userList.get(i);
                            if (user.getEmail().equals(u.getEmail())) {
                                Log.d("Fname", u.getFname());
                                Intent intExpList = new Intent(MainActivity.this, KeepNotesActivity.class);
                                intExpList.putExtra("ID", u.getFname()+ " "+ u.getLname());
                                intExpList.putExtra("IDONE", u.getUserid());
                                intExpList.putExtra("AUTH", user.getUid());
                                intExpList.putExtra("URI", u.getImgUri());
                                startActivity(intExpList);

                            }
                        }
                        Toast.makeText(MainActivity.this, "Sign in Done",
                                Toast.LENGTH_SHORT).show();
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            } else {
                // User is signed out
                Log.d("TAG", "onAuthStateChanged:signed_out");
            }

        }
    };
        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(MainActivity.this, SignUpActivity.class);
                startActivity(intent);
            }
        });
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userEmail = email.getText().toString();
                userPass = password.getText().toString();
                if (userEmail.length() > 0 && userPass.toString().length() > 0) {

                mAuth.signInWithEmailAndPassword(userEmail, userPass)
                        .addOnCompleteListener(MainActivity.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    Log.d("Demo", "signInWithEmail:onComplete:" + task.isSuccessful());
                                    Toast.makeText(MainActivity.this, "Sign in Done",
                                            Toast.LENGTH_SHORT).show();
                                    user = mAuth.getCurrentUser();
                                    myRef = FirebaseDatabase.getInstance().getReference();
                                    myRef.child("User").addValueEventListener(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot dataSnapshot) {
                                            userList = new ArrayList<>();
                                            Log.d("snapTrip", dataSnapshot.toString());
                                            for (DataSnapshot d : dataSnapshot.getChildren()) {
                                                u = d.getValue(User.class);
                                                userList.add(u);
                                            }
                                            for(int i=0; i<userList.size(); i++) {
                                                u=null;
                                                u=userList.get(i);
                                                if (user.getEmail().equals(u.getEmail())) {
                                                    Log.d("whatIS", u.getFname());
                                    Intent intExpList = new Intent(MainActivity.this, KeepNotesActivity.class);
                                    intExpList.putExtra("ID", u.getFname()+ " "+ u.getLname());
                                    intExpList.putExtra("IDONE", u.getUserid());
                                    intExpList.putExtra("AUTH", user.getUid());
                                    intExpList.putExtra("URI", u.getImgUri());
                                    startActivity(intExpList);
                                // If sign in fails, display a message to the user. If sign in succeeds
                                // the auth state listener will be notified and logic to handle the
                                // signed in user can be handled in the listener.
                                }
                            }
                        }
                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });
                        }
                                else {
                                    Log.w("Demo", "signInWithEmail:failed", task.getException());
                                    Toast.makeText(MainActivity.this, "Sign in Failed:" + task.getException().getMessage(),
                                            Toast.LENGTH_SHORT).show();
                                }

                            }
                        });
                }
                else {
                    Toast.makeText(MainActivity.this, "Enter all the Credentials", Toast.LENGTH_LONG).show();
                }
            }
        });
    }
}
