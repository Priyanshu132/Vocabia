package com.example.vocabia;

    import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.app.AppCompatActivity;


import android.app.ProgressDialog;
import android.app.VoiceInteractor;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.media.AudioAttributes;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.print.PrintAttributes;
import android.print.PrintDocumentAdapter;
import android.print.PrintJob;
import android.print.PrintManager;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator;

public class My_Vocabulary extends AppCompatActivity {

    private ArrayList<add_words> list;
    GoogleSignInClient m;
    private RecyclerView recyclerView;
    add_words add_words;
    SearchView searchView;
    private TextView noresult;
    private TextView new_word;
    private TextView new_mean;
    private TextView new_exam;
    int size;
    private ImageView voice;
    Bottom bottom;
    private TextToSpeech speech;
    private AdView adView1;
    private Adapter.RecyclerViewClickListner listner;
    private RequestQueue queue;
    private LinearLayout linearLayout;

    private InterstitialAd mInterstitialAd;
//  free api   -->  https://api.dictionaryapi.dev/api/v2/entries/en_US/


//"ca-app-pub-7109795074062636~2492711486"

    private DatabaseReference databaseReference;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my__vocabulary);

        mInterstitialAd = new InterstitialAd(this);
        mInterstitialAd.setAdUnitId("ca-app-pub-7109795074062636/6386764007");
        mInterstitialAd.loadAd(new AdRequest.Builder().build());
        queue = Volley.newRequestQueue(this);
        new_exam = findViewById(R.id.exmaple);
        new_mean = findViewById(R.id.meaning);
        new_word = findViewById(R.id.word);
        linearLayout = findViewById(R.id.perma);



        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail().build();

        searchView = findViewById(R.id.search);
        searchView.setIconifiedByDefault(false);
        voice = findViewById(R.id.voice);

        noresult = findViewById(R.id.noresult);
        voice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
                intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                        RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
                intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
                intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Speak");

                try {
                    startActivityForResult(intent, 1);
                } catch (ActivityNotFoundException e) {
                    Toast.makeText(My_Vocabulary.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
        m = GoogleSignIn.getClient(this, gso);
        add_words = new add_words();
        bottom = new Bottom(null);
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("My Vocabulary");
        toolbar.setTitleTextColor(Color.WHITE);
        toolbar.setSubtitleTextColor(Color.WHITE);
        toolbar.inflateMenu(R.menu.main_menu);
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if (item.getItemId() == R.id.profile) {
                    Intent intent = new Intent(getApplicationContext(), Profile.class);
                    intent.putExtra("size", String.valueOf(size));
                    startActivity(intent);
                }
                if (item.getItemId() == R.id.signOut) {
                    m.signOut()
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    Toast.makeText(My_Vocabulary.this, "Logout Successfully", Toast.LENGTH_SHORT).show();

                                    Intent intent = new Intent(My_Vocabulary.this, LoginPage.class);
                                    startActivity(intent);
                                    finish();
                                }
                            });
                    FirebaseAuth.getInstance().signOut();


                }


                if (item.getItemId() == R.id.add) {

                    if (mInterstitialAd.isLoaded()) {
                        mInterstitialAd.show();
                    } else {

                            Bottom bottomSheet = new Bottom(null);
                            bottomSheet.show(getSupportFragmentManager(), "TAG");

                    }

                    mInterstitialAd.setAdListener(new AdListener() {
                        @Override
                        public void onAdClosed() {


                                Bottom bottomSheet = new Bottom(null);
                                bottomSheet.show(getSupportFragmentManager(), "TAG");

                            mInterstitialAd.loadAd(new AdRequest.Builder().build());
                        }

                    });

                }

                return false;
            }
        });

        recyclerView = findViewById(R.id.recycle_list);
        databaseReference = FirebaseDatabase.getInstance().getReference().child("Total User")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid());

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleCallback);
        itemTouchHelper.attachToRecyclerView(recyclerView);

        speech = new TextToSpeech(this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if(status == TextToSpeech.SUCCESS){
                    int res = speech.setLanguage(Locale.ENGLISH);

                    if(res == TextToSpeech.LANG_NOT_SUPPORTED || res == TextToSpeech.LANG_MISSING_DATA){
                        Toast.makeText(My_Vocabulary.this,"Language not supported",Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
                Log.d("yyu","");
            }
        });

        adView1 = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        adView1.loadAd(adRequest);

    }
    public void speak(String word){
        speech.speak(word,TextToSpeech.QUEUE_FLUSH,null);
    }
    ItemTouchHelper.SimpleCallback simpleCallback = new ItemTouchHelper.SimpleCallback(0,ItemTouchHelper.RIGHT | ItemTouchHelper.LEFT) {
        @Override
        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
            return false;
        }

        @Override
        public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {

            final int pos = viewHolder.getAdapterPosition();
            switch (direction){

                case ItemTouchHelper.RIGHT:
                    final String delete_word = list.get(pos).getNewWord();
                    final  String delete_me = list.get(pos).getMeaning();
                    Query query = databaseReference.orderByChild("newWord").equalTo(delete_word);
                    query.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull final DataSnapshot dataSnapshot) {
                            for(DataSnapshot ds: dataSnapshot.getChildren()){
                                ds.getRef().removeValue();

                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });


                    Snackbar.make(recyclerView, delete_word + " Archived. ",Snackbar.LENGTH_LONG)
                            .setAction("Undo", new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    add_words.setNewWord(delete_word);
                                    add_words.setMeaning(delete_me);

                                    databaseReference.child(delete_word).setValue(add_words);

                                }
                            }).show();


                    break;

                case ItemTouchHelper.LEFT:

                    final  String share = "Word: "+list.get(pos).getNewWord()+";; "+"Meaning: "+list.get(pos).getMeaning();

                    Intent myIn = new Intent(Intent.ACTION_SEND);
                    myIn.setType("text/plain");
                    myIn.putExtra(Intent.EXTRA_TEXT,share);
                    myIn.putExtra(Intent.EXTRA_SUBJECT,"Word/Meaning");

                    startActivity(Intent.createChooser(myIn,"Share Using"));
            }

        }

        @Override
        public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder,
                                float dX, float dY, int actionState, boolean isCurrentlyActive) {

            new RecyclerViewSwipeDecorator.Builder(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
                    .addSwipeRightActionIcon(R.drawable.ic_delete_black_24dp)
                    .addSwipeRightBackgroundColor(ContextCompat.getColor(My_Vocabulary.this,R.color.delete))
                    .addSwipeLeftActionIcon(R.drawable.ic_share_black_24dp)
                    .addSwipeLeftBackgroundColor(ContextCompat.getColor(My_Vocabulary.this,R.color.delete))
                    .create()
                    .decorate();

            super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
        }
    };



    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode){

            case 1:
                if(resultCode == RESULT_OK && data != null) {
                    ArrayList<String> res = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    searchView.setQuery(res.get(0),false);                                              // new added
                    search(res.get(0));
                }
        }
    }

    @Override
    protected void onStart() {
        super.onStart();

        Intent intent = getIntent();
        final String te = String.valueOf(intent.getStringExtra(Intent.EXTRA_TEXT));
        if("null" != String.valueOf(intent.getStringExtra(Intent.EXTRA_TEXT))) {
            String action = intent.getAction();
            String type = intent.getType();
            //Log.d("tag  ",type);
            if (Intent.ACTION_SEND.equals(action) && type != null) {
                if (type.equalsIgnoreCase("text/plain")) {
                    Bottom bottomSheet = new Bottom(String.valueOf(intent.getStringExtra(Intent.EXTRA_TEXT)));
                    bottomSheet.show(getSupportFragmentManager(), "TAG");
                }
            }

        }
        if(databaseReference != null){
            databaseReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

//                    if (!dataSnapshot.exists()) {
//                        perma_word perma_word = new perma_word();
//                        perma_word.setLast_word("Last searched word");
//                        perma_word.setLast_meaning("Last word's meaning");
//                        perma_word.setLast_example("Example of last word-meaning");
//                        perma_word.setNewWord("Sample");
//                        perma_word.setExample("Investigations involved ananlysing samples of handwriting");
//                        perma_word.setMeaning("A small part or quantity intended to show what the whole is like");
//                        databaseReference.child("permanant").setValue(perma_word);
//
//                    }
                    new_exam.setText(dataSnapshot.child("permanant").child("last_example").getValue().toString());
                    new_mean.setText(dataSnapshot.child("permanant").child("last_meaning").getValue().toString());
                    new_word.setText(dataSnapshot.child("permanant").child("last_word").getValue().toString());
                    linearLayout.setVisibility(View.VISIBLE);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
           //
        }




        if (databaseReference != null) {
            databaseReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {

                        list = new ArrayList<>();
                        for (DataSnapshot ds : dataSnapshot.getChildren()) {

                            list.add(ds.getValue(add_words.class));


                        }
                        size = list.size();
                        if(size >0){
                            searchView.setVisibility(View.VISIBLE);

                        }
//                        for(add_words s: list){
//                            if(s.getNewWord().equals(te)){
//
//                            }
//                        }
                        Adapter adapter = new Adapter(list, getApplicationContext(),listner);
                        recyclerView.setAdapter(adapter);


                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Toast.makeText(My_Vocabulary.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }


        if(searchView!=null){
                        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                            @Override
                            public boolean onQueryTextSubmit(String query) {
                                return false;
                            }

                            @Override
                            public boolean onQueryTextChange(String newText) {
                                search(newText);

                                return true;
                            }
                        });
                    }

        listner = new Adapter.RecyclerViewClickListner() {
            @Override
            public void onClick(View v, int position) {

             //   Toast.makeText(getApplicationContext(),"done",Toast.LENGTH_SHORT).show();
                String word = list.get(position).getNewWord();
                String url = "https://api.dictionaryapi.dev/api/v2/entries/en_US/"+word;
                JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, url, null,
                        new Response.Listener<JSONArray>() {
                            @Override
                            public void onResponse(JSONArray response) {


                                    try{
                                        JSONObject object = response.getJSONObject(0);
                                        JSONArray array = object.getJSONArray("phonetics");
                                        JSONObject array1 = array.getJSONObject(0);

                                        String url = array1.getString("audio");
                                        MediaPlayer mediaPlayer = new MediaPlayer();
                                        mediaPlayer.setAudioAttributes(
                                                new AudioAttributes.Builder()
                                                        .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                                                        .setUsage(AudioAttributes.USAGE_MEDIA)
                                                        .build()
                                        );
                                        mediaPlayer.setDataSource(url);
                                        mediaPlayer.prepare(); // might take long! (for buffering, etc)
                                        mediaPlayer.start();


                                    }catch (Exception e){

                                    }


                            }
                        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                });
                queue.add(jsonArrayRequest);
            }
        };
    }


    private void search(String str){

        ArrayList<add_words> mylist=new ArrayList<>();
        for(add_words obj: list){

            if(obj.getNewWord().toLowerCase().contains(str.toLowerCase()) || obj.getMeaning().toLowerCase().contains(str.toLowerCase())){
                mylist.add(obj);
            }
        }
        if(mylist.isEmpty()){
            noresult.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.INVISIBLE);
        }
        else {
            noresult.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);

        }
        Adapter adapter=new Adapter(mylist,getApplicationContext(),listner);
        recyclerView.setAdapter(adapter);
    }


}
