package edu.neu.madcourse.musicloud.dashboard;

import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Vibrator;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Random;

import edu.neu.madcourse.musicloud.R;
import edu.neu.madcourse.musicloud.Song;

public class ShakeActivity extends AppCompatActivity implements SensorEventListener {
    private ImageView imageView;
    private TextView songTitle;
    private TextView songArtist;
    private SensorManager sensorManager;
    private Sensor sensor;
    private boolean isSensorAvailable,isNotFirstTime;
    private float curX, curY, curZ, difX, difY, difZ, lastX, lastY, lastZ;
    private final float threshold=1f;
    private Vibrator vibrator;
    private DatabaseReference databaseReference;
    private DatabaseReference postReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shake);

        imageView = findViewById(R.id.songImg);
        databaseReference = FirebaseDatabase.getInstance().getReference();;
        postReference = databaseReference.child("posts");

       

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        vibrator =(Vibrator) getSystemService(Context.VIBRATOR_SERVICE);

        if (sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER) != null){
            sensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
            isSensorAvailable = true;

        }else{
            isSensorAvailable = false;
        }

    }
    private void getPost(int num){
        postReference.orderByChild("commentCnt").startAfter(num).limitToFirst(1).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Song song = snapshot.getValue(Song.class);
                if (song == null){
                    Log.e("not get",null);
                }
                songTitle.setText(song.getTitle());
                songArtist.setText(song.getArtist());
                Glide.with(getApplicationContext()).load(song.getImg()).into(imageView);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }



    @Override
    protected void onPause() {
        super.onPause();
        if(isSensorAvailable){
            sensorManager.unregisterListener(this);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(isSensorAvailable){
            sensorManager.registerListener(this,sensor,SensorManager.SENSOR_DELAY_NORMAL);
        }
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        curX = sensorEvent.values[0];
        curY = sensorEvent.values[1];
        curZ = sensorEvent.values[2];
        if(isNotFirstTime){
            difX = Math.abs(curX - lastX);
            difY = Math.abs(curY - lastY);
            difZ = Math.abs(curZ -  lastZ);

            if((difX > threshold && difY > threshold)||(difX > threshold && difZ>threshold)||(difY>threshold && difZ >threshold)){
               vibrator.vibrate(500);
                postReference.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        int count = Math.toIntExact(snapshot.getChildrenCount());
                        Log.e("Random count", String.valueOf(count));
                        int random = (int)Math.floor(Math.random()*count + 1);
                        Log.e("Random", String.valueOf(random));
                        getPost(random);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

            }
        }
        lastX = curX;
        lastY = curY;
        lastZ = curZ;
        isNotFirstTime = true;


    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }
}