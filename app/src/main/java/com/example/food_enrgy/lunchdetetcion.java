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

import com.example.food_enrgy.ml.Lunch;

import org.tensorflow.lite.DataType;
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class lunchdetetcion extends AppCompatActivity {

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
        setContentView(R.layout.activity_lunchdetetcion);

        gallery = findViewById(R.id.button2);
        txtnuti = findViewById(R.id.txtnuti);
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
            Lunch model = Lunch.newInstance(getApplicationContext());

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
            Lunch.Outputs outputs = model.process(inputFeature0);
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
            String[] classes = {"Alo_Bhaji","Dosa","Chole_Bhature","Noodles"};

            showchart(classes[maxPos]);

            result.setText(classes[maxPos]);
            // Releases model resources if no longer used.
            // Releases model resources if no longer used.
            model.close();
        } catch (IOException e) {
            // TODO Handle the exception
        }
    }

    private void showchart(String data) {
        if(data.equals("Alo_Bhaji"))
        {
            txtnuti.setText("Nutrition:- Fat 18g. 23%, \n Carbs 35g. 13%, \n Protein 2.34, \n Calories 194, \n Fiber 3.6");
            SharedPreferences sharedPreferences = getSharedPreferences("My",MODE_PRIVATE);
            SharedPreferences.Editor myEdit = sharedPreferences.edit();
            myEdit.putString("calories","194");
            myEdit.putString("protein","2.34");
            myEdit.putString("fiber","3.6");
            myEdit.putString("carbs","35");
            myEdit.putString("fat","18");
            myEdit.commit();
        }else if(data.equals("Dosa"))
        {
            txtnuti.setText("Nutrition:- Fat 3.7g ,\n Protein 3.9g, \nCarbohydrates 29g, \n Calories 168, \n Fiber 0.9");

            SharedPreferences sharedPreferences = getSharedPreferences("My",MODE_PRIVATE);
            SharedPreferences.Editor myEdit = sharedPreferences.edit();
            myEdit.putString("calories","168");
            myEdit.putString("protein","3.9");
            myEdit.putString("fiber","0.9");
            myEdit.putString("carbs","29");
            myEdit.putString("fat","3.7");
            myEdit.commit();
        }else if(data.equals("Chole_Bhature"))
        {
            txtnuti.setText("Nutrition:- Calories: 427, \n Protein: 10.8 grams, \n Carbohydrates: 50.3 grams, \n Fat 20.1, \n Protein 10.8, \n Fiber 12g ");
            SharedPreferences sharedPreferences = getSharedPreferences("My",MODE_PRIVATE);
            SharedPreferences.Editor myEdit = sharedPreferences.edit();
            myEdit.putString("calories","427");
            myEdit.putString("protein","10.8");
            myEdit.putString("fiber","012");
            myEdit.putString("carbs","50.3");
            myEdit.putString("fat","20.1");
            myEdit.commit();

        }else if(data.equals("Noodles"))
        {

            txtnuti.setText("Nutrition:- Calories: 188, \n Carbs: 27 grams, \n fat: 7 grams, \n Protein: 4 grams, \n Fiber: 0.9 grams,");
            SharedPreferences sharedPreferences = getSharedPreferences("My",MODE_PRIVATE);
            SharedPreferences.Editor myEdit = sharedPreferences.edit();
            myEdit.putString("calories","188");
            myEdit.putString("protein","4");
            myEdit.putString("fiber","0.9");
            myEdit.putString("carbs","0");
            myEdit.putString("fat","7");
            myEdit.commit();

        }
    }


}