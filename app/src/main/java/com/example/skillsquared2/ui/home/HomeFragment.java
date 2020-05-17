package com.example.skillsquared2.ui.home;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.skillsquared2.R;
import com.example.skillsquared2.Adapters.RecyclerViewAdapter;

import java.util.ArrayList;

public class HomeFragment extends Fragment {

    private HomeViewModel homeViewModel;


    private RecyclerView firstRecyclerView;
    private RecyclerView secondRecyclerView;
    private View root;


    private ArrayList<String> mNames = new ArrayList<>();
    private ArrayList<String> mImageUrls = new ArrayList<>();

    private ArrayList<String> gNames = new ArrayList<>();
    private ArrayList<String> gImageUrls = new ArrayList<>();

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        homeViewModel = ViewModelProviders.of(this).get(HomeViewModel.class);
        root = inflater.inflate(R.layout.fragment_home, container, false);

        loadPopularServices();
        loadFeaturedGigs();

        homeViewModel.getText().observe(this, new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                // textView.setText(s);
            }
        });
        return root;
    }

   /* @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.main, menu);
        MenuItem menuItem = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) menuItem.getActionView();
        searchView.setQueryHint("Type here to search");

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
        super.onCreateOptionsMenu(menu, inflater);
    }*/

   /* @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.action_search:
                Toast.makeText(getContext(), "search icon clicked", Toast.LENGTH_SHORT).show();
                return true;
            case R.id.action_settings:
            case R.id.action_dashboard:
            case R.id.action_view_profile:
                default:
                    return super.onOptionsItemSelected(item);
        }

    }*/

    private void loadPopularServices() {
        Log.d("GetImages called: ", "Initiallising Arrays");

        mImageUrls.add("raw/software.jpg");
        mNames.add("Software Engineer");

        mImageUrls.add("https://unsplash.com/photos/bs2Ba7t69mM");
        mNames.add("Web Developer");

        mImageUrls.add("drawable/website2.jpg");
        mNames.add("Android Developer");

        mImageUrls.add("raw/writer.jpg");
        mNames.add("Blog Writer");

        mImageUrls.add("raw/graphics.jpg");
        mNames.add("Graphic Designer");

        mImageUrls.add("raw/video.jpg");
        mNames.add("Video Editing");

        //========================================================================================

        initFirstRecyclerView();
        //initSecondRecyclerView();
    }

    private void loadFeaturedGigs() {
        gImageUrls.add("g");
        gNames.add("Graphic Design");

        gImageUrls.add("g");
        gNames.add("Graphic Design");

        gImageUrls.add("g");
        gNames.add("Graphic Design");

        gImageUrls.add("g");
        gNames.add("Graphic Design");

        gImageUrls.add("g");
        gNames.add("Graphic Design");
        initSecondRecyclerView();
    }

    private void initFirstRecyclerView() {

        Log.d("initRecyclerView: ", "init Recycler View");

        LinearLayoutManager layoutManager = new LinearLayoutManager(this.getContext(), LinearLayoutManager.HORIZONTAL, false);
        try {
            firstRecyclerView = root.findViewById(R.id.first_recycler_View);
            firstRecyclerView.setLayoutManager(layoutManager);
            RecyclerViewAdapter adapter = new RecyclerViewAdapter(mNames, mImageUrls, this.getContext());
            firstRecyclerView.setAdapter(adapter);
        } catch (Exception e) {
            System.out.println("ERROR:  " + e);
        }
    }

    private void initSecondRecyclerView() {

        Log.d("initRecyclerView: ", "init  Second Recycler View");

        LinearLayoutManager layoutManager = new LinearLayoutManager(this.getContext(), LinearLayoutManager.HORIZONTAL, false);
        try {
            secondRecyclerView = root.findViewById(R.id.second_recycler_View);
            secondRecyclerView.setLayoutManager(layoutManager);
            RecyclerViewAdapter adapter = new RecyclerViewAdapter(gNames, gImageUrls, this.getContext());
            secondRecyclerView.setAdapter(adapter);
        } catch (Exception e) {
            System.out.println("ERROR:  " + e);
        }
    }


}