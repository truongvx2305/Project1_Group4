package com.example.project1;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.project1.DB.DatabaseHelper;
import com.example.project1.Dao.UserDao;
import com.example.project1.Function.Home;
import com.example.project1.Function.Login;
import com.example.project1.Function.Management.Employee;
import com.example.project1.Function.Profile;
import com.example.project1.Function.SecurityCode;
import com.example.project1.Model.UserModel;
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

        // Kiểm tra trạng thái đăng nhập
        if (!sharedPreferences.getBoolean(KEY_IS_LOGGED_IN, false)) {
            logout();
        } else {
            username = sharedPreferences.getString(KEY_USERNAME, null);
        }

        // Log trạng thái đăng nhập
        Log.d("Navigation", "Username: " + username);
        Log.d("Navigation", "Is Logged In: " + sharedPreferences.getBoolean(KEY_IS_LOGGED_IN, false));

        // Khởi tạo DatabaseHelper và UserDao
        dbHelper = new DatabaseHelper(this);
        db = dbHelper.getWritableDatabase();
        userDao = new UserDao(db);

        // Thiết lập giao diện
        toolbarTitle = findViewById(R.id.toolbar_title);
        drawerLayout = findViewById(R.id.navigationLayout);
        navigationView = findViewById(R.id.navigation_view);

        setUpToolbar();
        if (navigationView != null) {
            setupNavigationHeader();
            checkRole();
            setUpNavigationView();
        }

        navigateToHome();
    }

    // Mở màn hình Home sau khi đăng nhập thành công
    private void navigateToHome() {
        Home homeFragment = new Home();
        homeFragment.setUsername(username);
        loadFragment(homeFragment, "Trang chủ");

        // Đặt trạng thái được chọn cho mục "Trang chủ"
        MenuItem homeItem = navigationView.getMenu().findItem(R.id.item_home);
        if (homeItem != null) {
            homeItem.setChecked(true);
        }
    }

    // Thiết lập toolbar và sự kiện click mở navigationView
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

    // Hển thị ảnh và tên người dùng trên header navigation
    @SuppressLint("SetTextI18n")
    public void setupNavigationHeader() {
        View view = navigationView.getHeaderView(0);
        TextView showUsername = view.findViewById(R.id.showUsername);
        ImageView imageHeaderNavigation = view.findViewById(R.id.imageHeaderNavigation);

        // Lấy ảnh từ cơ sở dữ liệu
        byte[] imageBytes = userDao.getProfileImage(username);
        if (imageBytes == null || imageBytes.length == 0) {
            imageHeaderNavigation.setImageResource(R.drawable.user1); // Ảnh mặc định
        } else {
            Bitmap bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
            imageHeaderNavigation.setImageBitmap(bitmap);
        }

        // Hiển thị username
        if (username != null) {
            showUsername.setText("Chào " + username + ",");
        } else {
            Toast.makeText(this, "Vui lòng đăng nhập lại!", Toast.LENGTH_SHORT).show();
            logout();
        }
    }

    // Kiểm tra vai trò người dùng và ẩn các chức năng không được dùng phía nhân viên
    private void checkRole() {
        if (username == null) {
            Log.e("Navigation", "Username không tồn tại trong SharedPreferences!");
            return;
        }

        Log.d("Navigation", "Kiểm tra vai trò cho username: " + username);

        UserModel user = userDao.getProfileByUsername(username);
        if (user == null) {
            Log.e("Navigation", "Không tìm thấy thông tin người dùng trong database cho username: " + username);
            return;
        }

        if (!user.isAdmin()) {
            Log.d("Navigation", "Người dùng không phải là admin. Ẩn các mục dành cho admin.");
            int[] restrictedItems = {
                    R.id.item_view_product,
                    R.id.item_product_management,
                    R.id.item_voucher_management,
                    R.id.item_employee_management,
                    R.id.item_report_statistics
            };
            for (int itemId : restrictedItems) {
                MenuItem item = navigationView.getMenu().findItem(itemId);
                if (item != null) {
                    item.setVisible(false);
                } else {
                    Log.e("Navigation", "Không tìm thấy mục: " + itemId);
                }
            }
        } else {
            Log.d("Navigation", "Người dùng là admin. Hiển thị đầy đủ quyền.");
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

        if (itemId == R.id.item_home) {
            fragment = new Home();
            ((Home) fragment).setUsername(username);
            title = "Trang chủ";
        } else if (itemId == R.id.item_employee_management) {
            fragment = new Employee();
            title = "Quản lý nhân viên";
        } else if (itemId == R.id.item_profile) {
            fragment = new Profile();
            ((Profile) fragment).setUsername(username);
            title = "Thông tin cá nhân";
        } else if (itemId == R.id.item_security) {
            fragment = new SecurityCode();
            ((SecurityCode) fragment).setUsername(username);
            title = "Khóa bảo mật";
        }else if (itemId == R.id.item_logout) {
            logout();
            return true;
        } else {
            return false;
        }
        if (fragment != null) {
            // Đánh dấu mục đã chọn
            menuItem.setChecked(true);
            loadFragment(fragment, title);
        }
        return true;
    }

    private void loadFragment(Fragment fragment, String title) {
        Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.fragment_view);
        if (currentFragment != null && currentFragment.getClass().equals(fragment.getClass())) {
            toolbarTitle.setText(title);
            return;
        }
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_view, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
        toolbarTitle.setText(title);
    }

    private void logout() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear(); // Xóa toàn bộ dữ liệu trong SharedPreferences
        editor.apply();

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
