package com.healbe.healbe_example_andorid.dashboard;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.healbe.healbe_example_andorid.R;
import com.healbe.healbe_example_andorid.connect.ConnectActivity;
import com.healbe.healbe_example_andorid.enter.EnterActivity;
import com.healbe.healbe_example_andorid.tools.BaseActivity;
import com.healbe.healbe_example_andorid.tools.SystemBarManager;
import com.healbe.healbesdk.business_api.HealbeSdk;

import androidx.appcompat.app.ActionBar;
import androidx.core.app.ActivityOptionsCompat;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import timber.log.Timber;

public class DashboardActivity extends BaseActivity {
    CompositeDisposable destroy = new CompositeDisposable();
    Menu mainMenu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        initPadding();
        ActionBar appBar = getSupportActionBar();

        if (appBar != null)
            appBar.setTitle(R.string.dashboard);

        // only dashboard for now
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.container, DashboardFragment.newInstance())
                .commit();
    }

    private void initPadding() {
        SystemBarManager tintManager = new SystemBarManager(this);
        SystemBarManager.SystemBarConfig config = tintManager.getConfig();
        findViewById(R.id.placeholder).setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, config.getPixelInsetTop(true)));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (menu != null && menu.size() != 0)
            menu.clear();
        getMenuInflater().inflate(R.menu.main_menu, menu);
        mainMenu = menu;

        updateMenu();
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.logout:
                logout();
                return true;
            case R.id.connect:
                connectOrDisconnect();
                return true;
            case R.id.find:
                findAnother();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void logout() {
        destroy.add(HealbeSdk.get().USER.logout() // logout disconnecting wb itself
                .subscribe(this::goEnter, Timber::e));
    }

    void updateMenu() {
        if (mainMenu != null) {
            boolean connectionActive = HealbeSdk.get().GOBE.isConnectionStarted();
            mainMenu.findItem(R.id.connect).setTitle(connectionActive ? R.string.disconnect : R.string.connect);
            mainMenu.findItem(R.id.find).setVisible(!connectionActive);
        }
    }

    private void connectOrDisconnect() {
        if (HealbeSdk.get().GOBE.isConnectionStarted())
            destroy.add(HealbeSdk.get().GOBE.disconnect()
                    .andThen(HealbeSdk.get().GOBE.setActive(false)) // for next time connect will start from search
                    .subscribe(this::updateMenu, Timber::e)); // stay here, just update menu
        else
            destroy.add(HealbeSdk.get().GOBE.connect()
                    .subscribe(this::updateMenu, Timber::e));
    }

    private void findAnother() {
        destroy.add(HealbeSdk.get().GOBE.disconnect()
                .andThen(HealbeSdk.get().GOBE.setActive(false)) // for next time connect will start from search
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::goConnect, Timber::e)); // go search
    }

    private void goEnter() {
        Bundle bundle = ActivityOptionsCompat.makeCustomAnimation(this,
                android.R.anim.fade_in, android.R.anim.fade_out).toBundle();
        startActivity(new Intent(this, EnterActivity.class), bundle);
        finish();
    }

    private void goConnect() {
        Bundle bundle = ActivityOptionsCompat.makeCustomAnimation(this,
                android.R.anim.fade_in, android.R.anim.fade_out).toBundle();
        startActivity(new Intent(this, ConnectActivity.class), bundle);
        finish();
    }
}
