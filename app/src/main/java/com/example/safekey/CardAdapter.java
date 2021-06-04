package com.example.safekey;

import android.annotation.SuppressLint;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.AnimatorRes;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import cn.pedant.SweetAlert.SweetAlertDialog;
import de.hdodenhof.circleimageview.CircleImageView;

public class CardAdapter extends RecyclerView.Adapter<CardAdapter.ViewHolder> {

    List<String> cardIconList = new ArrayList<String>(Arrays.asList("Amazon","Amazon Prime Videos","Brainly","Digilocker","Facebook","Flipkart","Gaana","Google","Hotstar","Instagram","Irctc","Jiosavan","Linkdin","Lpu Touch","Messanger","Microsoft Office","Netflix","Ola","Oneplus","Outlook","Paytm","Phone Pe","Punjab National Bank","State Bank of India","Teitter","Uber","Zee5","Other"));
    List<CardMaker> cardlist;
    Context context;
    int[] iconResource= new int[]{R.drawable.card_ic_amazon,R.drawable.card_ic_amazonprimevideo,R.drawable.card_ic_brainly,R.drawable.icon_card,R.drawable.card_ic_facebook,R.drawable.card_ic_flipkart,R.drawable.card_ic_gaana,R.drawable.card_ic_google,R.drawable.card_ic_hotstar,R.drawable.card_ic_instagram,R.drawable.card_ic_irctc,R.drawable.card_ic_jiosaavn,R.drawable.card_ic_linkedin,R.drawable.lpu,R.drawable.card_ic_messenger,R.drawable.card_ic_microsoftoffice,R.drawable.card_ic_netflix,R.drawable.card_ic_ola,R.drawable.card_ic_oneplus,R.drawable.card_ic_outlook,R.drawable.card_ic_paytm,R.drawable.card_ic_phonepe,R.drawable.card_ic_pnb,R.drawable.card_ic_sbi,R.drawable.icon_card,R.drawable.icon_card,R.drawable.card_ic_zee5,R.drawable.icon_card};
    public CardAdapter(List<CardMaker> cardlist, Context context) {
        this.cardlist = cardlist;
        this.context = context;
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.layout_card,parent,false);
        return new ViewHolder(view);

    }


    @SuppressLint("ResourceType")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        CardMaker obj = cardlist.get(position);
        holder.card_title.setText(obj.getCardName());
        holder.card_username.setText(obj.getUserName());
        holder.card_password.setText(obj.getUserPassword());
        int i=0;
        if(cardIconList.indexOf(obj.getCardName())!=-1)
            i =cardIconList.indexOf(obj.getCardName());
        else
            i = cardIconList.size();
        holder.card_icon.setImageResource(iconResource[i]);
        setAnimation((holder.itemView));
        holder.card_copy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ClipboardManager clipboardManager = (ClipboardManager)context.getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData data = (ClipData) ClipData.newPlainText("text",cardlist.get(position).getUserPassword());
                clipboardManager.setPrimaryClip(data);
                Toast.makeText(context,cardlist.get(position).getCardName()+" password copied" , Toast.LENGTH_SHORT).show();
            }
        });
        holder.card_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               DataStore dataStore = new DataStore(context);
               String deleting = cardlist.get(position).getCardName();
               if(dataStore.delete(deleting))
               {
                   Toast.makeText(context, deleting+" card delete successful \n Refresh your crad list", Toast.LENGTH_SHORT).show();
               }
               else
               {
                   Toast.makeText(context, "Something went wrong!", Toast.LENGTH_SHORT).show();
               }
            }
        });
        holder.card_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle b = new Bundle();
                b.putString("cardName", cardlist.get(position).getCardName());
                b.putString("userName", cardlist.get(position).getUserName());
                b.putString("userPassword", cardlist.get(position).getUserPassword());
                Intent i = new Intent(context, Edit_Card.class);
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                i.putExtras(b);
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(i);
            }
        });

    }
    private  void setAnimation(View view)
    {
        Animation animation = AnimationUtils.loadAnimation(context, android.R.anim.slide_in_left);
        view.setAnimation(animation);
    }
    @Override
    public int getItemCount() {
       return cardlist.size();
    }

    public class ViewHolder extends  RecyclerView.ViewHolder{


        TextView card_password,card_username,card_title;
        LinearLayout card_delete,card_edit,card_copy;
        CircleImageView card_icon;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            card_title = (TextView) itemView.findViewById(R.id.card_title);
            card_username = (TextView) itemView.findViewById(R.id.card_username);
            card_password = (TextView) itemView.findViewById(R.id.card_password);
            card_delete = (LinearLayout) itemView.findViewById(R.id.card_delete);
            card_edit = (LinearLayout) itemView.findViewById(R.id.card_edit);
            card_copy = (LinearLayout) itemView.findViewById(R.id.card_copy);
            card_icon = (CircleImageView) itemView.findViewById(R.id.card_icon);
        }
    }
}
