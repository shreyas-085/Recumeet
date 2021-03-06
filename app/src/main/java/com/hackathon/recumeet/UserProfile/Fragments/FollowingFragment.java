package com.hackathon.recumeet.UserProfile.Fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.hackathon.recumeet.Adapter.UserAdapter;
import com.hackathon.recumeet.Models.User;
import com.hackathon.recumeet.R;

import java.util.ArrayList;
import java.util.List;

public class FollowingFragment extends Fragment {

    private final String UId;

    private UserAdapter userAdapter;
    private List<User> users;

    View view;

    public FollowingFragment(String UId) {
        this.UId = UId;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_following, container, false);
        RecyclerView following = view.findViewById(R.id.following);

        users = new ArrayList<>();

        LinearLayoutManager llm = new LinearLayoutManager(getContext());
        following.setLayoutManager(llm);

        userAdapter = new UserAdapter(getContext(),users);
        following.setAdapter(userAdapter);

        final DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Follow").child(UId).child("following");
        ref.keepSynced(true);

        final DatabaseReference ref2 = FirebaseDatabase.getInstance().getReference().child("users");
        ref2.keepSynced(true);

        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                users.clear();
                for (DataSnapshot snapshot1 : snapshot.getChildren()){
                    final String userId = snapshot1.getKey();
                    ref2.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            for (DataSnapshot snapshot2 : snapshot.getChildren()){
                                User user = snapshot2.getValue(User.class);
                                assert user != null;
                                if (user.getuId().equals(userId)){
                                    users.add(user);
                                }
                            }
                            userAdapter.notifyDataSetChanged();
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            Toast.makeText(getContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
        return view;
    }
}