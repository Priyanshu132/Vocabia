package com.example.vocabia;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


public class Profile extends AppCompatActivity {


     Button SignOut;
     ImageView imageView;
     TextView name;
    GoogleSignInClient m;
     TextView email;
    TextView t_w;

     String size;
     DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail().build();

        m = GoogleSignIn.getClient(this,gso);
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("My Profile");
        toolbar.setTitleTextColor(Color.WHITE);
        SignOut = findViewById(R.id.logout);
        imageView = findViewById(R.id.pic);
        name = findViewById(R.id.name);
        email = findViewById(R.id.email);
        databaseReference  = FirebaseDatabase.getInstance().getReference();

        t_w = findViewById(R.id.totalnumber);

        size = getIntent().getStringExtra("size");
        t_w.setText(size);

        final GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
        if(account != null){
            name.setText(account.getDisplayName());
            email.setText(account.getEmail());
            Glide.with(this).load(account.getPhotoUrl()).into(imageView);
        }
        else{

            databaseReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    String name_1 = dataSnapshot.child(FirebaseAuth.getInstance().getUid()).child("Person details").child("name").getValue().toString();
                    String email_1 = dataSnapshot.child(FirebaseAuth.getInstance().getUid()).child("Person details").child("email").getValue().toString();
                    name.setText(name_1);
                    email.setText(email_1);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }

        SignOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                m.signOut().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Toast.makeText(Profile.this,"Logout Successfully",Toast.LENGTH_SHORT).show();
                        Intent intent=new Intent(getApplicationContext(),LoginPage.class);
                        startActivity(intent);
                    }
                });
                FirebaseAuth.getInstance().signOut();
            }
        });

    }

}
