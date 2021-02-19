package kr.ssc.front;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.ui.AppBarConfiguration;

import android.os.Bundle;
import android.view.WindowManager;

import kr.ssc.front.ui.login.LoginFragment;

public class LoginActivity extends AppCompatActivity {
    private AppBarConfiguration mAppBarConfiguration;
    private LoginFragment fragment;
    private OnBackPressedListener listener;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_open);
        setContentView(R.layout.activity_login);

        fragment = new LoginFragment();
        getSupportFragmentManager().beginTransaction().replace(R.id.container, fragment).commit();
    }

    public void setOnBackPressedListener(OnBackPressedListener listener){
        this.listener = listener;
    }

    // back버튼 클릭 처리
    @Override
    public void onBackPressed(){
        if(listener!=null) listener.onBackPressed();
        else super.onBackPressed();
    }
}