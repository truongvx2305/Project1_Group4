package com.example.project1.Function.Management;

import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.project1.Adapter.EmployeeAdapter;
import com.example.project1.Dao.UserDao;
import com.example.project1.DB.DatabaseHelper;
import com.example.project1.Model.UserModel;
import com.example.project1.R;

import java.util.List;

public class Employee extends Fragment {
    private String username;

    public void setUsername(String username) {
        this.username = username;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.employee, container, false);

        // Kết nối cơ sở dữ liệu
        DatabaseHelper dbHelper = new DatabaseHelper(getContext());
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        UserDao userDao = new UserDao(db);

        // Lấy danh sách nhân viên từ database
        List<UserModel> employeeList = userDao.getAllEmployees();

        // Gán adapter cho ListView
        ListView listView = view.findViewById(R.id.listEmployee);
        EmployeeAdapter adapter = new EmployeeAdapter(getContext(), employeeList);
        listView.setAdapter(adapter);

        return view;
    }
}
