package com.example.beefclassifier;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

public class MyBoardFragment extends Fragment {
    private RecyclerView recyclerView;
    private FirebaseAuth auth;
    private FirebaseRecyclerAdapter<Post, MyPostViewHolder> adapter;
    private FirebaseRecyclerOptions<Post> options;
    private FirebaseDatabase database ;
    private Query query;
    private DatabaseReference ref;
    private ImageButton imageButton;
    public MyBoardFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_my_board, container, false);
        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        ref = database.getReference("Board").child("user-post");

        query = ref.child(auth.getCurrentUser().getUid()).limitToFirst(100);
        options = new FirebaseRecyclerOptions.Builder<Post>() //어떤데이터를 어디서갖고올거며 어떠한 형태의 데이터클래스 결과를 반환할거냐 옵션을 정의한다.
                .setQuery(query, Post.class)
                .build();
        recyclerView=view.findViewById(R.id.RecylerView2);
        adapter = new  FirebaseRecyclerAdapter<Post, MyPostViewHolder>(options){
            @Override
            protected void onBindViewHolder(@NonNull MyPostViewHolder holder, int position, @NonNull Post model) {

                holder.titleView.setText(model.getTitle());
                holder.WriteDayView.setText(model.getDate());
                holder.WriterNameView.setText(model.getWriterName());

                final DatabaseReference postRef = getRef(position);

                // Set click listener for the whole post view
                final String postKey = postRef.getKey();

                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // Launch PostDetailActivity
                        Intent intent =new Intent(getActivity(), PostDetailActivity.class);
                        intent.putExtra("postkey", postKey);
                        startActivity(intent);
                    }
                });

                holder.imageButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        TextView t=(TextView)getActivity().findViewById(R.id.UserName);
                        PopupMenu popupMenu =  new PopupMenu(getContext(), v);
                        MenuInflater inflater1 = getActivity().getMenuInflater();
                        inflater1.inflate(R.menu.popup_menu,popupMenu.getMenu());
                        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                            @Override
                            public boolean onMenuItemClick(MenuItem item) {
                                switch (item.getItemId()){
                                    case R.id.modify:
                                        Intent intent = new Intent(getActivity(), WriteActivity.class);
                                        intent.putExtra("Username",t.getText().toString());
                                        intent.putExtra("postkey",postKey);
                                        startActivity(intent);
                                        return true;
                                    case R.id.remove:
                                        ref.child(auth.getCurrentUser().getUid()).child(postKey).removeValue();
                                        database.getReference("Board").child("post").child(postKey).removeValue();
                                        return true;

                                }
                                return false;
                            }
                        });
                        popupMenu.show();
                    }
                });

            }
            @NonNull
            @Override
            public MyPostViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.item2, parent, false);
                return new MyPostViewHolder(view);
            }

        };
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity()); //레이아웃매니저 생성
        layoutManager.setReverseLayout(true);
        layoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(layoutManager); ////만든 레이아웃매니저 객체를(설정을) 리사이클러 뷰에 설정해줌
        recyclerView.setAdapter(adapter);
        recyclerView.addItemDecoration(new DividerItemDecoration(view.getContext(), DividerItemDecoration.VERTICAL));

        imageButton = view.findViewById(R.id.iconbtn);

        return view;
    }
    @Override
    public void onStart() {
        super.onStart();
        if (adapter != null) {
            adapter.startListening();
        }


    }
    @Override
    public void onResume() {
        super.onResume();
        query = ref.child(auth.getCurrentUser().getUid()).limitToFirst(100);
        options = new FirebaseRecyclerOptions.Builder<Post>() //어떤데이터를 어디서갖고올거며 어떠한 형태의 데이터클래스 결과를 반환할거냐 옵션을 정의한다.
                .setQuery(query, Post.class)
                .build();
        adapter.updateOptions(options);
        adapter.notifyDataSetChanged();
    }
    @Override
    public void onStop() {
        super.onStop();
        if (adapter != null) {
            adapter.stopListening();
        }
    }
}