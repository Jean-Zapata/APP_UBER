package com.zrcarlos.uber_clone;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class AuthActivity extends AppCompatActivity {

    TextInputEditText txtEmail;
    TextInputEditText txtPass;
    Button btnIngresar;

    FirebaseAuth firebaseAuth;
    DatabaseReference mDatabase;
    Toolbar mToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_auth);

        // Configuración del Toolbar
        mToolbar = findViewById(R.id.toolbar);
        if (mToolbar != null) {
            setSupportActionBar(mToolbar);
        }

        // Verifica que getSupportActionBar() no sea null antes de usarlo
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Login");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        txtEmail = findViewById(R.id.txtemail);
        txtPass = findViewById(R.id.txtpass);
        btnIngresar = findViewById(R.id.btnIngresar);

        firebaseAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        btnIngresar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Login();
            }
        });
    }

    private void Login() {
        String email = txtEmail.getText().toString().trim();
        String pass = txtPass.getText().toString().trim();

        // Limpiar errores previos
        txtEmail.setError(null);
        txtPass.setError(null);

        // Validar campos
        if (!validateFields(email, pass)) {
            return; // Detener si hay errores
        }

        // Intentar autenticación con Firebase
        firebaseAuth.signInWithEmailAndPassword(email, pass)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            showToast("Inicio de sesión exitoso");
                        } else {
                            handleFirebaseError(task.getException());
                        }
                    }
                });
    }

    /**
     * Valida los campos de email y contraseña.
     *
     * @param email El email ingresado.
     * @param pass  La contraseña ingresada.
     * @return `true` si los campos son válidos, `false` si hay errores.
     */
    private boolean validateFields(String email, String pass) {
        boolean isValid = true;

        // Validar email
        if (TextUtils.isEmpty(email)) {
            txtEmail.setError(getString(R.string.error_email_empty));
            isValid = false;
        } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            txtEmail.setError(getString(R.string.error_email_invalid));
            isValid = false;
        }

        // Validar contraseña
        if (TextUtils.isEmpty(pass)) {
            txtPass.setError(getString(R.string.error_password_empty));
            isValid = false;
        } else if (pass.length() < 6) {
            txtPass.setError(getString(R.string.error_password_short));
            isValid = false;
        }

        // Mostrar un Toast si ambos campos están vacíos
        if (TextUtils.isEmpty(email) && TextUtils.isEmpty(pass)) {
            showToast(getString(R.string.error_email_password_empty));
        }

        return isValid;
    }

    /**
     * Maneja los errores de Firebase Auth.
     *
     * @param exception La excepción generada por Firebase.
     */
    private void handleFirebaseError(Exception exception) {
        if (exception instanceof FirebaseAuthInvalidCredentialsException) {
            txtPass.setError(getString(R.string.error_password_incorrect));
            txtPass.requestFocus();
        } else if (exception instanceof FirebaseAuthInvalidUserException) {
            txtEmail.setError(getString(R.string.error_email_not_found));
            txtEmail.requestFocus();
        } else {
            showToast(getString(R.string.error_login_failed));
        }
    }

    /**
     * Muestra un Toast con un mensaje.
     *
     * @param message El mensaje a mostrar.
     */
    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

}