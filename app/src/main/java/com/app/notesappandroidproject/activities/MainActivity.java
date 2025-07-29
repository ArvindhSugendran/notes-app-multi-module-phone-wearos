package com.app.notesappandroidproject.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import com.app.notesappandroidproject.R;
import com.app.notesappandroidproject.databinding.ActivityMainBinding;
import com.app.notesappandroidproject.utils.IOBackPress;
import com.google.android.material.navigation.NavigationView;

/**
 * Activity for navigating the user to nav fragments.
 * Handles UI initialization, validation, and navigation to the next activity.
 */
public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{

    // Declare navController and binding of MainActivity
    private NavController navController;

    ActivityMainBinding mainBinding;

    View rootView = null;

    // Interface backPress by setting the listeners
    private IOBackPress backPress;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Inflating the layout using View Binding
        mainBinding = ActivityMainBinding.inflate(getLayoutInflater());
        rootView = mainBinding.getRoot();
        setContentView(rootView);

        navController = Navigation.findNavController(this, R.id.nav_host_fragment);

        // Set up navigation view with navController
        mainBinding.navigationView.setNavigationItemSelectedListener(this);
        NavigationUI.setupWithNavController(mainBinding.navigationView, navController);

        mainBinding.hamMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mainBinding.drawerLayout.openDrawer(GravityCompat.START);
            }
        });
    }


    // Navigation view on the left bar to handle navigation from Drawer layout to both the fragments
    // Note Fragment and RecycleBin Fragment
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        navController.navigate(item.getItemId());
        return true;
    }

    // function to update Hamburger Menu
    public void setHamburgerMenuVisibility(boolean visible) {
       mainBinding.hamMenu.setVisibility(visible ? View.VISIBLE : View.GONE);
    }

    // We have this listener to be set from both the fragments to override on Back press
    public void setOnBackPressListener(IOBackPress mOnBackPress) {
        this.backPress = mOnBackPress;
    }

    // Function to call backPress from both the fragments
    public void backPress() {
        onBackPressed();
    }


    // check if any of the components are opened in any of the fragments if so close the components first
    // if components are closed then close the application
    @Override
    public void onBackPressed() {
        if (mainBinding.drawerLayout.isOpen()) {
            mainBinding.drawerLayout.close();
        } else {
            boolean canBack = true;
            if (backPress != null)
                canBack = backPress.onBackPress();
            if (canBack) {
                super.onBackPressed();
                mainBinding.hamMenu.setVisibility(View.VISIBLE);
            }
        }
    }

}