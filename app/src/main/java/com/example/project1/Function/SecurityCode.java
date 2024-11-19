package com.example.project1.Function;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.project1.R;

public class SecurityCode extends Fragment {
    private String username;

    public void setUsername(String username) {
        this.username = username;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.security_code, container, false);

        TextView securityMessage = view.findViewById(R.id.securityMessage);
        EditText edtSecurityCode1 = view.findViewById(R.id.edt_securityCode1);
        EditText edtSecurityCode2 = view.findViewById(R.id.edt_SecurityCode2);
        Button btnUpdateSecurity = view.findViewById(R.id.btn_updateSecurity);
        Button btnAddSecurity = view.findViewById(R.id.btn_addSecurity);

        return view;
    }
}
