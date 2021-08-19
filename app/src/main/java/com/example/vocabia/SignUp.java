package com.example.vocabia;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Objects;

public class SignUp extends AppCompatActivity {

    private TextView login;
    private EditText name;
    private EditText pass;
    private EditText Cpass;
    private EditText email;
    private DatabaseReference databaseReference;
    private Button sign;
    private SignUp_details signUp;
    private FirebaseAuth firebaseAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        login = findViewById(R.id.login1);
        name = findViewById(R.id.username);
        pass = findViewById(R.id.password);
        email = findViewById(R.id.password1);
        Cpass = findViewById(R.id.cpassword);
        sign = findViewById(R.id.login);
        signUp = new SignUp_details();
        firebaseAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference();

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SignUp.this,LoginPage.class);
                startActivity(intent);
            }
        });

        sign.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(name.getText().toString().isEmpty() || email.getText().toString().isEmpty() || pass.getText().toString().isEmpty()||
                Cpass.getText().toString().isEmpty()){
                    Toast.makeText(SignUp.this,"Some Fields are Empty",Toast.LENGTH_SHORT).show();
                }
                else if(pass.getText().toString().length() < 8 ){
                    int size = pass.getText().toString().length();
                    String temp = "Add "+(size-8)+" Characters More";
                    Toast.makeText(SignUp.this,temp,Toast.LENGTH_LONG).show();
                }
                else if(pass.getText().toString().contentEquals(Cpass.getText().toString())){

                    firebaseAuth.createUserWithEmailAndPassword(email.getText().toString(),pass.getText().toString())
                            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if(task.isSuccessful()){
                                        signUp.setName(name.getText().toString().trim());
                                        signUp.setEmail(email.getText().toString().trim());

                                        databaseReference.child(firebaseAuth.getCurrentUser().getUid())
                                                .child("Person details").setValue(signUp);
                                        Toast.makeText(SignUp.this,"Account Created",Toast.LENGTH_SHORT).show();
                                        Intent intent = new Intent(SignUp.this,LoginPage.class);
                                        startActivity(intent);

                                    } else {
                                        Toast.makeText(SignUp.this, Objects.requireNonNull(task.getException()).getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                }else {
                    Toast.makeText(SignUp.this, "Confirm Password is not Same", Toast.LENGTH_LONG).show();
                }
            }
        });

    }
}
