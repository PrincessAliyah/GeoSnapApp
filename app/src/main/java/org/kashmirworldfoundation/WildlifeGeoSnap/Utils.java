package org.kashmirworldfoundation.WildlifeGeoSnap;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.common.reflect.TypeToken;
import com.google.firebase.Timestamp;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.lang.reflect.Type;
import java.util.ArrayList;


public class Utils {
    public Member loaduser(Context context){
        SharedPreferences sharedPreferences = context.getSharedPreferences("user", Context.MODE_PRIVATE);
        Gson gson= new Gson();
        String json = sharedPreferences.getString("user", null);
        Type type =new TypeToken<Member>(){}.getType();
        return gson.fromJson(json,type);
    }
    public String loadUid(Context context){
        SharedPreferences sharedPreferences = context.getSharedPreferences("user", Context.MODE_PRIVATE);

        return sharedPreferences.getString("uid", null);


    }
    public boolean getAgreement(Context context){
        SharedPreferences sharedPreferences = context.getSharedPreferences("TOS", Context.MODE_PRIVATE);
        return sharedPreferences.getBoolean("tos", true);

    }
    public void setAgreement(Context context){
        SharedPreferences sharedPreferences = context.getSharedPreferences("TOS", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor= sharedPreferences.edit();
        editor.putBoolean("tos",false);
        editor.apply();
    }
    private String loaduid(Context context){
        SharedPreferences sharedPreferences = context.getSharedPreferences("user",Context.MODE_PRIVATE);
        return sharedPreferences.getString("uid",null);
    }
    public void saveSighting(WildlifeSighting wildlifeSighting, Context context){
        SharedPreferences sharedPreferences = context.getSharedPreferences("user",Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        ArrayList<WildlifeSighting> list= getSighting(context);
        list.add(wildlifeSighting);
        Gson gson = new GsonBuilder().registerTypeAdapter(Timestamp.class,new MyDateTypeAdapter()).create();
        String json =gson.toJson(list);

        editor.putString("wildlifeSighting",json);
        editor.apply();

    }
    public ArrayList<WildlifeSighting> getSighting(Context context){
        SharedPreferences sharedPreferences = context.getSharedPreferences("user",Context.MODE_PRIVATE);
        Gson gson = new GsonBuilder().registerTypeAdapter(Timestamp.class,new MyDateTypeAdapter()).create();
        String json =sharedPreferences.getString("wildlifeSighting",null);
        if (json==null){
            return new ArrayList<>();
        }
        else{


            Type type = new TypeToken<ArrayList<WildlifeSighting>>() {}.getType();
            return gson.fromJson(json,type);



        }

    }
}
