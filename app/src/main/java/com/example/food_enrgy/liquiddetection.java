package com.example.food_enrgy;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.food_enrgy.ml.Food;


import org.tensorflow.lite.DataType;
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class liquiddetection extends AppCompatActivity {


    Button camera, gallery;
    ImageView imageView;
    TextView result,txtnuti;
    int imageSize = 32;
    String MY_PREFS_NAME = "MyPrefsFile";
    String address , weather;


    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_liquiddetection);

        gallery = findViewById(R.id.button2);

        result = findViewById(R.id.result);
        imageView = findViewById(R.id.imageView);
        txtnuti = findViewById(R.id.txtnuti);

        camera = findViewById(R.id.button3);

        camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (checkSelfPermission(android.Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                        Intent cameraintent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        startActivityForResult(cameraintent, 3);

                    } else {
                        requestPermissions(new String[]{android.Manifest.permission.CAMERA}, 100);

                    }
                }
            }
        });

        gallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), chart.class);
                startActivity(intent);

            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 3) {
            Bitmap image = (Bitmap) data.getExtras().get("data");
            int dimension = Math.min(image.getWidth(), image.getHeight());
            image = ThumbnailUtils.extractThumbnail(image, dimension, dimension);
            imageView.setImageBitmap(image);

            image = Bitmap.createScaledBitmap(image, imageSize, imageSize, false);
            classifyImage(image);
        } else {
            Uri dat = data.getData();
            Bitmap image = null;
            try {
                image = MediaStore.Images.Media.getBitmap(this.getContentResolver(), dat);
            } catch (IOException e) {
                e.printStackTrace();
            }
            imageView.setImageBitmap(image);

            image = Bitmap.createScaledBitmap(image, imageSize, imageSize, false);
            classifyImage(image);
        }
    }

    private void classifyImage(Bitmap image) {

        try {
            Food model = Food.newInstance(getApplicationContext());

            // Creates inputs for reference.
            TensorBuffer inputFeature0 = TensorBuffer.createFixedSize(new int[]{1, 32, 32, 3}, DataType.FLOAT32);

            ByteBuffer byteBuffer = ByteBuffer.allocateDirect(4 * imageSize * imageSize * 3);
            byteBuffer.order(ByteOrder.nativeOrder());

            int[] intValues = new int[imageSize * imageSize];
            image.getPixels(intValues, 0, image.getWidth(), 0, 0, image.getWidth(), image.getHeight());
            int pixel = 0;
            //iterate over each pixel and extract R, G, and B values. Add those values individually to the byte buffer.
            for (int i = 0; i < imageSize; i++) {
                for (int j = 0; j < imageSize; j++) {
                    int val = intValues[pixel++]; // RGB
                    byteBuffer.putFloat(((val >> 16) & 0xFF) * (1.f / 1));
                    byteBuffer.putFloat(((val >> 8) & 0xFF) * (1.f / 1));
                    byteBuffer.putFloat((val & 0xFF) * (1.f / 1));
                }
            }
            inputFeature0.loadBuffer(byteBuffer);

            // Runs model inference and gets result.
            Food.Outputs outputs = model.process(inputFeature0);
            TensorBuffer outputFeature0 = outputs.getOutputFeature0AsTensorBuffer();

            float[] confidences = outputFeature0.getFloatArray();
            // find the index of the class with the biggest confidence.
            int maxPos = 0;
            float maxConfidence = 0;
            for (int i = 0; i < confidences.length; i++) {
                if (confidences[i] > maxConfidence) {
                    maxConfidence = confidences[i];
                    maxPos = i;
                }
            }
            String[] classes = {"chai","juice","water","milk"};

            showchart(classes[maxPos]);

            result.setText(classes[maxPos]);
            // Releases model resources if no longer used.
            model.close();
        } catch (IOException e) {
            // TODO Handle the exception
        }
    }

    private void showchart(String data) {
        if(data.equals("Water"))
        {
            txtnuti.setText("Nutrition:- Calories: 0,\n Protein: 0 grams \n ,Carbs:0, \n Fiber:0 ,\n Fat: 0, \nSodium 9.5mg");
//            SharedPreferences sharedPreferences = getSharedPreferences("My",MODE_PRIVATE);
//            SharedPreferences.Editor myEdit = sharedPreferences.edit();
//            myEdit.putString("calories","208");
//            myEdit.putString("protein","28.8");
//            myEdit.putString("fiber","0");
//            myEdit.putString("carbs","0");
//            myEdit.putString("fat","9.5");
//            myEdit.commit();
        }else if(data.equals("Chai"))
        {
            txtnuti.setText("Nutrition:- Fat 0g,\nProtein 0g, \nCarbohydrates 1g, \nCalories 2,\n Fiber 0");

            SharedPreferences sharedPreferences = getSharedPreferences("My",MODE_PRIVATE);
            SharedPreferences.Editor myEdit = sharedPreferences.edit();
            myEdit.putString("calories","2");
            myEdit.putString("protein","0");
            myEdit.putString("fiber","0");
            myEdit.putString("carbs","1");
            myEdit.putString("fat","0");
            myEdit.commit();
        }else if(data.equals("Juice"))
        {
            txtnuti.setText("Nutrition:- Calories: 112, \n Protein: 2 grams, \nCarbohydrates: 26 grams,\n Fat 0, \nProtein 0.2, \nFiber 0.2g ");
            SharedPreferences sharedPreferences = getSharedPreferences("My",MODE_PRIVATE);
            SharedPreferences.Editor myEdit = sharedPreferences.edit();
            myEdit.putString("calories","112");
            myEdit.putString("protein","2");
            myEdit.putString("fiber","0.2");
            myEdit.putString("carbs","26");
            myEdit.putString("fat","0");
            myEdit.commit();

        }else if(data.equals("Milk"))
        {

            txtnuti.setText("Nutrition:- Calories: 149, \nProtein: 8 grams, \nFat: 8 grams, \nCarbohydrates: 12 grams, \nSugar: 12 grams");
            SharedPreferences sharedPreferences = getSharedPreferences("My",MODE_PRIVATE);
            SharedPreferences.Editor myEdit = sharedPreferences.edit();
            myEdit.putString("calories","149");
            myEdit.putString("protein","8");
            myEdit.putString("fiber","0");
            myEdit.putString("carbs","12");
            myEdit.putString("fat","8");
            myEdit.commit();

        }


    }
}