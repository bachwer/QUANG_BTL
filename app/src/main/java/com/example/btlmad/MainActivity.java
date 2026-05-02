package com.example.btlmad;

import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.btlmad.fragment.DocumentFragment;
import com.example.btlmad.fragment.DocumentTypeFragment;
import com.example.btlmad.fragment.ReportFragment;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

public class MainActivity extends AppCompatActivity {

    private BottomNavigationView bottomNavigationView;
    private MaterialToolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        bottomNavigationView = findViewById(R.id.bottomNavigationView);

        if (savedInstanceState == null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.container, new DocumentTypeFragment())
                    .commit();
            if (getSupportActionBar() != null)
                getSupportActionBar().setTitle(R.string.title_doc_type);
        }

        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Fragment selectedFragment = null;
                int itemId = item.getItemId();

                if (itemId == R.id.nav_doc_type) {
                    selectedFragment = new DocumentTypeFragment();
                    if (getSupportActionBar() != null)
                        getSupportActionBar().setTitle(R.string.title_doc_type);
                } else if (itemId == R.id.nav_document) {
                    selectedFragment = new DocumentFragment();
                    if (getSupportActionBar() != null)
                        getSupportActionBar().setTitle(R.string.title_document);
                } else if (itemId == R.id.nav_report) {
                    selectedFragment = new ReportFragment();
                    if (getSupportActionBar() != null)
                        getSupportActionBar().setTitle(R.string.title_report);
                }

                if (selectedFragment != null) {
                    getSupportFragmentManager()
                            .beginTransaction()
                            .replace(R.id.container, selectedFragment)
                            .commit();
                }
                return true;
            }
        });
    }
}
