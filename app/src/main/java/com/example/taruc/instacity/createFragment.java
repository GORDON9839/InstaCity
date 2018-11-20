package com.example.taruc.instacity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
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


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link createFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link createFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class createFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER


    // TODO: Rename and change types of parameters

    private EditText caption;
    private Button createNewPost;
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

    public createFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *

     * @return A new instance of fragment createFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static createFragment newInstance() {
        createFragment fragment = new createFragment();


        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        mAuth = FirebaseAuth.getInstance();
        currentUserID = mAuth.getCurrentUser().getUid();
        UsersRef = FirebaseDatabase.getInstance().getReference().child("Users").child(currentUserID);
        PostRef = FirebaseDatabase.getInstance().getReference().child("Posts");
        loadingBar = new ProgressDialog(getActivity());
        PostsImagesReference = FirebaseStorage.getInstance().getReference();
        View view = inflater.inflate(R.layout.fragment_create, container, false);


        caption = (EditText)view.findViewById(R.id.captionEvent);

        createNewPost = (Button) view.findViewById(R.id.createButton);
        postImage = (ImageButton) view.findViewById(R.id.createImage);

        createNewPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
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

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }


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

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    private void StoringImageToFirebaseStorage() {
        Calendar calFordDate = Calendar.getInstance();
        SimpleDateFormat currentDate = new SimpleDateFormat("dd-MMMM-yyyy");
        saveCurrentDate=currentDate.format(calFordDate.getTime());

        Calendar calFordTime = Calendar.getInstance();
        SimpleDateFormat currentTime = new SimpleDateFormat("HH:mm");
        saveCurrentTime=currentTime.format(calFordTime.getTime());

        post=saveCurrentDate+saveCurrentTime;

        final StorageReference filePath = PostsImagesReference.child("Post Images").child(ImageUri.getLastPathSegment()+post+".jpg");
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
                    Toast.makeText(getActivity(),"Image uploaded successfully...",Toast.LENGTH_SHORT).show();
                    Uri downUri = task.getResult();
                    downloadUrl=downUri.toString();
                    Log.d("url", "onComplete: Url: "+ downUri.toString());
                    StoringFeedToDatabase();
                }else{
                    String message = task.getException().getMessage();
                    Toast.makeText(getActivity(),"Error Occured:"+message,Toast.LENGTH_SHORT).show();
                }
            }
        });
       /* filePath.putFile(ImageUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                if(task.isSuccessful()){
                    Toast.makeText(getActivity(),"Image uploaded successfully...",Toast.LENGTH_SHORT).show();
                    UploadTask.TaskSnapshot url1 = task.getResult();
                    String url= task.getResult().toString();

                    Log.d("downloadUrl",task.get);
                    StoringFeedToDatabase();



                }else{
                    String message = task.getException().getMessage();
                    Toast.makeText(getActivity(),"Error Occured:"+message,Toast.LENGTH_SHORT).show();
                }
            }
        });*/

    }
    private void inputFieldValidation() {
        String cap = caption.getText().toString();

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
    private void StoringFeedToDatabase() {



        UsersRef.addValueEventListener(new ValueEventListener(){
            @Override
            public void onDataChange(DataSnapshot dataSnapshot){
                if(dataSnapshot.exists()){
                    String userName = dataSnapshot.child("userName").getValue().toString();
                    String userProfileImage = dataSnapshot.child("profileImage").getValue().toString();
                    String cap = caption.getText().toString();


                    HashMap postMap=new HashMap();
                    postMap.put("uid",currentUserID);
                    postMap.put("date",saveCurrentDate);
                    postMap.put("time",saveCurrentTime);
                    postMap.put("caption",cap);
                    postMap.put("postImage",downloadUrl);
                    postMap.put("userName",userName);
                    postMap.put("profileImage",userProfileImage);

                   PostRef.child(post).updateChildren(postMap).addOnCompleteListener(new OnCompleteListener() {
                        @Override
                        public void onComplete(@NonNull Task task) {
                            if(task.isSuccessful()){
                                SendUserToMainActivity();
                                Toast.makeText(getActivity(),"Your Posts is created successfully...",Toast.LENGTH_SHORT).show();
                                loadingBar.dismiss();
                            }else{
                                String message = task.getException().getMessage();
                                Toast.makeText(getActivity(),"Error Occured:"+message,Toast.LENGTH_SHORT).show();
                                loadingBar.dismiss();
                            }
                        }
                    });
                }else{
                    loadingBar.dismiss();

                    Toast.makeText(getActivity(),"Error",Toast.LENGTH_SHORT).show();
                }
            }
            public void onCancelled(DatabaseError databaseError){

            }
        });


    }

    private void SendUserToMainActivity() {
        Intent mainIntent = new Intent(getActivity(),feed.class);
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(mainIntent);

    }
}
