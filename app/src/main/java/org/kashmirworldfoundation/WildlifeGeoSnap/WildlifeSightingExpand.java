package org.kashmirworldfoundation.WildlifeGeoSnap;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.text.SimpleDateFormat;

public class WildlifeSightingExpand extends AppCompatActivity {
    private TextView Author,Longitude,Lattitude,Notes,Date, WildlifeSighting,Back;

    private ImageView img;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wildlifesighting_expand);
        Author= findViewById(R.id.EXSightingAuthorText);
        Lattitude=findViewById(R.id.EXSightingLatitudeText);
        Longitude=findViewById(R.id.EXSightingLongitudeText);
        Notes=findViewById(R.id.EXSightingNotesText);
        WildlifeSighting =findViewById(R.id.EXSightingText);
        img=findViewById(R.id.SightingExpandImage);
        Back= findViewById(R.id.SightingExpandBack);
        Date=findViewById(R.id.EXSightingDateText);
        Back= findViewById(R.id.SightingExpandBack);
        WildlifeSighting wildlifeSighting = getIntent().getParcelableExtra("WildlifeSighting");
        Lattitude.setText(wildlifeSighting.getLatitudeS());
        Longitude.setText(wildlifeSighting.getLongitudeS());
        Notes.setText(wildlifeSighting.getNote());
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        String format = formatter.format(wildlifeSighting.getPosted().toDate());
        Date.setText(format);
        WildlifeSighting.setText(wildlifeSighting.getSighting());
        Author.setText(wildlifeSighting.getAuthor());
        Back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),MainActivity.class));
            }
        });
        StorageReference ref = FirebaseStorage.getInstance().getReference(wildlifeSighting.getPic());
        GlideApp.with(this)
                .load(ref)
                .into(img);


    }

}