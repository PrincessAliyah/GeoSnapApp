package org.kashmirworldfoundation.WildlifeGeoSnap.Fragment;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import org.kashmirworldfoundation.WildlifeGeoSnap.R;
public class WildlifeSightingFragmentViewHolder extends RecyclerView.ViewHolder {
    TextView Sighting;
    TextView dateId;
    ImageView imgId;
    public WildlifeSightingFragmentViewHolder(@NonNull View itemView) {
        super(itemView);
        Sighting = itemView.findViewById(R.id.SightingStationId);
        dateId = itemView.findViewById(R.id.PreyDateId);
        imgId = itemView.findViewById(R.id.PreyImgId);
    }
}
