package com.example.skillsquared2.ui.inbox;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import com.example.skillsquared2.R;

public class ChatListFragment extends Fragment {

    private ChatListModel chatListModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        chatListModel = ViewModelProviders.of(this).get(ChatListModel.class);
        View root = inflater.inflate(R.layout.fragment_chat_list, container, false);

        return root;
    }
}