package com.example.vocabia;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Objects;

public class LoginPage extends AppCompatActivity{


    SignInButton sign;
    GoogleSignInClient m;
    String TAG = "LoginPage";
    FirebaseAuth firebaseAuth;
    int RC_SIGN_IN= 0;
    TextView forget_1;
    TextView signUp_1;
    EditText U_n;
    EditText pass;
    Button login;
    Bottom_forget_pass bottom_forget_pass;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_page);

        sign = findViewById(R.id.googleSignIn);
        firebaseAuth = FirebaseAuth.getInstance();
        login = findViewById(R.id.login);
        signUp_1 = findViewById(R.id.signUp);
        pass = findViewById(R.id.password);
        U_n = findViewById(R.id.username);
        bottom_forget_pass = new Bottom_forget_pass();
        forget_1 = findViewById(R.id.forget);
        forget_1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bottom_forget_pass.show(getSupportFragmentManager(),"TAG");
            }
        });

        signUp_1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginPage.this,SignUp.class);
                startActivity(intent);
            }
        });

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(U_n.getText().toString().isEmpty() || pass.getText().toString().isEmpty()){
                    Toast.makeText(LoginPage.this,"Some Fields are Empty",Toast.LENGTH_SHORT).show();

                }
                else {
                    final ProgressDialog progressDialog=new ProgressDialog(getApplicationContext());
                    progressDialog.setTitle("Signing");
                    progressDialog.setCanceledOnTouchOutside(false);
                    progressDialog.setMessage("Please Wait...");
                    progressDialog.show();
                    firebaseAuth.signInWithEmailAndPassword(U_n.getText().toString(),pass.getText().toString())
                            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if(task.isSuccessful()){
                                        progressDialog.dismiss();
                                        perma_word perma_word = new perma_word();
                                        perma_word.setLast_word("Last searched word");
                                        perma_word.setLast_meaning("Last word's meaning");
                                        perma_word.setLast_example("Example of last word-meaning");
                                        perma_word.setNewWord("Sample  (Word)");
                                        perma_word.setExample("Investigations involved ananlysing samples of handwriting  (Example)");
                                        perma_word.setMeaning("A small part or quantity intended to show what the whole is like  (Meaning)");
                                        FirebaseDatabase.getInstance().getReference().child("Total User").child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                                .child("permanant").setValue(perma_word);
                                        Intent intent = new Intent(LoginPage.this, My_Vocabulary.class);
                                        startActivity(intent);
                                    }
                                    else{
                                        Toast.makeText(LoginPage.this, Objects.requireNonNull(task.getException()).getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                }
            }
        });

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail().build();

        m = GoogleSignIn.getClient(this,gso);

        sign.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               signIn();

            }
        });


    }
    private void signIn(){
        Intent s = m.getSignInIntent();
        startActivityForResult(s,RC_SIGN_IN);

    }

    public void onActivityResult(int requestCode , int resultCode , Intent data){
        super.onActivityResult(requestCode,resultCode,data);

        if(requestCode == RC_SIGN_IN){


            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }
    }
    private void handleSignInResult(Task<GoogleSignInAccount> completeTask){
        try{
            GoogleSignInAccount acc = completeTask.getResult(ApiException.class);
            FirebaseGoogleAuth(acc);
        }catch (ApiException e){
            Toast.makeText(LoginPage.this,"Login Unsuccesfully",Toast.LENGTH_SHORT).show();
            FirebaseGoogleAuth(null);
        }
    }
    private void FirebaseGoogleAuth(GoogleSignInAccount acc){
        final ProgressDialog progressDialog=new ProgressDialog(this);
        progressDialog.setTitle("Signing");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setMessage("Please Wait...");
        progressDialog.show();

        AuthCredential authCredential = GoogleAuthProvider.getCredential(acc.getIdToken(),null);
        firebaseAuth.signInWithCredential(authCredential).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                    if(task.isSuccessful()){
                       // Toast.makeText(LoginPage.this,"Login Succesfully",Toast.LENGTH_SHORT).show();
                        progressDialog.dismiss();
                        perma_word perma_word = new perma_word();
                        perma_word.setLast_word("Last searched word");
                        perma_word.setLast_meaning("Last word's meaning");
                        perma_word.setLast_example("Example of last word-meaning");
                        perma_word.setNewWord("Sample  (Word)");
                        perma_word.setExample("Investigations involved ananlysing samples of handwriting  (Example)");
                        perma_word.setMeaning("A small part or quantity intended to show what the whole is like  (Meaning)");
                        FirebaseDatabase.getInstance().getReference().child("Total User").child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                .child("permanant").setValue(perma_word);
                        Intent intent = new Intent(LoginPage.this,My_Vocabulary.class);
                        startActivity(intent);

                    }else{
                        Toast.makeText(LoginPage.this,"Unsuccesfully",Toast.LENGTH_SHORT).show();


                    }
            }
        });

    }


    @Override
    protected void onStart() {
        super.onStart();

        GoogleSignInAccount user = GoogleSignIn.getLastSignedInAccount(this);
        FirebaseUser auth = firebaseAuth.getCurrentUser();
        if(user != null){
            Intent intent = new Intent(LoginPage.this,My_Vocabulary.class);
            startActivity(intent);
        }
        else if(auth != null){
            Intent intent = new Intent(LoginPage.this,My_Vocabulary.class);
            startActivity(intent);
        }


    }
}


