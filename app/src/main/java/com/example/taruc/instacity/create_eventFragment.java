package com.example.taruc.instacity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

import static android.app.Activity.RESULT_OK;



public class create_eventFragment extends Fragment {

    private Spinner locationSpinner;
    private EditText titleEvent;
    private EditText eventDate;
    private EditText eventSTime;
    private EditText eventETime;
    private EditText captionEvent;
    private Button postButton;
    private ImageButton postImage;

    private FirebaseAuth mAuth;
    private DatabaseReference UsersRef,PostRef;
    private ProgressDialog loadingBar;
    private static final int Gallery_Pick=1;
    private Uri ImageUri;

    private StorageReference PostsImagesReference;
    private String saveCurrentDate,saveCurrentTime,post,downloadUrl;
    String currentUserID;


    private OnFragmentInteractionListener mListener;

    public create_eventFragment() {
        // Required empty public constructor
    }


    public static create_eventFragment newInstance() {
        create_eventFragment fragment = new create_eventFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view =inflater.inflate(R.layout.fragment_event, container, false);

        mAuth = FirebaseAuth.getInstance();
        currentUserID = mAuth.getCurrentUser().getUid();
        UsersRef = FirebaseDatabase.getInstance().getReference().child("Users").child(currentUserID);
        PostRef = FirebaseDatabase.getInstance().getReference().child("Events");
        loadingBar = new ProgressDialog(getActivity());
        PostsImagesReference = FirebaseStorage.getInstance().getReference();
        captionEvent = (EditText)view.findViewById(R.id.captionEvent);
        titleEvent = view.findViewById(R.id.eventName);
        eventDate = view.findViewById(R.id.eventDate);
        eventSTime=view.findViewById(R.id.eventStartTime);
        eventETime = view.findViewById(R.id.eventEndTime);
        locationSpinner = view.findViewById(R.id.locationEventSpinner);
        postButton = (Button) view.findViewById(R.id.postButton);
        postImage = (ImageButton) view.findViewById(R.id.createEventImage);
        postButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("hihi","testing 1 2 333");
                inputFieldValidation();
            }
        });

        postImage.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                OpenGallery();
            }
        });


        return view;
        // Inflate the layout for this fragment

    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    private void OpenGallery() {
        Intent i = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.INTERNAL_CONTENT_URI);

        startActivityForResult(i, Gallery_Pick);

    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {


        if(requestCode==Gallery_Pick&&resultCode==RESULT_OK&&data!=null){
            ImageUri = data.getData();
            postImage.setImageURI(ImageUri);
        }
    }
    private void inputFieldValidation() {
        Log.d("hihi","testing 1 2 33");
        String cap = captionEvent.getText().toString();

        if (TextUtils.isEmpty(cap)) {
            Toast.makeText(getActivity(), "Please enter caption...", Toast.LENGTH_SHORT).show();
        } else {
            loadingBar.setTitle("Creating Post");
            loadingBar.setMessage("Please wait....");
            loadingBar.show();
            loadingBar.setCanceledOnTouchOutside(true);

            StoringImageToFirebaseStorage();


        }
    }

        private void StoringImageToFirebaseStorage(){
            Calendar calFordDate = Calendar.getInstance();
            SimpleDateFormat currentDate = new SimpleDateFormat("dd-MMMM-yyyy");
            saveCurrentDate = currentDate.format(calFordDate.getTime());
            Log.d("hihi","testing 1 2 3 4");
            Calendar calFordTime = Calendar.getInstance();
            SimpleDateFormat currentTime = new SimpleDateFormat("HH:mm");
            saveCurrentTime = currentTime.format(calFordTime.getTime());

            post = saveCurrentDate + saveCurrentTime;

            final StorageReference filePath = PostsImagesReference.child("Event Images").child(ImageUri.getLastPathSegment() + post + ".jpg");
            filePath.putFile(ImageUri).continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                    if (!task.isSuccessful()) {
                        throw task.getException();
                    }

                    return filePath.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if (task.isSuccessful()) {
                        Toast.makeText(getActivity(), "Image uploaded successfully...", Toast.LENGTH_SHORT).show();
                        Uri downUri = task.getResult();
                        downloadUrl = downUri.toString();
                        Log.d("url", "onComplete: Url: " + downUri.toString());

                        StoringEventToDatabase();
                    } else {
                        String message = task.getException().getMessage();
                        Toast.makeText(getActivity(), "Error Occured:" + message, Toast.LENGTH_SHORT).show();
                    }
                }
            });

        }

    private void StoringEventToDatabase() {
        UsersRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String userName = dataSnapshot.child("userName").getValue().toString();
                    Log.d("hihi","testing 1111");
                    String cap = captionEvent.getText().toString();
                    String sTime = eventSTime.getText().toString();
                    String eTime = eventETime.getText().toString();
                    String date = eventDate.getText().toString();
                    String eventTitle = titleEvent.getText().toString();
                    String location = locationSpinner.getSelectedItem().toString();
                    HashMap postMap = new HashMap();
                    postMap.put("uid", currentUserID);
                    postMap.put("userName", userName);
                    postMap.put("uploadDate", saveCurrentDate);
                    postMap.put("uploadTime", saveCurrentTime);
                    postMap.put("eventCaption", cap);
                    postMap.put("eventImage", downloadUrl);
                    postMap.put("eventDate",date);
                    postMap.put("startTime",sTime);
                    postMap.put("endTime",eTime);
                    postMap.put("eventTitle",eventTitle);
                    postMap.put("location",location);

                    Log.d("hihi","testing 222 ");
                    PostRef.child(post).updateChildren(postMap).addOnCompleteListener(new OnCompleteListener() {
                        @Override
                        public void onComplete(@NonNull Task task) {
                            if (task.isSuccessful()) {
                               // SendUserToEventActivity();
                                Log.d("hihi","testing 333");
                                Toast.makeText(getActivity(), "Your Posts is created successfully...", Toast.LENGTH_SHORT).show();
                                loadingBar.dismiss();
                            } else {
                                String message = task.getException().getMessage();
                                Toast.makeText(getActivity(), "Error Occured:" + message, Toast.LENGTH_SHORT).show();
                                loadingBar.dismiss();
                            }
                        }
                    });
                } else {
                    loadingBar.dismiss();

                    Toast.makeText(getActivity(), "Error", Toast.LENGTH_SHORT).show();
                }
            }
            public void onCancelled(DatabaseError databaseError){

            }
        });
        }
   /* private void SendUserToEventActivity() {
        Intent mainIntent = new Intent(getActivity(),feed.class);
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(mainIntent);

    }*/

   /* public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }*/

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }
    public interface OnFragmentInteractionListener {

        void onFragmentInteraction(Uri uri);
    }
}



