package com.hackathon.recumeet.Adapter;

import android.content.Context;
import android.content.Intent;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.hackathon.recumeet.Models.User;
import com.hackathon.recumeet.R;
import com.hackathon.recumeet.UserProfile.ProfileActivity;
import com.squareup.picasso.Picasso;

import java.util.List;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.ViewHolder> {

    private final Context context;
    private final List<User> users;

    private FirebaseUser firebaseUser;


    public UserAdapter(Context context, List<User> users) {
        this.context = context;
        this.users = users;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.list_layout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        final User user = users.get(position);

        holder.btnFollow.setVisibility(View.VISIBLE);

        holder.fullName.setText(user.getFName() + " " + user.getLName());
        holder.bio.setText(user.getBio());

        Picasso.get().load(user.getProfileUri()).into(holder.profile_pic);

        isFollowing(user.getuId(), holder.btnFollow);

        holder.itemView.setOnClickListener(view -> {
            Intent intent = new Intent(context, ProfileActivity.class);
            intent.putExtra("publisherId", user.getuId());
            context.startActivity(intent);
        });

        holder.btnFollow.setOnClickListener(view -> {
            if (holder.btnFollow.getText().toString().equals("follow")) {
                FirebaseDatabase.getInstance().getReference().child("Follow").child(firebaseUser.getUid())
                        .child("following").child(user.getuId()).setValue(true);
                FirebaseDatabase.getInstance().getReference().child("Follow").child(user.getuId())
                        .child("followers").child(firebaseUser.getUid()).setValue(true);
            } else {
                FirebaseDatabase.getInstance().getReference().child("Follow").child(firebaseUser.getUid())
                        .child("following").child(user.getuId()).removeValue();
                FirebaseDatabase.getInstance().getReference().child("Follow").child(user.getuId())
                        .child("followers").child(firebaseUser.getUid()).removeValue();
            }
        });

    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public TextView bio;
        public TextView fullName;
        public ImageView profile_pic;
        public Button btnFollow;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            bio = itemView.findViewById(R.id.bio);
            fullName = itemView.findViewById(R.id.name);
            profile_pic = itemView.findViewById(R.id.profile_photo);
            btnFollow = itemView.findViewById(R.id.btn_follow);
        }
    }

    private void isFollowing(final String userid, final Button button) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("Follow").child(firebaseUser.getUid()).child("following");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.child(userid).exists()) {
                    button.setText("following");
                } else {
                    button.setText("follow");
                }
                button.setTextSize(TypedValue.COMPLEX_UNIT_SP ,11);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}
