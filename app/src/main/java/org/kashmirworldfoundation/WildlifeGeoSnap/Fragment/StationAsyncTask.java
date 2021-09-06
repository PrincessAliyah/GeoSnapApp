package org.kashmirworldfoundation.WildlifeGeoSnap.Fragment;

import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.Log;
import android.util.Pair;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import org.kashmirworldfoundation.WildlifeGeoSnap.DeleteAsyncTask;
import org.kashmirworldfoundation.WildlifeGeoSnap.Member;
import org.kashmirworldfoundation.WildlifeGeoSnap.Study;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class StationAsyncTask extends AsyncTask<String, Void, String> {

    private FirebaseFirestore firebaseFirestore;
    private CollectionReference collectionReference;
    private FirebaseAuth FireAuth;

    private ArrayList<Study> CStations= new ArrayList<>();
    private Member mem;
    private String Org;
    private static final String TAG = "StationAsyncTask";
    private int count;
    private int size;

    private ListFragment listFragment;



    StationAsyncTask(ListFragment li){listFragment=li;}

    protected void update(){
        listFragment.updateStationList(CStations);
        listFragment.updateList();
    }
    public static Date setTimeToMidnight(Date date) {
        Calendar calendar = Calendar.getInstance();

        calendar.setTime( date );
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        return calendar.getTime();
    }


    @Override
    protected String doInBackground(String... strings) {


        // Add data from Firebase on the the Arrays
        try {
            FireAuth = FirebaseAuth.getInstance();
            firebaseFirestore = FirebaseFirestore.getInstance();
            collectionReference = firebaseFirestore.collection("Study");
        }
        catch (Exception e){
            //Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_SHORT).show();
        }
        Calendar c= Calendar.getInstance();
        Date currentTime = setTimeToMidnight(c.getTime());


        c.add(Calendar.DATE,-30);
        Date twoMonths=setTimeToMidnight(c.getTime());
        c.add(Calendar.DATE,-30);
        Date oneMonth=setTimeToMidnight(c.getTime());
        c.add(Calendar.DATE,-28);
        Date twoDays=setTimeToMidnight(c.getTime());
        c.add(Calendar.DATE,-1);
        Date oneDay=setTimeToMidnight(c.getTime());
        c.add(Calendar.DATE,-1);
        Date today=setTimeToMidnight(c.getTime());

        Date prevTime=c.getTime();
        final Date[] end1 = new Date[1];
        final ArrayList<Pair<String,String> >paths=new ArrayList();
//        final ArrayList<String> stations=new ArrayList<>();
        final ArrayList<String> oneDayStations=new ArrayList<>();
        final ArrayList<String> twoDaysStations=new ArrayList<>();
        final ArrayList<String> oneMonthStations=new ArrayList<>();
        final ArrayList<String> twoMonthsStations=new ArrayList<>();
        final ArrayList<String> threeMonthsStations=new ArrayList<>();

        firebaseFirestore.collection("Member").document(FireAuth.getCurrentUser().getUid()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()){
                    mem=task.getResult().toObject(Member.class);
                    collectionReference.whereEqualTo("org",mem.getOrg()).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()){
                                size = task.getResult().size();
                                for (DocumentSnapshot objectDocumentSnapshot: task.getResult()) {
                                    Study stat = objectDocumentSnapshot.toObject(Study.class);
                                    if (stat != null && stat.getEnd() != null) {
                                        end1[0] = setTimeToMidnight(stat.getEnd().toDate());
                                        if (end1[0].compareTo(currentTime) == 0){
                                            threeMonthsStations.add(stat.getTitle());
                                        }
                                        else if (end1[0].compareTo(oneDay) == 0){
                                            oneDayStations.add(stat.getTitle());

                                        }
                                        else if (end1[0].compareTo(twoDays) == 0){
                                            twoDaysStations.add(stat.getTitle());
                                        }
                                        else if (end1[0].compareTo(oneMonth) == 0){
                                            oneMonthStations.add(stat.getTitle());
                                        }
                                        else if (end1[0].compareTo(twoMonths) == 0){
                                            twoMonthsStations.add(stat.getTitle());
                                        }


                                        if (end1[0].compareTo(today) < 0) {
//                                            Log.e(TAG, stat.getTitle() + "/n" + end1[0].toString() + "/n" + currentTime.toString());
                                            paths.add(new Pair<>(objectDocumentSnapshot.getReference().getPath(), stat.getTitle()));
                                        }

                                        CStations.add(stat);
                                        count++;
                                    }



                                    if(count==size){
                                        update();
//                                        if(!stations.isEmpty()){
//                                            listFragment.studyMiss(stations,listFragment);
//                                        }
                                        if (!oneDayStations.isEmpty()){
                                            listFragment.studyMissOneDay(oneDayStations, listFragment);
                                        }
                                        if (!twoDaysStations.isEmpty()){
                                            listFragment.studyMissTwoDays(twoDaysStations, listFragment);
                                        }
                                        if (!oneMonthStations.isEmpty()){
                                            listFragment.studyMissOneMonth(oneMonthStations, listFragment);
                                        }
                                        if (!twoMonthsStations.isEmpty()){
                                            listFragment.studyMissTwoMonths(twoMonthsStations, listFragment);
                                        }
                                        if (!threeMonthsStations.isEmpty()){
                                            listFragment.studyMissThreeMonths(threeMonthsStations, listFragment);
                                        }
//                                        if (!oneDayStations.isEmpty() && ifShowDialogOneDay){
//                                            listFragment.studyMissOneDay(oneDayStations, listFragment);
//                                        }
//                                        if (!twoDaysStations.isEmpty() && ifShowDialogTwoDays){
//                                            listFragment.studyMissTwoDays(twoDaysStations, listFragment);
//                                        }
//                                        if (!oneMonthStations.isEmpty() && ifShowDialogOneMonth){
//                                            listFragment.studyMissOneMonth(oneMonthStations, listFragment);
//                                        }
//                                        if (!twoMonthsStations.isEmpty() && ifShowDialogTwoMonths){
//                                            listFragment.studyMissTwoMonths(twoMonthsStations, listFragment);
//                                        }
//                                        if (!threeMonthsStations.isEmpty() && ifShowDialogThreeMonths){
//                                            listFragment.studyMissThreeMonths(threeMonthsStations, listFragment);
//                                        }


                                        if (!paths.isEmpty()){
                                            for(Pair<String,String> stuff: paths){
                                                new DeleteAsyncTask(stuff.first,stuff.second,mem).execute();
                                            }

                                        }
                                    }
                                }
                            }
                        }
                    });
                }
            }
        });




        return null;
    }
}
