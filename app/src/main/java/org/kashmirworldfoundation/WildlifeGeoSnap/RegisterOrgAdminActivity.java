package org.kashmirworldfoundation.WildlifeGeoSnap;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class RegisterOrgAdminActivity extends AppCompatActivity {
    EditText mEmail, mPassword, mPassword2, mPhone, mJob,mName;
    Button mbRegisterA;
    FirebaseAuth fAuth2;
    FirebaseFirestore db2;
    FirebaseStorage St2;
    ArrayList<String> studies;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register__org__admin);
        mbRegisterA = findViewById(R.id.AdminRegisterB);
        mEmail = findViewById(R.id.AdminEmail);
        mPassword = findViewById(R.id.AdminPassword);
        mPassword2 = findViewById(R.id.AdminPassword2);
        mPhone = findViewById(R.id.AdminPhone);
        mJob = findViewById(R.id.AdminJob);
        mName =findViewById(R.id.AdminName);
        fAuth2 = FirebaseAuth.getInstance();
        mbRegisterA.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utils util = new Utils();


                if (util.getAgreement(RegisterOrgAdminActivity.this)){
                    LayoutInflater inflater= LayoutInflater.from(RegisterOrgAdminActivity.this);
                    View view=inflater.inflate(R.layout.disclaimer_layout, null);


                    AlertDialog.Builder alertDialog = new AlertDialog.Builder(RegisterOrgAdminActivity.this);
                    alertDialog.setTitle("Terms of Service");
                    alertDialog.setView(view);
                    alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Toast.makeText(RegisterOrgAdminActivity.this,"Agreement needed to register",Toast.LENGTH_LONG).show();
                        }

                    });

                    alertDialog.setPositiveButton("Agree", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            util.setAgreement(RegisterOrgAdminActivity.this);
                            registerAdmin();
                        }
                    });
                    AlertDialog alert = alertDialog.create();
                    alert.show();
                }
                else {
                    registerAdmin();
                }


            }
        });
    }
    public String capitalizeFirstLetter(String string) {
        string =string.toLowerCase();
        return string.substring(0,1).toUpperCase() + string.substring(1);
    }
    private void registerAdmin(){
        final String Aname = mName.getText().toString().trim();
        final String Aemail = mEmail.getText().toString().trim();
        final String Apassword = mPassword.getText().toString().trim();
        final String Apassword2 = mPassword2.getText().toString().trim();
        final String Ajob = mJob.getText().toString().trim();
        final String Aphone = mPhone.getText().toString().trim();
        St2 = FirebaseStorage.getInstance();
        db2  = FirebaseFirestore.getInstance();
        Intent intent = getIntent();
        final String Orgname =intent.getStringExtra("Orgname");
        //final String Region = intent.getStringExtra("Region");
        final String Country = intent.getStringExtra("Country");
        if(Aname.isEmpty()){
            emptytoast(getApplicationContext());
        }
        if(Aemail.isEmpty()){
            emptytoast(getApplicationContext());
        }
        Pattern pattern = Pattern.compile("^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$");
        Matcher matcher = pattern.matcher(Aemail);
        if (!matcher.matches()){
            mEmail.setError("Invalid Email.");
            return;
        }
        if(Apassword.isEmpty()){
            emptytoast(getApplicationContext());
        }
        if(Apassword.length() < 6){
            mPassword.setError("Password must be at least 6 characters.");
            return;
        }
        if(Apassword2.isEmpty()){
            emptytoast(getApplicationContext());
        }
        if (!Apassword.equals(Apassword2))
        {
            Toast.makeText(RegisterOrgAdminActivity.this, "Password do not match", Toast.LENGTH_SHORT).show();
            //What's the difference between Toast and setting an Error?
            //Apassword.setError("Password do not match."); //REASON: Apassword and mPassword... should it matter? will it cause a bug later?

            //return;
        }
        // Password length must be >= 6 characters

        if(Aphone.isEmpty()){
            emptytoast(getApplicationContext());
        }
        if(Ajob.isEmpty()){
            emptytoast(getApplicationContext());
        }

        //Orgname= capitalizeFirstLetter(Orgname);
        //Region = capitalizeFirstLetter(Region);
        //Country = capitalizeFirstLetter(Country);
        final Member memA =new Member();

        fAuth2.createUserWithEmailAndPassword(Aemail,Apassword).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()){
                    db2.collection("Organization").whereEqualTo("orgName",Orgname).whereEqualTo("orgCountry",Country).
                            get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()){
                                studies=new ArrayList<>();
                                studies.add("Pick a Study");
                                for (QueryDocumentSnapshot documentSnapshot: task.getResult()){
                                    Toast.makeText(RegisterOrgAdminActivity.this,"Org Found", Toast.LENGTH_SHORT).show();
                                    memA.setOrg(documentSnapshot.getReference().getPath());

                                }
                                memA.setJob(Ajob);
                                memA.setFullname(Aname);
                                memA.setPhone(Aphone);
                                memA.setAdmin(Boolean.TRUE);
                                memA.setEmail(Aemail);
                                memA.setProfile("profile/kwflogo.jpg");
                                FirebaseUser user= FirebaseAuth.getInstance().getCurrentUser();
                                db2.collection("Member").document(user.getUid()).set(memA).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if(task.isSuccessful()){
                                            saveMember(memA,user.getUid());
                                            Toast.makeText(RegisterOrgAdminActivity.this,"User Created", Toast.LENGTH_SHORT).show();
                                            saveAdmin();

                                            studies.set(0, "No Studies");
                                            saveStudies(studies);
                                            startActivity(new Intent(getApplicationContext(),MainActivity.class));

                                        }
                                    }
                                });
                            }

                        }
                    });
                }
                else {
                    Toast.makeText(RegisterOrgAdminActivity.this, "onComplete: Failed=" + task.getException().getMessage(), Toast.LENGTH_LONG ).show();
                }
            }
        });
    }
    private void emptytoast(Context cont){
        Toast.makeText(cont,"Fields are empty",Toast.LENGTH_LONG).show();
    }
    private void saveMember (Member mem,String uid){
        SharedPreferences sharedPreferences = RegisterOrgAdminActivity.this.getSharedPreferences("user", Context.MODE_PRIVATE);

        SharedPreferences.Editor editor = sharedPreferences.edit();
        Gson gson = new Gson();
        String json =gson.toJson(mem);
        editor.putString("user",json);
        editor.putString("uid",uid);
        editor.apply();
    }
    private void saveCamNum(){
        SharedPreferences sharedPreferences = RegisterOrgAdminActivity.this.getSharedPreferences("camstations",Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt("CamNum",0);
        editor.apply();
    }
    private void saveStudies(ArrayList<String> studies){
        SharedPreferences sharedPreferences = RegisterOrgAdminActivity.this.getSharedPreferences("user",Context.MODE_PRIVATE);
        SharedPreferences.Editor editor =sharedPreferences.edit();


        Gson gson = new Gson();
        String json =gson.toJson(studies);
        editor.putString("studies",json);
        editor.apply();
    }
    private void saveAdmin(){
        SharedPreferences sharedPreferences = RegisterOrgAdminActivity.this.getSharedPreferences("Admin", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor =sharedPreferences.edit();


        Gson gson = new Gson();

        editor.putBoolean("Admin",false);
        editor.apply();
    }
}
