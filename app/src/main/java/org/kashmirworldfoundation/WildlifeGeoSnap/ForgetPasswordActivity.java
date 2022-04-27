package org.kashmirworldfoundation.WildlifeGeoSnap;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

public class ForgetPasswordActivity extends AppCompatActivity {
    TextView Email, Back;
    Button Submit;
    FirebaseAuth fAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget_password);
        Email=findViewById(R.id.EmailRecoveryInput);
        Back=findViewById(R.id.ForgetPassBack);
        Submit=findViewById(R.id.RecoverySubmit);
        fAuth=FirebaseAuth.getInstance();
        Submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email=Email.getText().toString().trim();
                if (!email.isEmpty()) {
                    fAuth.sendPasswordResetEmail(Email.getText().toString().trim());
                    Toast.makeText(getApplicationContext(), "Email sent to" + email + " reset Password", Toast.LENGTH_LONG).show();
                }
                else {
                    Toast.makeText(ForgetPasswordActivity.this, "Please enter an email", Toast.LENGTH_LONG ).show();
                }
            }
        });
        Back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                startActivity(new Intent(getApplicationContext(), LoginActivity.class));
            }
        });

    }
}