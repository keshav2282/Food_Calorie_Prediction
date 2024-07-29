package com.example.food_enrgy;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.eazegraph.lib.charts.PieChart;
import org.eazegraph.lib.models.PieModel;

public class chart extends AppCompatActivity {

    private Button click;
    private PieChart chart;
    private int i1 = 15;
    private int i2 = 25;
    private int i3 = 35;
    private int i4 = 45;

    TextView txttitle;
    String calories,calcusim,sodium,protein,car,cho,iron;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chart);

        SharedPreferences prf = getSharedPreferences("My", Context.MODE_PRIVATE);
        calories = prf.getString("calories", "");
        calcusim=prf.getString("protein","");
        cho=prf.getString("fiber","");
        sodium=prf.getString("carbs","");
        car=prf.getString("fat","");



         Toast.makeText(getApplicationContext(), calories.toString(), Toast.LENGTH_SHORT).show();
        click = findViewById(R.id.btn_click);
        chart = findViewById(R.id.pie_chart);


//        txttitle.setText("Calories - Yellow" +"\n" +
//                "Calcium - Green" +"\n"+
//                "Cholesterol -  Red" + "\n"+
//                "Sodium - blue" + "\n" +
//                "carbohydrate - Cyan  " +"\n" +
//                "Iron - Dark Blue" +"\n" +
//                "Protein - Dark Green" );
        click.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                chart.addPieSlice(new PieModel("Calories", Float.parseFloat(calories), Color.parseColor("#FFA726")));
                chart.addPieSlice(new PieModel("Protein", Float.parseFloat(calcusim), Color.parseColor("#66BB6A")));
                chart.addPieSlice(new PieModel("Fiber", Float.parseFloat(cho), Color.parseColor("#EF5350")));
                chart.addPieSlice(new PieModel("Carbs", Float.parseFloat(sodium), Color.parseColor("#2986F6")));

                chart.addPieSlice(new PieModel("Fat", Float.parseFloat(car), Color.parseColor("#00FFFF")));

                chart.startAnimation();
                click.setClickable(false);
            }
        });
    }
}