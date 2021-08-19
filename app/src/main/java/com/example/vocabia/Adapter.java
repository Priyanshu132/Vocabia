package com.example.vocabia;

import android.content.Context;
import android.graphics.Typeface;
import android.speech.tts.TextToSpeech;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;


public class Adapter extends RecyclerView.Adapter<Adapter.ViewHolder>  {

    private ArrayList<add_words> list;
    int i_=0;
    private RecyclerViewClickListner recyclerViewClickListner;
    Context context;
    My_Vocabulary m = new My_Vocabulary();

// checking
    public Adapter(ArrayList<add_words> list,Context context,RecyclerViewClickListner listner) {
        this.list = list;
        this.context=context;
        this.recyclerViewClickListner=listner;


    }
    @NonNull
    @Override
    public Adapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.list_layout,parent,false);
        return new Adapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull Adapter.ViewHolder holder, final int position) {


            holder.word.setText(list.get(position).getNewWord());
            holder.meaning.setText(list.get(position).getMeaning());
            String t = "\""+list.get(position).getExample() +"\"";
            holder.example.setText(t);

//            holder.linearLayout.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    row_index = position;
//                    notifyDataSetChanged();
//
//                }
//            });



//            if (row_index == position) {
//                holder.meaning.setTypeface(null, Typeface.BOLD);
//                holder.word.setTypeface(null, Typeface.BOLD);
//                holder.word.setTextSize(22);
//                holder.meaning.setTextSize(17);
//
//
//            } else {
//                holder.meaning.setTypeface(null, Typeface.NORMAL);
//                holder.word.setTypeface(null, Typeface.NORMAL);
//                holder.word.setTextSize(20);
//                holder.meaning.setTextSize(15);
//
//            }




    }



    @Override
    public int getItemCount() {


        return list.size();
    }






    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        TextView word, meaning,example;
        LinearLayout linearLayout;
        ImageView pronounciation;




        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            word = itemView.findViewById(R.id.word);
            meaning = itemView.findViewById(R.id.meaning);
            example = itemView.findViewById(R.id.exmaple);
            linearLayout = itemView.findViewById(R.id.bg);
            pronounciation = itemView.findViewById(R.id.pro);

            pronounciation.setOnClickListener(this);


        }

        @Override
        public void onClick(View view) {
            recyclerViewClickListner.onClick(view,getAdapterPosition());
        }
    }
    public interface RecyclerViewClickListner{
        void onClick(View v,int position);
    }

}
