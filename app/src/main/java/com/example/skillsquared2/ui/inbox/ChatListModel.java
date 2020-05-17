package com.example.skillsquared2.ui.inbox;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class ChatListModel extends ViewModel {

    private MutableLiveData<String> mText;

    public ChatListModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is inbox fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}