package com.example.yusuf.mobilemechanic;

/**
 * Created by YUSUF on 6/18/2016.
 */
import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

public class CustomListAdapter extends BaseAdapter {
    private Activity activity;
    private LayoutInflater inflater;
    private List<Requests> requests;
    List<String> distance;


    public CustomListAdapter(Activity activity, List<Requests> requests, List<String> distance ) {
        this.activity = activity;
        this.requests = requests;
        this.distance= distance;

    }

    @Override
    public int getCount() {
        return requests.size();
    }

    @Override
    public Object getItem(int location) {
        return requests.get(location);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (inflater == null)
            inflater = (LayoutInflater) activity
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (convertView == null)
        convertView = inflater.inflate(R.layout.list_row, null);



        ImageView thumbNail = (ImageView) convertView.findViewById(R.id.thumb);
        TextView lname = (TextView) convertView.findViewById(R.id.name);
        TextView lproblem = (TextView) convertView.findViewById(R.id.problem);
        TextView ldistance = (TextView) convertView.findViewById(R.id.distance);







        Requests m = requests.get(position);


        lname.setText(m.getRequesterUsername());


        lproblem.setText(m.getProblem());


        ldistance.setText(distance.get(position));

//       if(m.getVechile().equals("TRUCK")){
//           thumbNail.setImageResource(R.drawable.truck);
//       }else if(m.problem.equals("BUS")){
//           thumbNail.setImageResource(R.drawable.bus);
//        }else if(m.problem.equals("CAR")){
//           thumbNail.setImageResource(R.drawable.car);
//       }else if(m.problem.equals("SCOOTER")){
//           thumbNail.setImageResource(R.drawable.scooter);
//       }else if(m.problem.equals("BIKE")){
//           thumbNail.setImageResource(R.drawable.bike);
//       }

        switch (m.getVechile()){
            case "CAR":
                thumbNail.setImageResource(R.drawable.car);
                break;
            case "BUS":
                thumbNail.setImageResource(R.drawable.bus);
                break;
            case "TRUCK":
                thumbNail.setImageResource(R.drawable.truck);
                break;
            case "SCOOTER":
                thumbNail.setImageResource(R.drawable.scooter);
                break;
            case "BIKE":
                thumbNail.setImageResource(R.drawable.bike);
                break;
        }

//        if(m.isMale()){
//            thumbNail.setImageResource(R.drawable.m);
//        }else{
//            thumbNail.setImageResource(R.drawable.f);
//        }

        return convertView;
    }

}