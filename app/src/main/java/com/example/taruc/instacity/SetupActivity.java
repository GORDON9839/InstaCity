package com.example.taruc.instacity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Gallery;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

public class SetupActivity extends AppCompatActivity {

    private EditText Username, Fullname,icNumber,contactNumber;
    private Button saveInformation;
    private ImageView profileImage;
    private RadioGroup genderRadio,residenceRadio;
    private RadioButton genderRB,residenceRB;

    private FirebaseAuth mAuth;
    private DatabaseReference UsersRef;
    private ProgressDialog loadingBar;
    private static final int Gallery_Pick=1;
    private Uri ImageUri;

    private StorageReference PostsImagesReference;
    private String saveCurrentDate,saveCurrentTime,post,downloadUrl;

    String currentUserID;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup);

        mAuth = FirebaseAuth.getInstance();
        currentUserID = mAuth.getCurrentUser().getUid();
        UsersRef = FirebaseDatabase.getInstance().getReference().child("Users").child(currentUserID);
        loadingBar = new ProgressDialog(this);
        PostsImagesReference = FirebaseStorage.getInstance().getReference();


        Username = (EditText) findViewById(R.id.setupUsername);
        Fullname = (EditText) findViewById(R.id.setupFullname);
        icNumber = (EditText) findViewById(R.id.setupIc);
        contactNumber =(EditText) findViewById(R.id.setupContact);
        genderRadio =(RadioGroup) findViewById(R.id.setupGender);
        residenceRadio =(RadioGroup) findViewById(R.id.setupResidence);

        saveInformation = (Button) findViewById(R.id.setupSaveInformation);
        profileImage = (ImageView) findViewById(R.id.setupImage);

        saveInformation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SaveAccountSetupInformation();
            }
        });

        profileImage.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
               OpenGallery();
            }
        });

    }

    private void OpenGallery() {
        Intent i = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.INTERNAL_CONTENT_URI);
        final int ACTIVITY_SELECT_IMAGE = 1234;
        startActivityForResult(i, Gallery_Pick);
       /* Intent galleryIntent = new Intent();
        galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
        galleryIntent.setType("/image/*");

        startActivityForResult(Intent.createChooser(galleryIntent,"Select Picture"), Gallery_Pick);*/
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode==Gallery_Pick&&resultCode==RESULT_OK&&data!=null){
            ImageUri = data.getData();
            profileImage.setImageURI(ImageUri);
        }
    }


    private void SaveAccountSetupInformation() {
        String username = Username.getText().toString();
        String fullname = Fullname.getText().toString();
        String ic = icNumber.getText().toString();
        String contact = contactNumber.getText().toString();
        if(ImageUri==null) {
            Toast.makeText(this, "Please select your Profile Image...", Toast.LENGTH_SHORT).show();
        }
        else if(TextUtils.isEmpty(username)){
            Toast.makeText(this,"Please enter your User Name...",Toast.LENGTH_SHORT).show();
        }else if(TextUtils.isEmpty(fullname)){
            Toast.makeText(this,"Please enter your Full Name...",Toast.LENGTH_SHORT).show();
        }else if(TextUtils.isEmpty(ic)){
            Toast.makeText(this,"Please enter your IC Number...",Toast.LENGTH_SHORT).show();
        }
        else if(TextUtils.isEmpty(contact)) {
            Toast.makeText(this, "Please enter your Contact Number...", Toast.LENGTH_SHORT).show();
        }else{
            loadingBar.setTitle("Verifying Your Information");
            loadingBar.setMessage("Please wait....");
            loadingBar.show();
            loadingBar.setCanceledOnTouchOutside(true);

            StoringImageToFirebaseStorage();



        }
    }

    private void StoringImageToFirebaseStorage() {
        Calendar calFordDate = Calendar.getInstance();
        SimpleDateFormat currentDate = new SimpleDateFormat("dd-MMMM-yyyy");
        saveCurrentDate=currentDate.format(calFordDate.getTime());

        Calendar calFordTime = Calendar.getInstance();
        SimpleDateFormat currentTime = new SimpleDateFormat("HH:mm");
        saveCurrentTime=currentTime.format(calFordDate.getTime());

        post=saveCurrentDate+saveCurrentTime;

        final StorageReference filePath = PostsImagesReference.child("Profile Images").child(ImageUri.getLastPathSegment()+post+".jpg");
        filePath.putFile(ImageUri).continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
            @Override
            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                if (!task.isSuccessful()){
                    throw task.getException();
                }

                return filePath.getDownloadUrl();
            }
        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {
                if (task.isSuccessful()){
                    Toast.makeText(SetupActivity.this,"Image uploaded successfully...",Toast.LENGTH_SHORT).show();
                    Uri downUri = task.getResult();
                    downloadUrl = downUri.toString();
                    Log.d("url", "onComplete: Url: "+ downUri.toString());
                    StoringImageToDatabase();
                }else{
                    String message = task.getException().getMessage();
                    Toast.makeText(SetupActivity.this,"Error Occured:"+message,Toast.LENGTH_SHORT).show();
                }
            }
        });
       /* filePath.putFile(ImageUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                if(task.isSuccessful()){
                   downloadUrl = task.getResult().getMetadata().getReference().getDownloadUrl().toString();

                    Toast.makeText(SetupActivity.this,"Image uploaded successfully..."+downloadUrl,Toast.LENGTH_SHORT).show();
                    StoringImageToDatabase();

                }else{
                    String message = task.getException().getMessage();
                    Toast.makeText(SetupActivity.this,"Error Occured:"+message,Toast.LENGTH_SHORT).show();
                }
            }
        });*/

    }

    private void StoringImageToDatabase() {

        String username = Username.getText().toString();
        String fullname = Fullname.getText().toString();
        String ic = icNumber.getText().toString();
        String contact = contactNumber.getText().toString();
        int selectedGender = genderRadio.getCheckedRadioButtonId();
        genderRB = (RadioButton) findViewById(selectedGender);
        int selectedResidence = residenceRadio.getCheckedRadioButtonId();
        residenceRB = (RadioButton) findViewById(selectedResidence);
        HashMap userMap=new HashMap();
        userMap.put("userName",username);
        userMap.put("fullName",fullname);
        userMap.put("icNumber",ic);
        userMap.put("contactNumber",contact);
        userMap.put("gender",genderRB.getText());
        userMap.put("residence",residenceRB.getText());
        userMap.put("profileImage",downloadUrl);
        UsersRef.updateChildren(userMap).addOnCompleteListener(new OnCompleteListener() {
            @Override
            public void onComplete(@NonNull Task task) {
                if(task.isSuccessful()){
                    SendUserToMainActivity();
                    Toast.makeText(SetupActivity.this,"Your Account is created successfully...",Toast.LENGTH_SHORT).show();
                    loadingBar.dismiss();
                }else{
                    String message = task.getException().getMessage();
                    Toast.makeText(SetupActivity.this,"Error Occured:"+message,Toast.LENGTH_SHORT).show();
                    loadingBar.dismiss();
                }
            }
        });
    }

    private void SendUserToMainActivity() {
        Intent mainIntent = new Intent(this,feed.class);
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(mainIntent);
        finish();
    }
}
