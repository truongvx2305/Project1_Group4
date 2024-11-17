package com.example.project1;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.project1.DB.DatabaseHelper;
import com.example.project1.Dao.UserDao;
import com.example.project1.Function.Login;
import com.example.project1.Function.Profile;
import com.google.android.material.navigation.NavigationView;

public class Navigation extends AppCompatActivity {
    private DatabaseHelper dbHelper;
    private SQLiteDatabase db;
    private UserDao userDao;
    private SharedPreferences sharedPreferences;
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private Fragment fragment;
    private String username;
    private TextView toolbarTitle;

    private static final String PREF_NAME = "user_prefs";
    private static final String KEY_USERNAME = "username";
    private static final String KEY_IS_LOGGED_IN = "is_logged_in";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.navigation);

        // Khởi tạo SharedPreferences
        sharedPreferences = getSharedPreferences(PREF_NAME, MODE_PRIVATE);

        // Khởi tạo DatabaseHelper và UserDao
        dbHelper = new DatabaseHelper(this);
        db = dbHelper.getWritableDatabase();
        userDao = new UserDao(db);

        // Lấy thông tin người dùng từ SharedPreferences
        username = sharedPreferences.getString(KEY_USERNAME, null);

        // Khởi tạo các thành phần của giao diện
        toolbarTitle = findViewById(R.id.toolbar_title);
        drawerLayout = findViewById(R.id.navigationLayout);
        setUpToolbar();

        // Hiển thị tên người dùng
        navigationView = findViewById(R.id.navigation_view);
        if (navigationView != null) {
            setupNavigationHeader();
            setUpNavigationView();
        }
    }

    private void setUpToolbar() {
        ImageButton drawerToggleButton = findViewById(R.id.action_toolbar);
        if (drawerToggleButton != null) {
            drawerToggleButton.setOnClickListener(v -> {
                if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
                    drawerLayout.closeDrawer(GravityCompat.START);
                } else {
                    drawerLayout.openDrawer(GravityCompat.START);
                }
            });
        }
    }

    // Hàm xử lý việc gán username khi đăng nhập vào header của NavigationView
    private void setupNavigationHeader() {
        View view = navigationView.getHeaderView(0);
        TextView showUsername = view.findViewById(R.id.showUsername);
        if (username != null) {
            showUsername.setText(username);
        } else {
            Toast.makeText(this, "Vui lòng đăng nhập lại!", Toast.LENGTH_SHORT).show();
            logout();
        }
    }

    private void setUpNavigationView() {
        navigationView.setNavigationItemSelectedListener(this::onNavigationItemSelected);
    }

    private boolean onNavigationItemSelected(MenuItem menuItem) {
        drawerLayout.closeDrawer(GravityCompat.START);

        fragment = null;
        String title = "";

        int itemId = menuItem.getItemId();

        if (itemId == R.id.item_profile) {
            fragment = new Profile();
            ((Profile) fragment).setUsername(username);
            title = "Thông tin cá nhân";
        } else if (itemId == R.id.item_logout) {
            logout();
            return true;
        } else {
            return false;
        }
        if (fragment != null) {
            loadFragment(fragment, title);
            updateNavigationViewSelection(fragment);
        }
        return true;
    }

    private void loadFragment(Fragment fragment, String title) {
        Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.fragment_view);
        if (currentFragment != null && currentFragment.getClass().equals(fragment.getClass())) {
            return; // Không thay thế nếu fragment hiện tại giống với fragment mới
        }
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_view, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
        toolbarTitle.setText(title);
    }

    private void updateNavigationViewSelection(Fragment fragment) {
        MenuItem item = null;

        if (fragment instanceof Profile) {
            item = navigationView.getMenu().findItem(R.id.item_profile);
        }

        if (item != null) {
            item.setChecked(true);
        }
    }

    private void logout() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(KEY_IS_LOGGED_IN, false);
        editor.apply();

        // Chuyển đến màn hình đăng nhập
        Toast.makeText(this, "Đăng xuất thành công!", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(Navigation.this, Login.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (dbHelper != null) dbHelper.close();
        if (db != null && db.isOpen()) db.close();
    }
}
