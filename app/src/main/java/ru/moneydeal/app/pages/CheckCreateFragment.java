package ru.moneydeal.app.pages;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

public class CheckCreateFragment extends Fragment {
    private static String GROUP_ID = "GROUP_ID";

    public static CheckCreateFragment getInstance(String groupId) {
        Bundle bundle = new Bundle();
        bundle.putString(GROUP_ID, groupId);
        CheckCreateFragment fragment = new CheckCreateFragment();
        fragment.setArguments(bundle);
        return fragment;
    }
}
