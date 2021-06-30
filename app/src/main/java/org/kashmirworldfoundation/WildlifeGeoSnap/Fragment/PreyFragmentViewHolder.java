package org.kashmirworldfoundation.WildlifeGeoSnap.Fragment;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import org.kashmirworldfoundation.WildlifeGeoSnap.R;
public class PreyFragmentViewHolder extends RecyclerView.ViewHolder {
    TextView Prey;
    TextView dateId;
    ImageView imgId;
    public PreyFragmentViewHolder(@NonNull View itemView) {
        super(itemView);
        Prey= itemView.findViewById(R.id.PreyStationId);
        dateId = itemView.findViewById(R.id.PreyDateId);
        imgId = itemView.findViewById(R.id.PreyImgId);
    }
}
