package com.intern.barberataskapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class AddUserActivity extends AppCompatActivity {

    private CircleImageView profileIv;
    private EditText nameEt, phoneTv, sEmailEt;
    private Button updateBtn;
    DatabaseReference  mRef;

    private static final int CAMERA_REQUEST_CODE = 967;
    private static final int STORAGE_REQUEST_CODE = 968;

    private static final int IMAGE_PICK_GALLERY_CODE = 965;
    private static final int IMAGE_PICK_CAMERA_CODE = 964;


    //permission arrays
    private String[] cameraPermissions;
    private String[] storagePermissions;

    //image picked uri
    private Uri s_image_uri;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_user);

        profileIv = findViewById(R.id.profileIv);
        nameEt = findViewById(R.id.nameEt);
        phoneTv = findViewById(R.id.phoneTv);
        sEmailEt = findViewById(R.id.sEmailEt);
        updateBtn = findViewById(R.id.updateBtn);

        mRef = FirebaseDatabase.getInstance().getReference("Users");
        //init permissions arrays
        cameraPermissions = new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};
        storagePermissions = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE};

        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Please wait...");
        progressDialog.setCanceledOnTouchOutside(false);


        profileIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //pick image
                showImagePickDialog();
            }
        });
        updateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                inputData();
            }
        });

    }
