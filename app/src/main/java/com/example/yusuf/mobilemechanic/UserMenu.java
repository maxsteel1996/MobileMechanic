package com.example.yusuf.mobilemechanic;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class UserMenu extends AppCompatActivity implements View.OnClickListener{
    ImageView bus, car, truck,scooter,bike;
    TextView t;
    EditText editText;
    String vechile="";
    String problem="";
    public void next(View v){
        if((!vechile.equals(""))&&(!editText.getText().toString().equals(""))){
        Intent i=new Intent(getApplicationContext(),DriverLocation.class);
        i.putExtra("v",vechile);
        i.putExtra("p",editText.getText().toString());
        startActivity(i);}
        else{
            Toast.makeText(getApplicationContext(),"Please select problem and vechile first", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_menu);
        bus=(ImageView) findViewById(R.id.bus);
        car=(ImageView) findViewById(R.id.car);
        truck=(ImageView) findViewById(R.id.truck);
        scooter=(ImageView) findViewById(R.id.scooter);
        bike=(ImageView) findViewById(R.id.bike);
        t=(TextView)findViewById(R.id.textView);
        bus.setOnClickListener(this);
        car.setOnClickListener(this);
        truck.setOnClickListener(this);
        bike.setOnClickListener(this);
        scooter.setOnClickListener(this);
        editText=(EditText)findViewById(R.id.editText);
    }



    @Override
    public void onClick(View view) {

            problem=editText.getText().toString();


        switch (view.getId()){
            case R.id.bus:

                t.setText("BUS");
                vechile="BUS";
                bus.setImageResource(R.drawable.busg);
                car.setImageResource(R.drawable.car);
                truck.setImageResource(R.drawable.truck);
                scooter.setImageResource(R.drawable.scooter);
                bike.setImageResource(R.drawable.bike);
                break;
            case R.id.car: t.setText("CAR");
                vechile="CAR";
                bus.setImageResource(R.drawable.bus);
                car.setImageResource(R.drawable.carg);
                truck.setImageResource(R.drawable.truck);
                scooter.setImageResource(R.drawable.scooter);
                bike.setImageResource(R.drawable.bike);
                break;
            case R.id.truck: t.setText("TRUCK");
                vechile="TRUCK";
                bus.setImageResource(R.drawable.bus);
                car.setImageResource(R.drawable.car);
                truck.setImageResource(R.drawable.truckg);
                scooter.setImageResource(R.drawable.scooter);
                bike.setImageResource(R.drawable.bike);
                break;
            case R.id.bike: t.setText("BIKE");
                vechile="BIKE";
                bus.setImageResource(R.drawable.bus);
                car.setImageResource(R.drawable.car);
                truck.setImageResource(R.drawable.truck);
                scooter.setImageResource(R.drawable.scooter);
                bike.setImageResource(R.drawable.bikeg);
                break;
            case R.id.scooter: t.setText("SCOOTER");
                vechile="SCOOTER";
                bus.setImageResource(R.drawable.bus);
                car.setImageResource(R.drawable.car);
                truck.setImageResource(R.drawable.truck);
                scooter.setImageResource(R.drawable.scooterg);
                bike.setImageResource(R.drawable.bike);
                break;
        }
    }
}
