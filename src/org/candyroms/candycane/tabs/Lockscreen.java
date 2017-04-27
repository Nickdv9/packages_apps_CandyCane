/*
 * Copyright (C) 2014-2016 The Dirty Unicorns Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
 
package org.candyroms.candycane.tabs;
 
import android.content.Context;
import android.content.ContentResolver;
import android.os.Bundle;
import android.support.v14.preference.SwitchPreference;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceScreen;
import android.provider.Settings;
 
import android.hardware.fingerprint.FingerprintManager;

import com.android.settings.R;
import com.android.settings.SettingsPreferenceFragment;
import com.android.internal.logging.MetricsProto.MetricsEvent;
import com.android.internal.util.candy.CandyUtils;

import org.candyroms.candycane.preference.SystemSettingSwitchPreference;

public class Lockscreen extends SettingsPreferenceFragment implements
        Preference.OnPreferenceChangeListener {

    private static final String KEYGUARD_TOGGLE_TORCH = "keyguard_toggle_torch";
    private static final String FP_UNLOCK_KEYSTORE = "fp_unlock_keystore";

    private SwitchPreference mKeyguardTorch;
    private SystemSettingSwitchPreference mFpKeystore;

    private FingerprintManager mFingerprintManager;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
 
        addPreferencesFromResource(R.xml.lockscreen);
        PreferenceScreen prefSet = getPreferenceScreen();

        ContentResolver resolver = getActivity().getContentResolver();

        mKeyguardTorch = (SwitchPreference) findPreference(KEYGUARD_TOGGLE_TORCH);
        mKeyguardTorch.setOnPreferenceChangeListener(this);
        if (!CandyUtils.deviceSupportsFlashLight(getActivity())) {
            prefSet.removePreference(mKeyguardTorch);
        } else {
            mKeyguardTorch.setChecked((Settings.System.getInt(resolver,
                    Settings.System.KEYGUARD_TOGGLE_TORCH, 0) == 1));
        }

        mFingerprintManager = (FingerprintManager) getActivity().getSystemService(Context.FINGERPRINT_SERVICE);
        mFpKeystore = (SystemSettingSwitchPreference) findPreference(FP_UNLOCK_KEYSTORE);
        if (!mFingerprintManager.isHardwareDetected()){
            prefSet.removePreference(mFpKeystore);
        } else {
            mFpKeystore.setChecked((Settings.System.getInt(getContentResolver(),
                    Settings.System.FP_UNLOCK_KEYSTORE, 0) == 1));
            mFpKeystore.setOnPreferenceChangeListener(this);
        }
    }
 
    @Override
    protected int getMetricsCategory() {
        return MetricsEvent.CANDYCANE;
    }
 
    @Override
    public void onResume() {
        super.onResume();
    }
 
    @Override
    public void onPause() {
        super.onPause();
    }

    public boolean onPreferenceChange(Preference preference, Object objValue) {
        if  (preference == mKeyguardTorch) {
            boolean checked = ((SwitchPreference)preference).isChecked();
            Settings.System.putInt(getActivity().getContentResolver(),
                    Settings.System.KEYGUARD_TOGGLE_TORCH, checked ? 1:0);
            return true;
        } else if (preference == mFpKeystore) {
            boolean value = (Boolean) objValue;
            Settings.System.putInt(getActivity().getContentResolver(),
                    Settings.System.FP_UNLOCK_KEYSTORE, value ? 1 : 0);
            return true;
        }
        return false;
    }
}
