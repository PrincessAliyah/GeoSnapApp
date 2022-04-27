package org.kashmirworldfoundation.WildlifeGeoSnap;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import com.google.firebase.auth.FirebaseAuth;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.gson.Gson;

import org.jetbrains.annotations.NotNull;

import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class RegisterOrgActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener{
    EditText mEmail, mOrgname, mPhone, mOrgWebsite;
    Button mbRegister;
    TextView mbLogin;
    FirebaseAuth fAuth;
    FirebaseFirestore db;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_org);
        mOrgname = findViewById(R.id.OrgName);
        mPhone = findViewById(R.id.OrgPhone);
        mOrgWebsite = findViewById(R.id.orgwebsite);
        mEmail = findViewById(R.id.OrgEmail);

        mbLogin = findViewById(R.id.logindr);
        mbRegister = findViewById(R.id.RegisterB);
        // mRegion = findViewById(R.id.Region);
        fAuth = FirebaseAuth.getInstance();

        final Spinner spinner = findViewById(R.id.countries);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource
                (this,R.array.Countries,android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(this);
        mbLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), LoginActivity.class));

            }
        });

        mbRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String orgName = mOrgname.getText().toString().trim();
                String email = mEmail.getText().toString().trim();
                String website = mOrgWebsite.getText().toString().trim();
                String phone = mPhone.getText().toString().trim();
                String country =spinner.getSelectedItem().toString().trim();
                //String region = mRegion.getText().toString().trim();
                if(TextUtils.isEmpty(orgName)){
                    mOrgname.setError("Orgname Required");
                    return;
                }

                if (TextUtils.isEmpty(email)){
                    mEmail.setError("Email Required.");
                    return;
                }
                Pattern pattern = Pattern.compile("^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$");
                Matcher matcher = pattern.matcher(email);
                if (!matcher.matches()){
                    mEmail.setError("Invalid Email.");
                    return;
                }

                if (TextUtils.isEmpty(website)){
                    mOrgWebsite.setError(("Website Required"));
                    return;
                }
                if (TextUtils.isEmpty(phone)){
                    mOrgWebsite.setError(("Phone Number Required"));
                    return;
                }

                if (country.equals("Country")){
                    Toast.makeText(RegisterOrgActivity.this, "Need to select a country", Toast.LENGTH_SHORT).show();
                    return;
                }
/*                if (TextUtils.isEmpty(region)){
                    mRegion.setError("Region Required");
                    return;
                }*/

                db=FirebaseFirestore.getInstance();

                final Org morg = new Org();
                morg.setOrgCountry(country);
                //morg.setOrgRegion(region);
                morg.setOrgName(orgName);
                morg.setOrgWebsite(website);
                morg.setOrgPhone(phone);
                morg.setOrgEmail(email);

                final Intent i = new Intent(getApplicationContext(), RegisterOrgAdminActivity.class);
//Create the bundle
                Bundle bundle = new Bundle();

//Add your data to bundle
                bundle.putString("Orgname", orgName);
                bundle.putString("Country", country);
                //bundle.putString("Region", region);

//Add the bundle to the intent
                i.putExtras(bundle);

// Check if orgname has already been registered(exists in FireBase database already)
/*                DatabaseReference rootRef=FirebaseDatabase.getInstance().getReference();
                Query query = rootRef.child("Organization").orderByChild("orgName").equalTo(orgName);

                query.addValueEventListener(new ValueEventListener(){
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot){
                        if(dataSnapshot.exists()) {
                            //username exist
                            Toast.makeText(RegisterOrgActivity.this,"This organization already registered",Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(RegisterOrgActivity.this,"Looks good",Toast.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull @NotNull DatabaseError error) {

                    }
                });*/


                db.collection("Organization").whereEqualTo("orgName",orgName).whereEqualTo("orgCountry",country).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()){
                                if (task.getResult().isEmpty()){

                                    db.collection("Organization").add(morg).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                                        @Override
                                        public void onComplete(@NonNull Task<DocumentReference> task) {

                                            if (task.isSuccessful()) {
                                                Toast.makeText(RegisterOrgActivity.this,"Organization Successfully Created",Toast.LENGTH_LONG).show();
                                                Log.e("Tag", "Success 1");
                                                saveAdmin();
                                                // sendMessage(Orgname,phone,website,country);
                                                // sendMessage(Orgname,phone,website,country,region);

                                                startActivity(i);
                                            }
                                            else{
                                                Toast.makeText(RegisterOrgActivity.this,"Error submitting Organization",Toast.LENGTH_LONG).show();
                                                Log.e("Tag", "Fail 1");
                                                recreate();
                                            }
                                        }
                                    });
                                }
                                else{
                                    Toast.makeText(RegisterOrgActivity.this,"This organization has already been registered.",Toast.LENGTH_LONG).show();
                                    Log.e("Tag","Fail2");
                                    recreate();
                                }
                        }
                        else{
                            Toast.makeText(RegisterOrgActivity.this,"Error checking for duplicate Organizations",Toast.LENGTH_LONG).show();
                        }
                    }
                });
            //Fire that second activity

            }
        });
    }
    public String capitalizeFirstLetter(String string) {
        string =string.toLowerCase();
        return string.substring(0,1).toUpperCase() + string.substring(1);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        String text = parent.getItemAtPosition(position).toString();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
    private void sendMessage(final String orgname, final String phone, final String website, final String country) {
        final ProgressDialog dialog = new ProgressDialog(RegisterOrgActivity.this);
        dialog.setTitle("Sending Email");
        dialog.setMessage("Please wait");
        dialog.show();
        Thread sender = new Thread(new Runnable() {
            String Body="Orgname = " +orgname +" \n phone = " + phone+ "\n website =" + website + "\nCountry = " + country;
            String subject = orgname + " wants to register under KWF app ";

            public void run() {
                try {
                    //"aliyah@kashmirworldfoundation.org"
                    GMailSender sender = new GMailSender("adm1nkwf1675@gmail.com", "Chowder1675!");
                    sender.sendMail(subject,
                            Body,
                            "adm1nkwf1675@gmail.com","aliyah@kashmirworldfoundation.org"
                            );
                    dialog.dismiss();
                } catch (Exception e) {
                    Log.e("mylog", "Error: " + e.getMessage());
                }
            }
        });
        sender.start();
    }

    private void saveAdmin(){
        SharedPreferences sharedPreferences = RegisterOrgActivity.this.getSharedPreferences("Admin", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor =sharedPreferences.edit();

        Gson gson = new Gson();

        editor.putBoolean("Admin",true);
        editor.apply();
    }
}

