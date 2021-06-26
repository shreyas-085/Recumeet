package com.hackathon.recumeet.UserProfile;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.hackathon.recumeet.Adapter.PhotoAdapter;
import com.hackathon.recumeet.Models.Post;
import com.hackathon.recumeet.Models.User;
import com.hackathon.recumeet.R;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ProfileActivity extends AppCompatActivity {

    private String publisherId;

    private ImageView profile_pic;
    private TextView username, user_name, bio;

    private TextView noOfPosts;

    private Button follow;
    private FirebaseUser firebaseUser;
    private ProgressDialog pd;

    private int posts = 0;

    private List<Post> myPhotoList;
    private PhotoAdapter photoAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        publisherId = getIntent().getStringExtra("publisherId");

        profile_pic = findViewById(R.id.profile_pic);
        username = findViewById(R.id.full_name);
        user_name = findViewById(R.id.user_name);
        bio = findViewById(R.id.bio);

        noOfPosts = findViewById(R.id.no_posts);
        TextView noOFFollowers = findViewById(R.id.no_followers);
        TextView noOfFollowing = findViewById(R.id.no_following);

        follow = findViewById(R.id.follow);
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        myPhotoList = new ArrayList<>();
        RecyclerView recyclerView_profile = findViewById(R.id.recycler_view_profile);

        recyclerView_profile.setHasFixedSize(true);
        recyclerView_profile.setLayoutManager(new GridLayoutManager(ProfileActivity.this, 3));

        photoAdapter = new PhotoAdapter(ProfileActivity.this, myPhotoList, publisherId);
        recyclerView_profile.setAdapter(photoAdapter);

        RelativeLayout folloewer = findViewById(R.id.followers);
        RelativeLayout following = findViewById(R.id.following);

        folloewer.setOnClickListener(v -> {
            Intent intent = new Intent(ProfileActivity.this, ConnectionActivity.class);
            intent.putExtra("Uid", publisherId);
            startActivity(intent);
        });


        following.setOnClickListener(v -> {
            Intent intent = new Intent(ProfileActivity.this, ConnectionActivity.class);
            intent.putExtra("Uid", publisherId);
            startActivity(intent);
        });

        getUserData();

        setFollowStatus(follow);

        follow.setOnClickListener(v -> {
            if (follow.getText().toString().equals("Follow")) {
                FirebaseDatabase.getInstance().getReference().child("Follow").child(firebaseUser.getUid())
                        .child("following").child(publisherId).setValue(true);
                FirebaseDatabase.getInstance().getReference().child("Follow").child(publisherId)
                        .child("followers").child(firebaseUser.getUid()).setValue(true);
            } else {
                FirebaseDatabase.getInstance().getReference().child("Follow").child(firebaseUser.getUid())
                        .child("following").child(publisherId).removeValue();
                FirebaseDatabase.getInstance().getReference().child("Follow").child(publisherId)
                        .child("followers").child(firebaseUser.getUid()).removeValue();
            }
        });

        pd = new ProgressDialog(ProfileActivity.this);
        pd.setMessage("Getting Follow Status");
        pd.show();

        setNoOfPosts(noOfPosts);
        setnoOFFollowers(noOFFollowers);
        setnoOFFollowing(noOfFollowing);
        getPost();

        pd.dismiss();

    }

    private void getPost() {
        DatabaseReference ref5 = FirebaseDatabase.getInstance().getReference().child("posts");
        ref5.keepSynced(true);

        ref5.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                myPhotoList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Post post = snapshot.getValue(Post.class);
                    assert post != null;
                    if (post.getPublisher().equals(publisherId) && post.getImageUri() != null) {
                        myPhotoList.add(post);
                    }
                }
                noOfPosts.setText(String.valueOf(myPhotoList.size()));
                Collections.reverse(myPhotoList);
                photoAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ProfileActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setnoOFFollowing(final TextView noOfFollowing) {

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Follow").child(publisherId).child("following");
        ref.keepSynced(true);

        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                noOfFollowing.setText(snapshot.getChildrenCount() + " ");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ProfileActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                pd.dismiss();
            }
        });
    }

    private void setnoOFFollowers(final TextView noOFFollowers) {

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Follow").child(publisherId).child("followers");
        ref.keepSynced(true);

        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                noOFFollowers.setText(snapshot.getChildrenCount() + " ");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ProfileActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                pd.dismiss();
            }
        });
    }

    private void setNoOfPosts(final TextView noOfPosts) {
        DatabaseReference ref4 = FirebaseDatabase.getInstance().getReference().child("posts");
        ref4.keepSynced(true);

        ref4.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                    Post post = snapshot1.getValue(Post.class);
                    assert post != null;
                    if (post.getPublisher().equals(publisherId)) {
                        posts++;
                    }
                }

                noOfPosts.setText(posts + "");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ProfileActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                pd.dismiss();
            }
        });
    }

    private void setFollowStatus(final Button follow) {

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Follow").child(publisherId).child("followers");
        ref.keepSynced(true);

        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.child(firebaseUser.getUid()).exists()) {
                    follow.setText("Following");
                } else {
                    follow.setText("Follow");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ProfileActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                pd.dismiss();
            }
        });
    }

    private void getUserData() {

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("users").child(publisherId);
        ref.keepSynced(true);

        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                final User user = snapshot.getValue(User.class);

                assert user != null;
                Picasso.get().load(user.getProfileUri()).networkPolicy(NetworkPolicy.OFFLINE).into(profile_pic, new Callback() {
                    @Override
                    public void onSuccess() {

                    }

                    @Override
                    public void onError(Exception e) {
                        Picasso.get().load(user.getProfileUri()).into(profile_pic);
                    }
                });

                username.setText(user.getFName() + " " + user.getLName());
                user_name.setText(user.getUName());
                bio.setText(user.getBio());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ProfileActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                pd.dismiss();
            }
        });
    }

}