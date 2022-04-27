package org.kashmirworldfoundation.WildlifeGeoSnap;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class Takedown_ListAdapter extends RecyclerView.Adapter<Takedown_List_ViewHolder> {

    TakedownListActivity activity;
    //final public ListItemClickListener mOnClickListener;

    ArrayList<Takedown> CtakedownList;
    Context mContext;
    Takedown Ctakedown;
    Takedown_ListAdapter(ArrayList<Takedown> TakedownList, TakedownListActivity activity){
        this.activity=activity;
        this.CtakedownList=TakedownList;

    }

    @NonNull
    @Override
    public Takedown_List_ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View itemView= LayoutInflater.from(parent.getContext())
                //pass the list_row_item back to itemView
                .inflate(R.layout.row_rebait,parent,false);
        Log.e("layout", "inflated");
        //wait to open browser

        //and pass the entry
        return new Takedown_List_ViewHolder(itemView);

    }


    public void onBindViewHolder(@NonNull Takedown_List_ViewHolder holder, int position) {
        Ctakedown=CtakedownList.get(position);

        holder.Name.setText(activity.AuthorS);
        holder.Date.setText(Ctakedown.getCurrentTime().toString());
        holder.Sd.setText(Ctakedown.getSdNum());
        holder.Bait.setText(Ctakedown.getLureType());
        holder.Pics.setText(Ctakedown.getTotalPics());
        holder.Notes.setText("");

    }





    public int getItemCount() {
        return CtakedownList.size();
    }

}
