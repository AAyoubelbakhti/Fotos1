package com.ayoub.FOTOS1;



import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import java.io.File;
import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

public class MainActivity extends AppCompatActivity {
    private Uri photoURI;
    private File photoFile;
    ActivityResultLauncher<Intent> someActivityResultLauncher;
    ActivityResultLauncher<Intent> cameraResultLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        someActivityResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if (result.getResultCode() == Activity.RESULT_OK) {
                            Intent data = result.getData();
                            if (data != null) {
                                Uri uri = data.getData();
                                ImageView imageView = findViewById(R.id.imageView);
                                imageView.setImageURI(uri);
                            }
                        }
                    }
                }
        );

        cameraResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if (result.getResultCode() == AppCompatActivity.RESULT_OK) {
                            ImageView imageView = findViewById(R.id.imageView);
                            Intent data = result.getData();
                            Bundle extras = (data != null) ? data.getExtras() : null;

                            if (extras != null) {
                                Bitmap imageBitmap = (Bitmap) extras.get("data");
                                imageView.setImageBitmap(imageBitmap);
                            } else if (photoURI != null) {
                                imageView.setImageURI(photoURI);
                            } else {
                                Log.e("ERROR", "No hi ha cap photoURI");
                            }
                        }
                    }
                }
        );

        Button button = findViewById(R.id.button);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openSomeActivityForResult();

            }

        });


        //botón cam
        Button fotoButton = findViewById(R.id.fotoButton);
        fotoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                cameraResultLauncher.launch(intent);
            }
        });
        Button tempPhotoButton = findViewById(R.id.tempPhotoButton);
        tempPhotoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {

                    photoFile = File.createTempFile("tmpImg", ".jpg", getExternalFilesDir(Environment.DIRECTORY_PICTURES));
                    photoURI = FileProvider.getUriForFile(
                            MainActivity.this,
                            "com.ayoub.FOTOS1.fileprovider",
                            photoFile
                    );
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                    cameraResultLauncher.launch(intent);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void openSomeActivityForResult() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/jpg");
        intent.putExtra(Intent.EXTRA_LOCAL_ONLY, true);
        someActivityResultLauncher.launch(intent);
    }





}