String nameSt,phoneSt,mailSt;
    private void inputData() {
        nameSt = nameEt.getText().toString();
        phoneSt = phoneTv.getText().toString();
        mailSt = sEmailEt.getText().toString();
        if (nameSt.isEmpty()){
            nameEt.setError("Name Required");
        }
        else if (phoneSt.isEmpty()){
            phoneTv.setError("phoneNumber is Empty");
        }
        else if (mailSt.isEmpty()){
            sEmailEt.setError("mail is empty");
        }
        else if (phoneSt.length() !=10){
            phoneTv.setError("In-valid Number(write without country code)");
        }
        else if (!Patterns.EMAIL_ADDRESS.matcher(mailSt).matches()) {
            sEmailEt.setError("Invalid Mail Address");
            return;
        }
        else {
            validateData();
        }

    }

    private void validateData() {
        Query userQuery= FirebaseDatabase.getInstance().getReference().child("Users")
                .orderByChild("phone").equalTo(phoneSt);
        userQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.getChildrenCount()>0){
                    phoneTv.setError("Phone number exists");
                }
                else {
                    saveUser();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(AddUserActivity.this, ""+error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void saveUser() {

        progressDialog.setMessage("Adding user...");
        progressDialog.show();
        String timpstamp = "" + System.currentTimeMillis();
        if (s_image_uri == null) {
            //upload without image
            DatabaseReference user = mRef.push();
            String uid_user = user.getKey();
            HashMap<String, Object> hashMap = new HashMap<>();
            hashMap.put("uid",""+uid_user);
            hashMap.put("name",""+nameSt);
            hashMap.put("phone","+91"+phoneSt);
            hashMap.put("mail",""+mailSt);
            hashMap.put("profileImage", "");
            hashMap.put("timestamp", "" + timpstamp);
            mRef.child(uid_user).setValue(hashMap)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    progressDialog.dismiss();
                    Toast.makeText(AddUserActivity.this, "add user sucessfully", Toast.LENGTH_SHORT).show();
                    goToHomePage();
                }
            })
                    .addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    progressDialog.dismiss();
                    Toast.makeText(AddUserActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }
        else {
            DatabaseReference user = mRef.push();
            String uid_user = user.getKey();
            //upload with image
            // save info with image
            // name and path of image
            String filePathName = "profile_images/"+""+uid_user;
            StorageReference storageReference = FirebaseStorage.getInstance().getReference(filePathName);
            storageReference.putFile(s_image_uri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            // get the Url of UploadTask
                            Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                            while (!uriTask.isSuccessful());
                            Uri downloadImageUri = uriTask.getResult();
                            if (uriTask.isSuccessful())
                            {
                                //save info with image
                                HashMap<String, Object> hashMap = new HashMap<>();

                                hashMap.put("uid",""+uid_user);
                                hashMap.put("name",""+nameSt);
                                hashMap.put("phone","91"+phoneSt);
                                hashMap.put("mail",""+mailSt);
                                hashMap.put("profileImage", ""+downloadImageUri);
                                hashMap.put("timestamp", "" + timpstamp);
                                mRef.child(uid_user).setValue(hashMap)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                progressDialog.dismiss();
                                                Toast.makeText(AddUserActivity.this, "add user sucessfully", Toast.LENGTH_SHORT).show();
                                                goToHomePage();
                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                progressDialog.dismiss();
                                                Toast.makeText(AddUserActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                                            }
                                        });
                            }
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    progressDialog.dismiss();
                    Toast.makeText(AddUserActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });

        }

    }

    private void goToHomePage() {
        startActivity(new Intent(this,MainActivity.class));
        finish();
    }

    private boolean checkStoragePermission() {
        boolean result = ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) == (PackageManager.PERMISSION_GRANTED);
        return result;
    }

    private void requestStoragePermission() {
        ActivityCompat.requestPermissions(this, storagePermissions, STORAGE_REQUEST_CODE);
    }

    private boolean checkCameraPermission() {
        boolean result = ContextCompat.checkSelfPermission(this,
                Manifest.permission.CAMERA) == (PackageManager.PERMISSION_GRANTED);
        boolean result1 = ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) == (PackageManager.PERMISSION_GRANTED);
        return result && result1;
    }

    private void requestCameraPermission() {
        ActivityCompat.requestPermissions(this, cameraPermissions, CAMERA_REQUEST_CODE);
    }

    private void showImagePickDialog() {
        // options to display in dialog
        String[] options = {"Camera", "Gallery"};
        //dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Pick Image")
                .setItems(options, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (which == 0) {
                            //Camera Clicked
                            if (checkCameraPermission()) {
                                // cameraPermission allowed
                                pickFromCamera();
                            } else {
                                // cameraPermission not allowed, request
                                requestCameraPermission();
                            }
                        } else {
                            //Gallery Clicked
                            if (checkStoragePermission()) {
                                // Storage Permission allowed
                                pickFromGallery();
                            } else {
                                // Storage Permission not allowed, request
                                requestStoragePermission();

                            }

                        }
                    }
                }).show();
    }

    private void pickFromGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, IMAGE_PICK_GALLERY_CODE);
    }

    private void pickFromCamera() {
        ContentValues contentValues = new ContentValues();
        contentValues.put(MediaStore.Images.Media.TITLE, "Temp_Image Title");
        contentValues.put(MediaStore.Images.Media.DESCRIPTION, "Temp_Image Description");

        s_image_uri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues);

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, s_image_uri);
        startActivityForResult(intent, IMAGE_PICK_CAMERA_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case CAMERA_REQUEST_CODE: {
                if (grantResults.length > 0) {
                    boolean cameraAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    boolean storageAccepted = grantResults[1] == PackageManager.PERMISSION_GRANTED;
                    if (cameraAccepted && storageAccepted) {
                        //permission allowed
                        pickFromCamera();
                    } else {
                        //permission Denied
                        Toast.makeText(getApplicationContext(), "Camera permissions are necessary...", Toast.LENGTH_SHORT).show();
                    }
                }
            }
            break;
            case STORAGE_REQUEST_CODE: {
                if (grantResults.length > 0) {
                    boolean storageAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    if (storageAccepted) {
                        //permission allowed
                        pickFromGallery();
                    } else {
                        //permission Denied
                        Toast.makeText(getApplicationContext(), "Storage permission is necessary...", Toast.LENGTH_SHORT).show();
                    }
                }
            }
            break;
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (resultCode == RESULT_OK) {
            if (requestCode == IMAGE_PICK_GALLERY_CODE) {
                //get picked image
                s_image_uri = data.getData();
                //set to imageView
                profileIv.setImageURI(s_image_uri);
            } else if (requestCode == IMAGE_PICK_CAMERA_CODE) {
                //set to imageView
                profileIv.setImageURI(s_image_uri);
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}