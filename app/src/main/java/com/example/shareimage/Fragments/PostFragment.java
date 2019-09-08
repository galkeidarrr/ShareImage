package com.example.shareimage.Fragments;


import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.shareimage.Adapters.PostAdapter;
import com.example.shareimage.Models.PostModel;
import com.example.shareimage.Models.Repository;
import com.example.shareimage.R;

import java.util.ArrayList;
import java.util.List;

import static android.content.Context.MODE_PRIVATE;


/**
 * A simple {@link Fragment} subclass.
 */
public class PostFragment extends Fragment {
    private static final String TAG = "PostFragment";
    String postid;


    ImageView closeBtn;
    private RecyclerView recyclerView;
    private PostAdapter postAdapter;
    private List<PostModel> postList;
    Repository repository;

    public PostFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view=inflater.inflate(R.layout.fragment_post, container, false);

        SharedPreferences prefs = getContext().getSharedPreferences("PREFS", MODE_PRIVATE);
        postid = prefs.getString("postid", "none");
        //save the variables
        recyclerView = view.findViewById(R.id.post_recycler_view);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(mLayoutManager);

        postList = new ArrayList<>();
        postAdapter = new PostAdapter(getContext(), postList);
        recyclerView.setAdapter(postAdapter);
        closeBtn=view.findViewById(R.id.post_close);

        repository.instance.getPost(postid, new Repository.GetPostListener() {
            @Override
            public void onComplete(PostModel postModel) {
                if(postModel!=null){
                    postList.clear();
                    postList.add(postModel);
                    postAdapter = new PostAdapter(getContext(), postList);
                    recyclerView.setAdapter(postAdapter);
                }
            }
        });

        closeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Navigation.findNavController(v)
                        .popBackStack();
            }
        });

        return view;
    }

}
