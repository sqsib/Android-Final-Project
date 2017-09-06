package com.example.sib.finalproject.fragments;

/**
 * Created by Rameen Barish on 4/29/2017.
 */

import android.os.Bundle;
import android.support.v4.app.Fragment;
import com.example.sib.finalproject.interfaces.RetainedFragmentInteraction;

public class TaskFragment extends Fragment implements RetainedFragmentInteraction {

    public static final String TAG_TASK_FRAGMENT = "task_fragment";
    private String mActiveFragmentTag;

    public static TaskFragment newInstance() {
        TaskFragment fragment = new TaskFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    public TaskFragment() {}

    @Override
    public void onResume() { super.onResume(); }

    public String getActiveFragmentTag() { return mActiveFragmentTag; }

    public void setActiveFragmentTag(String s) { mActiveFragmentTag = s; }
}