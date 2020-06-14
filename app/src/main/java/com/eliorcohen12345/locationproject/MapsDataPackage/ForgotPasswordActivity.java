package com.eliorcohen12345.locationproject.MapsDataPackage;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.eliorcohen12345.locationproject.MainAndOtherPackage.EmailValidator;
import com.eliorcohen12345.locationproject.R;
import com.google.firebase.auth.FirebaseAuth;

public class ForgotPasswordActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "forgotSuccessful";
    private EditText editTextForgot;
    private Button btnForgot;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        initUI();
        initListeners();
    }

    private void initUI() {
        editTextForgot = findViewById(R.id.editTextForgot);
        btnForgot = findViewById(R.id.btnForgot);
    }

    private void initListeners() {
        btnForgot.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnForgot:
                String myEmailForgot = editTextForgot.getText().toString();
                if (!EmailValidator.getInstance().isValidEmail(myEmailForgot)) {
                    Toast.makeText(this, "Invalid email...", Toast.LENGTH_SHORT).show();
                } else {
                    FirebaseAuth.getInstance().sendPasswordResetEmail(myEmailForgot)
                            .addOnCompleteListener(task -> {
                                if (task.isSuccessful()) {
                                    Log.d(TAG, "The password sent");
                                    Toast.makeText(this, "The password sent", Toast.LENGTH_SHORT).show();
                                    startActivity(new Intent(ForgotPasswordActivity.this, LoginActivity.class));
                                } else {
                                    Toast.makeText(this, "The email doesn't exist", Toast.LENGTH_SHORT).show();
                                }
                            });
                }
                break;
        }
    }

}
