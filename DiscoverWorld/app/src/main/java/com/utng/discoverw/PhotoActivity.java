package com.utng.discoverw;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Random;
//import id.zelory.compressor.Compressor;

public class PhotoActivity extends AppCompatActivity {
    private ProgressDialog loading;
    private ImageButton btnSelectPhoto;
    private ImageView photo;
    private EditText editPostTitle;
    private EditText editPostDescription;
    private Button btnUploadPost;

    private static final int REQUEST_PERMISSION_CAMERA = 98;
    private static final int REQUEST_IMAGE_CAMERA = 99;

    private String currentPhotoPath;
    private String namePost;


    private FirebaseAuth aAuth = FirebaseAuth.getInstance();

    private FirebaseFirestore ddBb = FirebaseFirestore.getInstance();
    FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
            .setTimestampsInSnapshotsEnabled(true)
            .build();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo);

        ddBb.setFirestoreSettings(settings);

        loading = new ProgressDialog(this);
        btnSelectPhoto = findViewById(R.id.btnSelectPhoto);
        photo = findViewById(R.id.photo);
        editPostTitle = findViewById(R.id.editPostTitle);
        editPostDescription = findViewById(R.id.editPostDescription);
        btnUploadPost = findViewById(R.id.btnUploadPost);

        setup();
        validPermission();
    }

    private void setup() {
        btnUploadPost.setEnabled(false);
        btnSelectPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validPermission();
            }
        });
    }

    private void validPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ActivityCompat.checkSelfPermission(PhotoActivity.this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                goToCamera();
            } else {
                // Pide permisos, segun respuesta va a onRequestPermissionsResult
                ActivityCompat.requestPermissions(PhotoActivity.this, new String[]{Manifest.permission.CAMERA}, REQUEST_PERMISSION_CAMERA);
            }
        } else {
            goToCamera();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_PERMISSION_CAMERA) {
            if (permissions.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                goToCamera();
            } else {
                // Si NO dio permisos
                Toast.makeText(this, "Necesitas habilitar los permisos", Toast.LENGTH_SHORT).show();
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    private void goToCamera() {
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Si no hay camara en el dispositivo
        if (cameraIntent.resolveActivity(getPackageManager()) != null) {
            File photoFile = null;
            try {
                photoFile = createFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (photoFile != null) {
                Uri photoUri = FileProvider.getUriForFile(
                        this,
                        "com.utng.discoverw",
                        photoFile
                );
                cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);// La foto se agrega a currentPhotoPath
                startActivityForResult(cameraIntent, REQUEST_IMAGE_CAMERA);
            }
        }
    }

    private File createFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HH-mm-ss", Locale.getDefault()).format(new Date());
        namePost = timeStamp + "_" + Objects.requireNonNull(aAuth.getCurrentUser()).getUid();
        File image = File.createTempFile(
                namePost,
                ".jpg",
                getExternalFilesDir(Environment.DIRECTORY_PICTURES)// Se almacena en la APP
        );

        currentPhotoPath = image.getAbsolutePath();
        return image;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        //Resibe foto
        if (requestCode == REQUEST_IMAGE_CAMERA) {
            if (resultCode == Activity.RESULT_OK) {
                // Extrae foto tomada
                photo.setImageURI(Uri.parse(currentPhotoPath));
                Log.i("TAG", currentPhotoPath);
                setupUpload();
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void setupUpload() {
        btnUploadPost.setEnabled(true);
        btnUploadPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (correctData()) {
                    uploadPhoto();
                }
            }
        });
    }

    private void uploadPhoto() {
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference().child(namePost);

        loading.setTitle("Publicando...");
        loading.setMessage("Espere por favor..");
        loading.show();
        /**
         // Comprimir image
         try {
         Log.i("INFOTAG", "Intenta comprimir imagen");
         thumb_bitmap = new Compressor(this)
         .setMaxWidth(654)
         .setMaxHeight(420)
         .setQuality(90)
         .compressToBitmap(url);
         } catch (IOException e) {
         e.printStackTrace();
         Log.i("ERRORTAG", "Error al comprimir imagen");
         }

         ByteArrayOutputStream byteArrayOutputStrem = new ByteArrayOutputStream();
         thumb_bitmap.compress(Bitmap.CompressFormat.JPEG, 90, byteArrayOutputStrem);
         final byte [] thumb_byte = byteArrayOutputStrem.toByteArray();
         Log.i("INFOTAG", "Termina de comprimir imagen");
         **/

        InputStream stream = null;
        try {
            stream = new FileInputStream(new File(currentPhotoPath));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        assert stream != null;
        UploadTask uploadTask = storageRef.putStream(stream);

        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle unsuccessful uploads
                loading.dismiss();
                Toast.makeText(PhotoActivity.this, "No se pudo realizar la publicacion", Toast.LENGTH_SHORT).show();
                finish();
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Log.i("TAG", "taskSnapshot.getMetadata()" + taskSnapshot.getMetadata());
                registerPost();

                loading.dismiss();
                Toast.makeText(PhotoActivity.this, "Publicacion Exitosa", Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }

    private void registerPost() {
        Random rand = new Random();
        double rand_dub1 = rand.nextDouble();
        double rand_dub2 = rand.nextDouble();

        Map<String, Object> post = new HashMap<>();
        post.put("title", editPostTitle.getText().toString());
        post.put("description", editPostDescription.getText().toString());
        post.put("long", 21 + rand_dub1);
        post.put("lat", -100 + rand_dub2);

        ddBb.collection("posts")
                .document(namePost) // yyyyMMdd_HH-mm-ss_UID
                .set(post).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.i("TAG", "Post added");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.i("TAG", "Post added ERROR");
            }
        });

        Map<String, Object> postUser = new HashMap<>();
        postUser.put("key", namePost);

        /** Asigna al usuario los poat propios **/
        ddBb.collection("users")
                .document(aAuth.getCurrentUser().getUid())
                .collection("posts")
                .add(postUser).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
            @Override
            public void onSuccess(DocumentReference documentReference) {
                Log.i("TAG", "Post added to user");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.i("TAG", "Post added to user ERROR");
            }
        });
    }

    private boolean correctData() {
        if (editPostTitle.getText().length() > 1 && editPostDescription.getText().length() > 1) {
            if(editPostTitle.getText().length() < 31 && editPostDescription.getText().length() < 101){
                return true;
            } else {
                Toast.makeText(PhotoActivity.this, "El titulo no puede ser mayor a 30 caracteres \n Y la descripcion no puede ser mayor a 100 caracteres", Toast.LENGTH_SHORT).show();
                return false;
            }
        } else {
            Toast.makeText(PhotoActivity.this, "Favor de llenar todos los campos", Toast.LENGTH_SHORT).show();
            return false;
        }
    }

}