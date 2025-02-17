package com.zrcarlos.uber_clone;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class LoginActivity extends AppCompatActivity {

    private Toolbar mToolbar;
    private Button mAuthButton;
    private Button mRegisterButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);

        // Verifica que el ID 'main' exista antes de aplicarle los insets
        if (findViewById(R.id.main) != null) {
            ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
                Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
                v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
                return insets;
            });
        }

        // Configuración del Toolbar
        mToolbar = findViewById(R.id.toolbar);
        if (mToolbar != null) {
            setSupportActionBar(mToolbar);
        }

        // Verifica que getSupportActionBar() no sea null antes de usarlo
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Seleccionar Opción");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        // Configuración del botón de autenticación
        mAuthButton = findViewById(R.id.btnAuth);
        if (mAuthButton != null) {
            mAuthButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    goToAuth();
                }
            });
        }

        // Configuración del botón de registro
        mRegisterButton = findViewById(R.id.btnRegister);
        if (mRegisterButton != null) {
            mRegisterButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    goToRegister();
                }
            });
        }
    }

    private void goToAuth() {
        Intent intent = new Intent(this, AuthActivity.class);
        startActivity(intent);
    }

    private void goToRegister() {
        Intent intent = new Intent(this, RegisterActivity.class);
        startActivity(intent);
    }
}
