package com.zrcarlos.uber_clone;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.zrcarlos.uber_clone.models.User;

public class RegisterActivity extends AppCompatActivity {

    SharedPreferences mPrefe;
    FirebaseAuth mAuth;
    DatabaseReference mDatabase;

    // Views
    Button btnRegister;
    TextInputEditText txtName;
    TextInputEditText txtEmail;
    TextInputEditText txtPass;

    private static final String TAG = "RegisterActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_register);

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        // Inicializar vistas
        txtName = findViewById(R.id.txtname);
        txtEmail = findViewById(R.id.txtemail);
        txtPass = findViewById(R.id.txtpass);
        btnRegister = findViewById(R.id.btnRegister);

        mPrefe = getApplicationContext().getSharedPreferences("typeUser", MODE_PRIVATE);
        String selectedUser = mPrefe.getString("User", "");
        Toast.makeText(this, "El valor que se seleccionó es: " + selectedUser, Toast.LENGTH_SHORT).show();

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                RegisterUser();
            }
        });
    }

    private void RegisterUser() {
        final String name = txtName.getText().toString().trim();
        final String email = txtEmail.getText().toString().trim();
        final String pass = txtPass.getText().toString().trim();

        // Validaciones
        if (name.isEmpty() || email.isEmpty() || pass.isEmpty()) {
            Toast.makeText(this, "Debe llenar todos los campos", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(this, "Correo inválido. Debe contener @ y un dominio válido", Toast.LENGTH_SHORT).show();
            return;
        }

        if (pass.length() < 6) {
            Toast.makeText(this, "La contraseña debe tener al menos 6 caracteres", Toast.LENGTH_SHORT).show();
            return;
        }

        // Mostrar progreso
        btnRegister.setEnabled(false);
        Toast.makeText(RegisterActivity.this, "Procesando registro...", Toast.LENGTH_SHORT).show();

        mAuth.createUserWithEmailAndPassword(email, pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    String id = mAuth.getCurrentUser().getUid();
                    saveUser(id, name, email);
                } else {
                    btnRegister.setEnabled(true);
                    String errorMessage = "No se pudo registrar el usuario";
                    try {
                        throw task.getException();
                    } catch (FirebaseAuthException e) {
                        switch (e.getErrorCode()) {
                            case "ERROR_EMAIL_ALREADY_IN_USE":
                                errorMessage = "El correo ya está registrado";
                                break;
                            case "ERROR_INVALID_EMAIL":
                                errorMessage = "Correo inválido. Verifique el formato";
                                break;
                            case "ERROR_WEAK_PASSWORD":
                                errorMessage = "Contraseña demasiado débil";
                                break;
                            default:
                                errorMessage = "Error: " + e.getMessage();
                                break;
                        }
                    } catch (Exception e) {
                        errorMessage = "Error desconocido: " + e.getMessage();
                    }
                    Log.e(TAG, "Error de autenticación: " + errorMessage);
                    Toast.makeText(RegisterActivity.this, errorMessage, Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private void saveUser(String id, String name, String email) {
        String selectedUser = mPrefe.getString("User", "");
        User user = new User();
        user.setId(id);
        user.setName(name);
        user.setEmail(email);

        String path = selectedUser.equals("Driver") ? "Drivers" : "Clients";
        mDatabase.child("Users").child(path).child(id).setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(RegisterActivity.this, "Registro exitoso", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                } else {
                    btnRegister.setEnabled(true);
                    String errorMessage = "Error al guardar usuario: ";
                    if (task.getException() != null) {
                        errorMessage += task.getException().getMessage();
                        Log.e(TAG, "Error completo: ", task.getException());
                    }
                    Toast.makeText(RegisterActivity.this, errorMessage, Toast.LENGTH_LONG).show();
                }
            }
        });
    }
}