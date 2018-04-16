package com.app.rpi.rpi;
/*
* @autor: Jefferson Rivera
* riverajefer@gmail.com
* Abril  2018
*
* */
import android.app.Activity;
import android.os.Bundle;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends Activity {
    // Write a message to the database
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference refHome = database.getReference("home");
    DatabaseReference refLuces, refBotones, refLuzSala, refPulsadorA;
    ToggleButton btnToggle;
    TextView textEstadoPulsador;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        refLuces = refHome.child("luces");
        refLuzSala = refLuces.child("luz_sala");

        refBotones = refHome.child("botones");
        refPulsadorA = refBotones.child("pulsador_a");

        btnToggle = (ToggleButton)  findViewById(R.id.toggleButton);
        btnToggle.setTextOn("APAGAR");
        btnToggle.setTextOff("ENCENDER");

        textEstadoPulsador = (TextView) findViewById(R.id.textViewPulsador);

        controlLED(refLuzSala, btnToggle);

        estadoPulsador(refPulsadorA, textEstadoPulsador);
    }

    private void controlLED(final DatabaseReference refLed, final ToggleButton toggle_btn ) {

        toggle_btn.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                refLed.setValue(isChecked);
            }
        });

        refLed.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Boolean estado_led  = (Boolean) dataSnapshot.getValue();
                toggle_btn.setChecked(estado_led);
                if(estado_led){
                    toggle_btn.setTextOn("APAGAR");
                } else {
                    toggle_btn.setTextOff("ENCENDER");
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) { }

        });
    }

    private void estadoPulsador(final DatabaseReference refPulsador_a, final TextView textEstadoPulsador) {

        refPulsador_a.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Boolean estado_pulsador = (Boolean) dataSnapshot.getValue();
                textEstadoPulsador.setText(estado_pulsador.toString());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) { }
        });
    }
}
