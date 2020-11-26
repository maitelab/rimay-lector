package com.maitelab.rimaylector;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.TextView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.text.TextRecognition;
import com.google.mlkit.vision.text.TextRecognizer;

import java.io.IOException;
import com.google.mlkit.vision.text.Text;
public class MainActivity extends AppCompatActivity {

    private TextRecognizer recognizer=null;
    private static TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //set welcome text
        textView= (TextView) findViewById(R.id.textView);



        System.out.println("******************************onCreate");
        recognizer = TextRecognition.getClient();

        // Get intent, action and MIME type
        Intent intent = getIntent();
        String action = intent.getAction();
        String type = intent.getType();

        if (Intent.ACTION_SEND.equals(action) && type != null) {
            textView.setText("");
            if (type.startsWith("image/")) {

                handleSendImage(intent); // Handle single image being sent
            }
        }
        else
        {
            textView.setText("Aplicación hecha por Henry Tong como experimento tecnológico. Busca una imagen en tu galería o en otra app que contenga texto y compártela con esta app para que te lo lea.");
        }
    }
    void handleSendImage(Intent intent) {
        Uri imageUri = (Uri) intent.getParcelableExtra(Intent.EXTRA_STREAM);
        if (imageUri != null) {
            // Update UI to reflect image being shared
            System.out.println("****************algo llegó " + imageUri.getPath());

            InputImage image;

            try {
                image = InputImage.fromFilePath(getApplicationContext(), imageUri);

                System.out.println("*****got image now textrecog");
                Task<Text> result =
                        recognizer.process(image)
                                .addOnSuccessListener(new OnSuccessListener<Text>() {
                                    @Override
                                    public void onSuccess(Text result) {
                                        System.out.println("************************************************Task completed successfully");
                                        // ...

                                        String resultText = result.getText();

                                        /*
                                        for (Text.TextBlock block : result.getTextBlocks()) {
                                            //System.out.println("****bloque");
                                            String blockText = block.getText();
                                            //System.out.println(blockText);
                                            //Point[] blockCornerPoints = block.getCornerPoints();
                                            //Rect blockFrame = block.getBoundingBox();
                                            for (Text.Line line : block.getLines()) {
                                                //System.out.println("****linea******");
                                                String lineText = line.getText();
                                                //System.out.println(lineText);
                                                //Point[] lineCornerPoints = line.getCornerPoints();
                                                //Rect lineFrame = line.getBoundingBox();
                                                for (Text.Element element : line.getElements()) {
                                                    String elementText = element.getText();
                                                    //Point[] elementCornerPoints = element.getCornerPoints();
                                                    //Rect elementFrame = element.getBoundingBox();
                                                }
                                            }
                                        }
                                        */
                                        textView.setText(resultText);
                                        textView.announceForAccessibility(resultText);

                                    }
                                })
                                .addOnFailureListener(
                                        new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                // Task failed with an exception
                                                // ...
                                                e.printStackTrace();
                                            }
                                        });

            } catch (IOException e) {
                e.printStackTrace();
            }


        }
    }

}