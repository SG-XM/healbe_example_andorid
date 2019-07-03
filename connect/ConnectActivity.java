package com.healbe.healbe_example_andorid.connect;

import android.content.Intent;
import android.os.Bundle;
import com.healbe.healbe_example_andorid.R;
import com.healbe.healbe_example_andorid.connect.fragments.ConnectFragment;
import com.healbe.healbe_example_andorid.connect.fragments.EnterPinFragment;
import com.healbe.healbe_example_andorid.connect.fragments.ErrorFragment;
import com.healbe.healbe_example_andorid.connect.fragments.SearchFragment;
import com.healbe.healbe_example_andorid.connect.fragments.SetupPinFragment;
import com.healbe.healbe_example_andorid.dashboard.DashboardActivity;
import com.healbe.healbe_example_andorid.enter.EnterActivity;
import com.healbe.healbe_example_andorid.tools.BaseActivity;
import com.healbe.healbe_example_andorid.tools.SystemBarManager;
import com.healbe.healbesdk.business_api.HealbeSdk;
import com.healbe.healbesdk.business_api.user_storage.entity.HealbeDevice;
import com.healbe.healbesdk.utils.Supplier;

import java.util.HashMap;

import androidx.core.app.ActivityOptionsCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import timber.log.Timber;

public class ConnectActivity extends BaseActivity implements ConnectRouter {
    private CompositeDisposable destroy = new CompositeDisposable();

    //fragment routing map
    private HashMap<ConnectRouter.State, Supplier<Fragment>> stateMap
            = new HashMap<ConnectRouter.State, Supplier<Fragment>>() {{
        put(State.SEARCH, SearchFragment::newInstance);
        put(State.CONNECT, ConnectFragment::newInstance);
        put(State.ERROR, ErrorFragment::newInstance);
        put(State.ENTER_PIN, EnterPinFragment::newInstance);
        put(State.SETUP_PIN, SetupPinFragment::newInstance);
    }};

    //TODO 6.检查Healbe-GoBe-get()-HealbeDevice，是否需要搜寻设备
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_container);
        initPadding();

        // try to get default wristband
        destroy.add(HealbeSdk.get().GOBE.get()
                .observeOn(AndroidSchedulers.mainThread())
                .onErrorReturnItem(new HealbeDevice())
                .subscribe(healbeGoBe -> goState(
                        healbeGoBe.isActive()  // if we have an active wristband (user has entered|set correct pin in past)
                                ? ConnectRouter.State.CONNECT
                                : ConnectRouter.State.SEARCH, false),
                        Timber::e));
    }

    private void initPadding() {
        SystemBarManager tintManager = new SystemBarManager(this);
        SystemBarManager.SystemBarConfig config = tintManager.getConfig();
        findViewById(R.id.container).setPadding(0, config.getPixelInsetTop(false), config.getPixelInsetRight(), config.getPixelInsetBottom());
    }

    //TODO 7.根据state及stateMap，进入对应的Fragment，如搜索设备state：SEARCH——SearchFragment
    @SuppressWarnings("ConstantConditions")
    @Override
    public void goState(ConnectRouter.State state, boolean back) {
        Fragment f = getSupportFragmentManager().findFragmentById(R.id.container);

        if (f != null && f.getTag() != null && f.getTag().equals(state.toString()))
            return;

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

        if (f != null)
            transaction.setCustomAnimations(
                    back ? R.anim.enter_from_left : R.anim.enter_from_right,
                    back ? R.anim.exit_to_right : R.anim.exit_to_left);

        // note what we don't need to save history here for back-navigation, it's boring
        // this way we have own predictable routing
        transaction.replace(R.id.container, stateMap.get(state).get(), state.toString()).commit();
    }

    @Override
    public void logout() {
        destroy.add(HealbeSdk.get().USER.logout()
                .subscribeOn(AndroidSchedulers.mainThread())
                .subscribe(this::goEnter, Timber::e));

    }

    private void goEnter() {
        Bundle bundle = ActivityOptionsCompat.makeCustomAnimation(this,
                android.R.anim.fade_in, android.R.anim.fade_out).toBundle();
        startActivity(new Intent(this, EnterActivity.class), bundle);
        finish();
    }

    @Override
    public void connected() {
        goDashboard();
    }

    private void goDashboard() {
        Bundle bundle = ActivityOptionsCompat.makeCustomAnimation(this,
                android.R.anim.fade_in, android.R.anim.fade_out).toBundle();
        startActivity(new Intent(this, DashboardActivity.class), bundle);
        finish();
    }

    @Override
    public void onBackPressed() {
        FragmentManager man = getSupportFragmentManager();
        Fragment frag = man.findFragmentById(R.id.container);

        // check if we consume on back in fragment
        if (frag instanceof BackNavigationConsumer
                && ((BackNavigationConsumer) frag).consumeBackPressed())
            return;

        super.onBackPressed();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        destroy.clear();
    }


}
