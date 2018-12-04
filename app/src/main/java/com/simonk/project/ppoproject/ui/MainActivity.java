package com.simonk.project.ppoproject.ui;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;
import androidx.navigation.NavController;
import androidx.navigation.NavDestination;
import androidx.navigation.NavOptions;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;
import android.widget.Toolbar;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.simonk.project.ppoproject.R;
import com.simonk.project.ppoproject.databinding.ActivityMainBinding;

import java.util.List;

public class MainActivity extends BindingActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        resolveIntent(getIntent());

        final BottomNavigationView bottomNavigationView = getBinding().bottomNavigation;

        final NavController navController =
                Navigation.findNavController(this, R.id.nav_host_fragment);
        navController.addOnNavigatedListener(new NavController.OnNavigatedListener() {
            @Override
            public void onNavigated(@NonNull NavController controller, @NonNull NavDestination destination) {
                bottomNavigationView.setSelectedItemId(destination.getId());
            }
        });
        
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                int current = navController.getCurrentDestination().getId();

                switch (menuItem.getItemId()) {
                    case R.id.account_fragment:
                        if (current == R.id.first_blank_fragment) {
                            navController.navigate(R.id.action_first_blank_to_account);
                        }
                        if (current == R.id.second_blank_fragment) {
                            navController.navigate(R.id.action_second_blank_to_account);
                        }
                        break;
                    case R.id.first_blank_fragment:
                        if (current == R.id.account_fragment) {
                            navController.navigate(R.id.action_account_to_first_blank);
                        }
                        if (current == R.id.second_blank_fragment) {
                            navController.navigate(R.id.action_second_blank_to_first_blank);
                        }
                        break;
                    case R.id.second_blank_fragment:
                        if (current == R.id.first_blank_fragment) {
                            navController.navigate(R.id.action_first_blank_to_second_blank);
                        }
                        if (current == R.id.account_fragment) {
                            navController.navigate(R.id.action_account_to_second_blank);
                        }
                        break;
                }
                return true;
            }
        });
    }

    @Override
    protected ViewDataBinding initBinding() {
        return DataBindingUtil.inflate(LayoutInflater.from(this), R.layout.activity_main, null, false);
    }

    @Override
    public ActivityMainBinding getBinding() {
        return (ActivityMainBinding) super.getBinding();
    }

    private void resolveIntent(Intent intent) {
        if (intent == null || intent.getAction() == null) {
            return;
        }

        if (!intent.getAction().equals(Intent.ACTION_VIEW)) {
            return;
        }

        Uri data = intent.getData();
        if (data == null) {
            return;
        }

        List<String> path = data.getPathSegments();
        if (path.size() != 2) {
            throw new IllegalArgumentException();
        }

        int page;
        try {
            page = Integer.parseInt(path.get(path.size() - 1));
        } catch (NumberFormatException ignore) {
            throw new IllegalArgumentException();
        }

        final NavController navController =
                Navigation.findNavController(this, R.id.nav_host_fragment);
        switch (page) {
            case 1:
                navController.navigate(R.id.account_fragment);
                break;
            case 2:
                navController.navigate(R.id.first_blank_fragment);
                break;
            case 3:
                navController.navigate(R.id.second_blank_fragment);
                break;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case R.id.about:
                final NavController navController =
                        Navigation.findNavController(this, R.id.nav_host_fragment);
                navController.navigate(R.id.action_account_to_about);

                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

}
