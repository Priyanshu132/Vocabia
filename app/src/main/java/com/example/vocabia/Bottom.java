package com.example.vocabia;


import android.app.VoiceInteractor;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.Nullable;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import org.json.JSONArray;
import org.json.JSONObject;


public class Bottom extends BottomSheetDialogFragment {

    private EditText editText;
    private Button button;
    private FirebaseAuth auth;
    private add_words add_words;
    private RequestQueue queue;
    String message;
    private perma_word perma_word1;





    public Bottom(String m) {
        this.message = m;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.bottom_fragment, container, false);
        button = v.findViewById(R.id.add);
        editText = v.findViewById(R.id.new1);
        perma_word1 = new perma_word();
        add_words = new add_words();
        if(message != null){

            editText.setText(message);
        }
        queue = Volley.newRequestQueue(getContext());
        auth = FirebaseAuth.getInstance();
            button.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (editText.getText().toString().isEmpty()) {
                                Toast.makeText(getContext(), "Please Enter Word", Toast.LENGTH_SHORT).show();
                            }
                            else{
                                final String temp_word= editText.getText().toString().trim();;
//                                if(message != null){
//                                     temp_word = message;
//                                     editText.setText(message);
//                                }
//                                else{
//                                     temp_word =
//                                }


                                String url = "https://api.dictionaryapi.dev/api/v2/entries/en_US/"+temp_word;

                                final JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, url, null,
                                        new Response.Listener<JSONArray>() {
                                            @Override
                                            public void onResponse(JSONArray response) {
                                                for(int i=0; i< response.length();i++){
                                                    try{
                                                        JSONObject object = response.getJSONObject(i);
                                                        JSONArray array = object.getJSONArray("meanings");

                                                        JSONObject array1 = array.getJSONObject(0);
                                                        JSONArray array2 = array1.getJSONArray("definitions");
                                                        JSONObject object1 = array2.getJSONObject(0);
                                                        String temp_mea = object1.getString("definition");
                                                        String temp_exa;
                                                        try{
                                                            temp_exa = object1.getString("example");
                                                        }catch (Exception e){
                                                            temp_exa = "Not Found";
                                                        }



                                                        add_words.setMeaning(temp_mea.substring(0,1).toUpperCase()+temp_mea.substring(1));
                                                        add_words.setNewWord(temp_word.substring(0,1).toUpperCase()+temp_word.substring(1));
                                                        add_words.setExample(temp_exa.substring(0,1).toUpperCase()+temp_exa.substring(1));

                                                        perma_word1.setLast_example(temp_exa.substring(0,1).toUpperCase()+temp_exa.substring(1));
                                                        perma_word1.setLast_meaning(temp_mea.substring(0,1).toUpperCase()+temp_mea.substring(1));
                                                        perma_word1.setLast_word(temp_word.substring(0,1).toUpperCase()+temp_word.substring(1));
                                                        perma_word1.setNewWord("Sample");
                                                        perma_word1.setExample("Investigations involved ananlysing samples of handwriting");
                                                        perma_word1.setMeaning("A small part or quantity intended to show what the whole is like");

                                                        FirebaseDatabase.getInstance().getReference().child("Total User").child(auth.getCurrentUser().getUid())
                                                                .child(temp_word.substring(0,1).toUpperCase()+temp_word.substring(1)).setValue(add_words);

                                                        FirebaseDatabase.getInstance().getReference().child("Total User").child(auth.getCurrentUser().getUid())
                                                                .child("permanant").setValue(perma_word1);
                                                       // textView.setText(object1.get("definition").toString());

                                                    }catch (Exception e){

                                                        e.printStackTrace();
                                                    }

                                                }

                                            }
                                        }, new Response.ErrorListener() {
                                    @Override
                                    public void onErrorResponse(VolleyError error) {
                                  //
                                        error.printStackTrace();

                                    }
                                });
                                queue.add(request);
                                Toast.makeText(getContext(), "Word Added Successfully", Toast.LENGTH_SHORT).show();

                                   // flag = 1;

                                   //
                                  //  flag = 0;




                            }
                            dismiss();
                        }
                    });

          //  }
     //   });











        return v;
    }




}


