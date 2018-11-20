package com.example.taruc.instacity;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.Query;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;




/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link feedFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link feedFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class feedFragment extends Fragment{
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER


    // TODO: Rename and change types of parameters
    private RecyclerView feedList;
    private FirebaseAuth mAuth;
    private DatabaseReference usersRef,postsRef;
    private FirebaseRecyclerAdapter<FeedsClass,feedViewHolder> adapter;

    String currentUserID;



    private OnFragmentInteractionListener mListener;

    public feedFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @
     * @return A new instance of fragment feedFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static feedFragment newInstance() {
        feedFragment fragment = new feedFragment();

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


        View view = inflater.inflate(R.layout.fragment_feed, container, false);
        feedList = (RecyclerView)view.findViewById(R.id.allFeedList);

        mAuth = FirebaseAuth.getInstance();
        currentUserID = mAuth.getCurrentUser().getUid();
        usersRef = FirebaseDatabase.getInstance().getReference().child("Users");
        postsRef = FirebaseDatabase.getInstance().getReference().child("Posts");
        Query query = postsRef.orderByKey();

        feedList.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        feedList.setLayoutManager(linearLayoutManager);

        FirebaseRecyclerOptions<FeedsClass> options = new
                FirebaseRecyclerOptions.Builder<FeedsClass>()
                .setQuery(query,FeedsClass.class).build();




        adapter= new FirebaseRecyclerAdapter<FeedsClass,feedViewHolder>
                (options)
        {




            @Override
            protected void onBindViewHolder(feedViewHolder holder, int position, FeedsClass model) {
                holder.setUsername(model.getUserName());
                holder.setCaption(model.getCaption());
                holder.setDate(model.getDate());
                holder.setTime(model.getTime());
                holder.setPostImage(getContext(),model.getPostImage());
                holder.setProfileImage(getContext(),model.getProfileImage());

            }
            @Override
            public feedViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
                View view = LayoutInflater.from(viewGroup.getContext())
                        .inflate(R.layout.all_feed_layout, viewGroup, false);
                return new feedViewHolder(view);
            }


        };
        Log.d("hihi",adapter.getItemCount()+"22");
        feedList.setAdapter(adapter);

        /*
         */

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);


    }

    @Override
    public void onStart() {
        super.onStart();
        adapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        adapter.stopListening();
    }

    private static class feedViewHolder extends RecyclerView.ViewHolder{

        View mView;
        public feedViewHolder(View itemView){
            super(itemView);
            mView=itemView;
        }
        public void setUsername(String username){
            TextView usern = (TextView)mView.findViewById(R.id.feedUserName);
            usern.setText(username);

        }
        public void setProfileImage(Context ctx,String profileImage){
            ImageView image=(ImageView)mView.findViewById(R.id.feedProfileImage);
            Picasso.with(ctx).load(profileImage).into(image);
        }
        public void setTime(String time){
            TextView t = (TextView)mView.findViewById(R.id.feedTime);
            t.setText(" "+time);
        }
        public void setDate(String date){
            TextView d = (TextView)mView.findViewById(R.id.feedDate);
            d.setText("  "+date);
        }
        public void setCaption(String caption){
            TextView cap = (TextView)mView.findViewById(R.id.feedDescription);
            cap.setText(caption);
        }
        public void setPostImage(Context ctx1,String postImage){
            ImageView image=(ImageView)mView.findViewById(R.id.feedImage);
            Picasso.with(ctx1).load(postImage).into(image);
        }
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
   /* public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }*/


    public void onDetach() {
        super.onDetach();
        mListener = null;
    }


    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

}