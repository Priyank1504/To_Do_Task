package com.example.priya.keepremember;
/**
 * Created by Priyank Verma on 5/17/2017.
 */

import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class SignUpActivity extends AppCompatActivity {
    EditText fName, lName, pass, rePass, emailID;
    Button register, cancel;
    ImageView userImg;
    Uri selectedImageURI;
    Spinner gender;
    private Uri imgLink;
    User uUser;
    FirebaseUser user;
    String Upassword, Urepassword, Uname,Uemail, Ugender, Uimg;;
    String genderSelect;
    FirebaseStorage mStorage= FirebaseStorage.getInstance();
    private StorageReference storageRef;
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private FirebaseAuth.AuthStateListener mAuthListner;
    DatabaseReference mRootRef;
    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListner);
    }
    protected void onStop() {
        super.onStop();
        if (mAuthListner != null) {
            mAuth.removeAuthStateListener(mAuthListner);
        }

    }
    @Override
    protected void onActivityResult ( int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == 100) {
                selectedImageURI = data.getData();
                Picasso.with(SignUpActivity.this).load(selectedImageURI).noPlaceholder().centerCrop().fit()
                        .into(userImg);
                storageRef = mStorage.getReferenceFromUrl("gs://keeptoremember-a90a4.appspot.com/");
                StorageReference imagesRef = storageRef.child("images/"+selectedImageURI.getLastPathSegment());
                UploadTask uploadTask = imagesRef.putFile(selectedImageURI);

                uploadTask.addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        // Handle unsuccessful uploads
                    }
                }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @SuppressWarnings("VisibleForTests")
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        // taskSnapshot.getMetadata() contains file metadata such as size, content-type, and download URL.
                        imgLink = taskSnapshot.getDownloadUrl();
                        Log.d("Link:",imgLink.toString());
                        register.setEnabled(true);
                    }
                });
            }
        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        setTitle("Sign Up");
        fName=(EditText)findViewById(R.id.editTextFname);
        lName=(EditText)findViewById(R.id.editTextLname);
        pass=(EditText)findViewById(R.id.editTextPass);
        rePass=(EditText)findViewById(R.id.editTextRePass);
        emailID=(EditText)findViewById(R.id.editTextEmailId);
        register=(Button)findViewById(R.id.buttonRegister);
        register.setEnabled(false);
        cancel=(Button)findViewById(R.id.buttonCancel);
        userImg=(ImageView) findViewById(R.id.imageViewUser);
        userImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_OPEN_DOCUMENT );
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), 100);
            }
        });
        mAuthListner = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {

                user = firebaseAuth.getCurrentUser();
                if (user != null) {//user is signed in
                    Log.d("Uid", "onAuthStateChanged:signed_in:" + user.getUid());
                } else {
                    Log.d("demo", "User is signed out");
                }
            }
        };
        gender = (Spinner) findViewById(R.id.spinner);
        List<String> categories = new ArrayList<String>();
        categories.add("Male");
        categories.add("Female");
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(SignUpActivity.this, android.R.layout.simple_spinner_item, categories);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        gender.setAdapter(dataAdapter);
        gender.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                genderSelect = parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                Toast.makeText(parent.getContext(), "Please Select the Gender!", Toast.LENGTH_LONG).show();
            }
        });
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        Upassword=pass.getText().toString();
        Urepassword=rePass.getText().toString();
        Log.d("Check",Upassword + "" +Urepassword);

            register.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Uname = fName.getText().toString() + " " + lName.getText().toString();
                    Uemail = emailID.getText().toString();
                    Ugender = genderSelect;
                    Upassword = pass.getText().toString();
                    Urepassword = rePass.toString();
                    Uimg = selectedImageURI.toString();
                    if(Uname.length()>0 && Uemail.length()>0 && Upassword.length()>0 &&Urepassword.length()>0 && fName.getText().length()>0 && lName.getText().length()>0) {
                        uUser = new User();
                        uUser.fname = fName.getText().toString();
                        uUser.lname = lName.getText().toString();
                        uUser.email = emailID.getText().toString();
                        uUser.password = pass.getText().toString();
                        uUser.rePassword = rePass.getText().toString();
                        uUser.imgUri = imgLink.toString();
                        uUser.gender = genderSelect;
                        mAuth.createUserWithEmailAndPassword(Uemail, Upassword).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {


                                // If sign in fails, display a message to the user. If sign in succeeds
                                // the auth state listener will be notified and logic to handle the
                                // signed in user can be handled in the listener.
                                if (!task.isSuccessful()) {
                                    Toast.makeText(SignUpActivity.this, "Error: " + task.getException(), Toast.LENGTH_LONG).show();

                                } else {
                                    Log.d("demo", "createUserWithEmail:onComplete:" + task.isSuccessful());
                                    mRootRef = FirebaseDatabase.getInstance().getReference("User");
                                    DatabaseReference mUserRef = mRootRef.child(user.getUid());
                                    DatabaseReference userreference = mUserRef.push();
                                    uUser.userid = userreference.getKey();
                                    Log.d("UID:", uUser.userid);
                                    mUserRef.setValue(uUser);
                                    Toast.makeText(SignUpActivity.this, "Successfully Created User", Toast.LENGTH_LONG).show();
                                    finish();
                                }

                            }
                        });
                    }
                    else
                    {
                        Toast.makeText(SignUpActivity.this,"All fields must be filled correctly!",Toast.LENGTH_LONG).show();
                    }

                }
            });
        }
    }

