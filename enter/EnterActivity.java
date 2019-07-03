package com.healbe.healbe_example_andorid.enter;

import android.content.Intent;
import android.os.Bundle;

import com.healbe.healbe_example_andorid.R;
import com.healbe.healbe_example_andorid.connect.ConnectActivity;
import com.healbe.healbe_example_andorid.tools.BaseActivity;
import com.healbe.healbe_example_andorid.tools.SystemBarManager;

import androidx.core.app.ActivityOptionsCompat;

public class EnterActivity extends BaseActivity implements LoginFragment.LoginListener{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_container);
        initPadding();

        // just one fragment for now but here we can route all "enter routine"
        // such as register and fill user profile data
        getSupportFragmentManager().beginTransaction()
                .add(R.id.container, LoginFragment.newInstance())
                .commit();
    }

    private void initPadding() {
        SystemBarManager tintManager = new SystemBarManager(this);
        SystemBarManager.SystemBarConfig config = tintManager.getConfig();
        findViewById(R.id.container).setPadding(0, config.getPixelInsetTop(false), config.getPixelInsetRight(), config.getPixelInsetBottom());
    }

    //TODO 5.登录成功连接设备
    @Override
    public void onLoginSucceeded() {
        Bundle bundle = ActivityOptionsCompat.makeCustomAnimation(this,
                android.R.anim.fade_in, android.R.anim.fade_out).toBundle();
        startActivity(new Intent(this, ConnectActivity.class), bundle);
        finish();
    }
}
