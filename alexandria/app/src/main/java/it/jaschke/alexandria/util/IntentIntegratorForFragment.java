package it.jaschke.alexandria.util;

import android.support.v4.app.Fragment;
import android.content.Intent;

import com.google.zxing.integration.android.IntentIntegrator;

public class IntentIntegratorForFragment extends IntentIntegrator {

    Fragment fragment;

    public IntentIntegratorForFragment(Fragment fragment) {
        super(fragment.getActivity());
        this.fragment = fragment;
    }

    @Override
    protected void startActivityForResult(Intent intent, int code) {
        fragment.startActivityForResult(intent, code);
    }
}
