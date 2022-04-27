package org.kashmirworldfoundation.WildlifeGeoSnap;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.Calendar;
import java.util.Date;

public class AddWildlifeSightingActivity extends AppCompatActivity {
    private EditText SightingInput;
    private EditText LongitudeInput;
    private EditText LatitudeInput;
    private EditText NoteInput;
    private ImageView img;
    private Button Post;
    private TextView Back;
    private FirebaseFirestore db;
    private FirebaseStorage fStorage;
    private float[] latlong=new float[2];
    Uri uri;
    Boolean pic=true;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_wildlifesighting);
        SightingInput =findViewById(R.id.SightingTitleInput);
        LongitudeInput=findViewById(R.id.SightingLongitudeInput);
        LatitudeInput =findViewById(R.id.SightingLatitudeInput);
        NoteInput=findViewById(R.id.SightingNoteInput);
        img=findViewById(R.id.PreyImg);
        Post=findViewById(R.id.SightingAddBtn);
        Back=findViewById(R.id.SightingBack);
        db=FirebaseFirestore.getInstance();
        fStorage= FirebaseStorage.getInstance();
        begin();
        uri=null;
        Back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
            }
        });
        //remember to check empty
        Post.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                WildlifeSighting wildlifeSighting = new WildlifeSighting();
                CollectionReference collection =db.collection("WildlifeSighting");
                DocumentReference doc = collection.document();
                String path=doc.getId();
                Date currentTime = Calendar.getInstance().getTime();
                postpic(path);
                wildlifeSighting.setSighting(SightingInput.getText().toString());
                wildlifeSighting.setLatitudeS(LatitudeInput.getText().toString());
                wildlifeSighting.setLongitudeS(LongitudeInput.getText().toString());
                wildlifeSighting.setNote(NoteInput.getText().toString());
                wildlifeSighting.setPosted(new Timestamp(currentTime));
                wildlifeSighting.setPic(uri.toString());

                Utils utils= new Utils();
                Member member=utils.loaduser(getApplicationContext());
                wildlifeSighting.setMember("Member/"+utils.loadUid(getApplicationContext()));

                wildlifeSighting.setOrg(member.getOrg());
                wildlifeSighting.setAuthor(member.getFullname());
                utils.saveSighting(wildlifeSighting,getApplicationContext());
                startActivity(new Intent(getApplicationContext(), MainActivity.class));

            }
        });
        img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openGallery(0);
            }
        });


    }
    private void openGallery(int PICK_IMAGE){
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE);
    }
    @RequiresApi(api = Build.VERSION_CODES.Q)
    @Override
    public void onActivityResult(int requestcode,int resultcode, @Nullable Intent data){
        super.onActivityResult(requestcode,resultcode,data);

        if (resultcode== Activity.RESULT_OK ){
            uri =data.getData();
            //img.setImageURI(uri);

            img.setDrawingCacheEnabled(true);
            img.buildDrawingCache();
            try {
                ExifInterface exif=new ExifInterface(new File(uri.getPath()));

                exif.getLatLong(latlong);
            } catch (IOException e) {
                e.printStackTrace();
            }
            String temp;
            if(latlong[0]!=0){
                temp=String.valueOf(latlong[0]);
                LatitudeInput.setText(temp);
            }
            if(latlong[1]!=0){
                temp=String.valueOf(latlong[1]);
                LongitudeInput.setText(temp);
            }
            temp=String.valueOf(latlong[0]);
            LatitudeInput.setText(temp);
            temp=String.valueOf(latlong[1]);
            LongitudeInput.setText(temp);
            end();


        }


    }
    public void postpic(String path){
        try {

            Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(),uri);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            byte[] datan = baos.toByteArray();
            StorageReference profile = fStorage.getReference(path + "/image");
            UploadTask uploadTask = profile.putBytes(datan);
            uploadTask.addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                    if (task.isSuccessful()) {

                    }

                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    private void begin(){
        SightingInput.setVisibility(View.GONE);
        LongitudeInput.setVisibility(View.GONE);
        LatitudeInput.setVisibility(View.GONE);
        LongitudeInput.setText("");
        LatitudeInput.setText("");
        NoteInput.setVisibility(View.GONE);
        Post.setVisibility(View.GONE);
        img.setVisibility(View.VISIBLE);
        findViewById(R.id.SightingTitleLabel).setVisibility(View.GONE);
        findViewById(R.id.SightingLatitudeLabel).setVisibility(View.GONE);
        findViewById(R.id.PreyLongitudeLabel).setVisibility(View.GONE);
        findViewById(R.id.PreyNoteLabel).setVisibility(View.GONE);
        pic=true;
        latlong[0]=0;
        latlong[1]=0;

    }
    private void end(){
        SightingInput.setVisibility(View.VISIBLE);
        LongitudeInput.setVisibility(View.VISIBLE);
        LatitudeInput.setVisibility(View.VISIBLE);
        NoteInput.setVisibility(View.VISIBLE);
        Post.setVisibility(View.VISIBLE);
        img.setVisibility(View.GONE);
        findViewById(R.id.SightingTitleLabel).setVisibility(View.VISIBLE);
        findViewById(R.id.SightingLatitudeLabel).setVisibility(View.VISIBLE);
        findViewById(R.id.PreyLongitudeLabel).setVisibility(View.VISIBLE);
        findViewById(R.id.PreyNoteLabel).setVisibility(View.VISIBLE);
        pic=false;
        String Slat= LatitudeInput.getText().toString();
        String Slong=LongitudeInput.getText().toString();

        if(Slat=="0"&& Slong=="0"){
            Toast.makeText(getApplicationContext(),"No GPS location in image",Toast.LENGTH_LONG);
        }

    }
    @Override
    public void onBackPressed() {
      if (pic){
          super.onBackPressed();
      }
      else{
          begin();
      }
    }

}
