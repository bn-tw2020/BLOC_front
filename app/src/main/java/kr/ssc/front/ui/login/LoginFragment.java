package kr.ssc.front.ui.login;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.beautycoder.pflockscreen.PFFLockScreenConfiguration;
import com.beautycoder.pflockscreen.fragments.PFLockScreenFragment;
import com.beautycoder.pflockscreen.security.PFResult;
import com.beautycoder.pflockscreen.security.PFSecurityManager;
import com.beautycoder.pflockscreen.security.callbacks.PFPinCodeHelperCallback;

import kr.ssc.front.JoinActivity;
import kr.ssc.front.LoginActivity;
import kr.ssc.front.MainActivity;
import kr.ssc.front.OnBackPressedListener;
import kr.ssc.front.PreferencesSettings;
import kr.ssc.front.R;

public class LoginFragment extends Fragment implements OnBackPressedListener {
    private View root;

    private PFLockScreenFragment fragment;
    private PFFLockScreenConfiguration.Builder builder;

    private LoginActivity activity;
    private boolean first = true;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.fragment_password,container,false);

        activity = (LoginActivity) getActivity();

        fragment = new PFLockScreenFragment();

        builder = new PFFLockScreenConfiguration.Builder(getContext())
                .setMode(PFFLockScreenConfiguration.MODE_AUTH)
                .setTitle("로그인하기 위해서 비밀번호 6자리를 입력해주십시오")
                .setUseFingerprint(false)
                .setClearCodeOnError(true)
                .setCodeLength(6);

        // 핀 존재 여부
        PFSecurityManager.getInstance().getPinCodeHelper().isPinCodeEncryptionKeyExist(
                new PFPinCodeHelperCallback<Boolean>() {
                    @Override
                    public void onResult(PFResult<Boolean> result) {
                        if(result.getResult()){
                            // TODO:Exist
                        }
                        else{ // TODO: Doesn't exist
                            Intent intent = new Intent(getActivity(), JoinActivity.class);
                            startActivity(intent);
                        }
                    }
                }
        );
        fragment.setEncodedPinCode(PreferencesSettings.getCode(getContext()));

        fragment.setConfiguration(builder.build());
        fragment.setLoginListener(new PFLockScreenFragment.OnPFLockScreenLoginListener() {
            @Override
            public void onCodeInputSuccessful() {
                Toast.makeText(activity, "CodeInputSuccessful", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(getActivity(), MainActivity.class);
                startActivity(intent);
            }

            @Override
            public void onFingerprintSuccessful() {
                Toast.makeText(activity, "FingerprintSuccessful", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onPinLoginFailed() {
                Toast.makeText(activity, "비밀번호를 잘못 입력하였습니다.", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFingerprintLoginFailed() {
                Toast.makeText(activity, "Fingerprint failed", Toast.LENGTH_SHORT).show();
            }
        });
        getActivity().getSupportFragmentManager().beginTransaction()
                .replace(R.id.pincode_container, fragment).commit();

        return root;
    }

    @Override
    public void onResume() {
        super.onResume();
        activity.setOnBackPressedListener(this);
    }
    @Override
    public void onBackPressed() {
        if(first){
            first = false;
            Toast.makeText(getActivity(), "한번 더 누르면 앱이 종료됩니다.", Toast.LENGTH_SHORT).show();
        } else{
            activity.finishAffinity();
            System.runFinalization();
            System.exit(0);
        }
    }
}
