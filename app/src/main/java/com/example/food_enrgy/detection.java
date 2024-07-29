package com.example.food_enrgy;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

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
import android.widget.Toast;

import com.example.food_enrgy.ml.Breakfast;


import org.tensorflow.lite.DataType;
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Objects;

public class
detection extends AppCompatActivity {


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
        setContentView(R.layout.activity_detection);


        txtnuti = findViewById(R.id.txtnuti);
        gallery = findViewById(R.id.button2);

        result = findViewById(R.id.result);
        imageView = findViewById(R.id.imageView);

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
            Breakfast model = Breakfast.newInstance(getApplicationContext());

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
            Breakfast.Outputs outputs = model.process(inputFeature0);
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
            String[] classes = {"Apple","Banana","Poha","Vadapav"};


            result.setText(classes[maxPos]);


            showchart(classes[maxPos]);


            // Releases model resources if no longer used.
            model.close();
        } catch (IOException e) {
            // TODO Handle the exception
        }
    }

    private void showchart(String data) {

        if(data.equals("Apple"))
        {
            txtnuti.setText("Nutrition:- Calories: 94.6,Water: 156 grams,Protein: 0.43 grams,Carbs: 25.1 grams, Fiber: 4.37 grams,Fat: 0.3 grams");
            SharedPreferences sharedPreferences = getSharedPreferences("My",MODE_PRIVATE);
            SharedPreferences.Editor myEdit = sharedPreferences.edit();
            myEdit.putString("calories","94");
            myEdit.putString("protein","94");
            myEdit.putString("fiber","94");
            myEdit.putString("carbs","25");
            myEdit.putString("fat","0");
            myEdit.commit();
        }else if(data.equals("Banana"))
        {
            txtnuti.setText("Nutrition:- Fat 0.4g, Cholesterol 0mg, Protein 1.3g, Carbohydrates 27g");

            SharedPreferences sharedPreferences = getSharedPreferences("My",MODE_PRIVATE);
            SharedPreferences.Editor myEdit = sharedPreferences.edit();
            myEdit.putString("calories","89");
            myEdit.putString("protein","1");
            myEdit.putString("fiber","2.6");
            myEdit.putString("carbs","27");
            myEdit.putString("fat","0");
            myEdit.commit();
        }else if(data.equals("Poha"))
        {
            txtnuti.setText("Nutrition:- Fat 8.5, Fiber 4.3, Calories 272, Protein 5.5, Carbohydrates 43.3");
            SharedPreferences sharedPreferences = getSharedPreferences("My",MODE_PRIVATE);
            SharedPreferences.Editor myEdit = sharedPreferences.edit();
            myEdit.putString("calories","272");
            myEdit.putString("protein","6");
            myEdit.putString("fiber","4.3");
            myEdit.putString("carbs","43.3");
            myEdit.putString("fat","8.5");
            myEdit.commit();

        }else if(data.equals("Vadapav"))
        {

            txtnuti.setText("Nutrition:- Fat 9.5, Fiber 3.8, Calories 263, Protein 7.5, Carbohydrates 37");
            SharedPreferences sharedPreferences = getSharedPreferences("My",MODE_PRIVATE);
            SharedPreferences.Editor myEdit = sharedPreferences.edit();
            myEdit.putString("calories","272");
            myEdit.putString("protein","6");
            myEdit.putString("fiber","4.3");
            myEdit.putString("carbs","43.3");
            myEdit.putString("fat","8.5");
            myEdit.commit();

        }


    }
}

