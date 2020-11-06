package com.utng.discoverw;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ActionBar;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.Objects;

import id.zelory.compressor.Compressor;


public class PhotoActivity extends AppCompatActivity {

    private ImageView foto;
    private Button subir, seleccionar;
    private FirebaseFirestore imageRef;
    private StorageReference storageReference;
    private ProgressDialog cargando;

    Bitmap thumb = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo);

        foto = findViewById(R.id.photo);
        seleccionar = findViewById(R.id.btnSelectPhoto);
        subir = findViewById(R.id.btnUploadPhoto);
        imageRef = FirebaseFirestore.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference().child("imagesUsers");
        cargando = new ProgressDialog(this);

        seleccionar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CropImage.startPickImageActivity(PhotoActivity.this);
                // Permite seleccionar foto
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == CropImage.PICK_IMAGE_CHOOSER_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            Uri imageuri = CropImage.getPickImageResultUri(this, data);
            // Recortar imagen
            CropImage.activity(imageuri).setGuidelines(CropImageView.Guidelines.ON)
                    .setRequestedSize(640, 480)
                    .setAspectRatio(2, 1).start(PhotoActivity.this);
        }

        if(requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);

            if(requestCode == RESULT_OK) {
                Uri resultUri = result.getUri();
                File url = new File(resultUri.getPath());


                Picasso.with(this).load(url).into(foto);
                // Comprimir image
                try {
                    thumb = new Compressor(this)
                            .setMaxWidth(640)
                            .setMaxHeight(480)
                            .setQuality(90)
                            .compressToBitmap(url);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                ByteArrayOutputStream byteArrayOutStrem = new ByteArrayOutputStream();
                thumb.compress(Bitmap.CompressFormat.JPEG, 90, byteArrayOutStrem);
                final byte [] thumb_byte = byteArrayOutStrem.toByteArray();

                // Final del compressor
                final String nombre = "post.jpg";

                subir.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        cargando.setTitle("Subiendo foto...");
                        cargando.setMessage("Espere por favor..");
                        cargando.show();

                        StorageReference ref = storageReference.child("post.jpg");
                        UploadTask uploadTask = ref.putBytes(thumb_byte);

                        // Subir imagen a Storage
                        Task<Uri> uriTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                            @Override
                            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                                if(!task.isSuccessful()) {
                                    throw Objects.requireNonNull(task.getException());
                                }
                                return ref.getDownloadUrl();
                            }
                        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                            @Override
                            public void onComplete(@NonNull Task<Uri> task) {
                                Uri dowloadUri = task.getResult();
                                // imageRef.push().child("urifoto").setValue(dowloadUri.toString()); TODO : Guardar en FireStore
                                cargando.dismiss();
                                Toast.makeText(PhotoActivity.this, "Imagen cargada con exito", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                });
            }
        }

    }

}