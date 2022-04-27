package org.kashmirworldfoundation.WildlifeGeoSnap.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import org.kashmirworldfoundation.WildlifeGeoSnap.GlideApp;
import org.kashmirworldfoundation.WildlifeGeoSnap.WildlifeSighting;
import org.kashmirworldfoundation.WildlifeGeoSnap.R;

import java.util.ArrayList;

public class WildlifeSightingFragmentAdapter extends RecyclerView.Adapter<WildlifeSightingFragmentViewHolder> {
    WildlifeSightingFragment wildlifeSightingFragment;
    ArrayList<WildlifeSighting> sightingslist;
    WildlifeSighting csighting;
    WildlifeSightingFragmentAdapter(ArrayList<WildlifeSighting> wildlifeSightings, WildlifeSightingFragment wildlifeSightingFragment){
        this.wildlifeSightingFragment = wildlifeSightingFragment;
        this.sightingslist = wildlifeSightings;
    }
    @NonNull
    @Override
    public WildlifeSightingFragmentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView= LayoutInflater.from(parent.getContext())
                //pass the list_row_item back to itemView
                .inflate(R.layout.row_wildlifesighting,parent,false);

        //wait to open browser
        itemView.setOnClickListener(wildlifeSightingFragment);
        //and pass the entry
        return new WildlifeSightingFragmentViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull WildlifeSightingFragmentViewHolder holder, int position) {

            csighting = sightingslist.get(position);

            holder.Sighting.setText(csighting.getSighting());
            holder.dateId.setText(csighting.getPosted().toDate().toString());
            fetchData(csighting.getPic(),holder.imgId);




    }
    private void fetchData(String location, ImageView image) {
        StorageReference ref = FirebaseStorage.getInstance().getReference(location);
        GlideApp.with(wildlifeSightingFragment)
                .load(ref)
                .into(image);
    }
    @Override
    public int getItemCount() {
        return sightingslist.size();
    }
}
