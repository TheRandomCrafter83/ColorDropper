package com.coderzf1.colordropper;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class StateModel extends ViewModel {
    private MutableLiveData<SaveState> mSaveState;
    public void setSaveState(SaveState saveState){
        mSaveState.setValue(saveState);
    }
    public LiveData<SaveState> getSaveState(){
        if (mSaveState == null){
            mSaveState = new MutableLiveData<SaveState>();
        }
        return mSaveState;
    }
}
