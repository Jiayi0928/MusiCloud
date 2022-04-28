package edu.neu.madcourse.musicloud.dashboard;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContract;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.core.content.MimeTypeFilter;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.common.internal.Objects;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

import edu.neu.madcourse.musicloud.HomeScreenActivity;
import edu.neu.madcourse.musicloud.R;
import edu.neu.madcourse.musicloud.User;

public class GetAvatar extends AppCompatActivity {
    private Button camera;
    private Button gallery;
    private static final int CAMERA_IMAGE = 1;
    private static final int GALLERY_IMAGE = 2;
    private static final int STORAGE_REQUEST = 3;
    private Uri imagePath = null;
    private User currentUser;
    private DatabaseReference databaseReference;
    private DatabaseReference userDatabase;
    private ImageView avatar;
    String photoPath;
    String cameraPermission[];
    String storagePermission[];
    File photo = null;
    String imagePath2;
    File image = null;

    String currentPhotoPath;
    File photoFile = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_avatar);
        avatar = findViewById(R.id.navUserAvatar);
        Bundle extras = getIntent().getExtras();
        if (extras != null && extras.getParcelable("currentUser") != null) {
            currentUser = extras.getParcelable("currentUser");
            Log.v("Logged in: ", currentUser.getUsername());
        }
        databaseReference = FirebaseDatabase.getInstance().getReference();
        userDatabase = databaseReference.child("users").child(currentUser.getUsername()).child("profileImage");
        camera = findViewById(R.id.camera);
        camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //ask permission
                if(ContextCompat.checkSelfPermission(GetAvatar.this,Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED){
                    ActivityCompat.requestPermissions(GetAvatar.this,new String[] {Manifest.permission.CAMERA}, CAMERA_IMAGE);
                }else {dispatchTakePictureIntent();}
            }
        });
        gallery=findViewById(R.id.gallery);
        gallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent gallery = new Intent(Intent.ACTION_PICK,MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(gallery,GALLERY_IMAGE);
            }
        });
    }
//refer code from: https://developer.android.com/training/camera/photobasics
    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go

                try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                ex.printStackTrace();

            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this,
                        "edu.neu.madcourse.musicloud.android.fileprovider",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, CAMERA_IMAGE);
            }}
        }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
//        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        currentPhotoPath = image.getAbsolutePath();
        return image;
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == CAMERA_IMAGE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                dispatchTakePictureIntent();
            } else {
                Toast.makeText(this, "Camera Permission is Required to Use camera.", Toast.LENGTH_SHORT).show();
            }
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == CAMERA_IMAGE) {
            if (resultCode == Activity.RESULT_OK) {
                File f = new File(currentPhotoPath);
                Uri contentUri = Uri.fromFile(f);
                avatar.setImageURI(contentUri);
                Log.d("tag", "ABsolute Url of Image is " + Uri.fromFile(f));
                currentUser.setProfileImage(String.valueOf(contentUri));
                userDatabase.setValue(String.valueOf(contentUri));

                Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);

                mediaScanIntent.setData(contentUri);
                this.sendBroadcast(mediaScanIntent);




            }

        }
        if(requestCode == GALLERY_IMAGE){
            if(resultCode == Activity.RESULT_OK){
                Uri contentUri = data.getData();
                String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
                String imageFileName = "JPEG_" + timeStamp +"."+getFileExt(contentUri);
                Log.d("tag", "onActivityResult: Gallery Image Uri:  " +  imageFileName);
                avatar.setImageURI(contentUri);
                currentUser.setProfileImage(String.valueOf(contentUri));
                userDatabase.setValue(String.valueOf(contentUri));

            }
        }


    }
    private String getFileExt(Uri contentUri) {
        ContentResolver c = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(c.getType(contentUri));
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(getApplicationContext(),DashBoardActivity.class).putExtra("currentUser",currentUser));
    }
}