package com.example.ea_reminder;

import android.app.Activity;
import android.content.Context;
import android.text.Editable;
import android.text.method.KeyListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class RecyclerBoardSettingAdapter extends RecyclerView.Adapter<RecyclerBoardSettingAdapter.BoardSettingViewHolder> {

    static ArrayList<RecyclerBoardSetting> rbslist;
    Context context;
    ArrayList<bSetOnClickListener> bsocl;
    static boolean editingone=false;
    //View maintopbar=null;
    BoardSetting bs;

    public RecyclerBoardSettingAdapter(ArrayList<RecyclerBoardSetting> rbslist, Context context){
        this.rbslist = rbslist;
        this.context = context;
        this.bs =null;
        bsocl = new ArrayList<>();
    }
    public RecyclerBoardSettingAdapter(ArrayList<RecyclerBoardSetting> rbslist, Context context, BoardSetting bs){
        this.rbslist = rbslist;
        this.context = context;
        this.bs =bs;
        bsocl = new ArrayList<>();
    }

    public  void alertadapter(){
        for(int i=0;i<bsocl.size();i++){
            bsocl.get(i).forceKeyListen();
        }
        bsocl.clear();
        if(bs!=null)bs.setres();
        this.notifyDataSetChanged();
    }
    @NonNull
    @Override
    public BoardSettingViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_boardsetting, parent, false);
        RecyclerBoardSettingAdapter.BoardSettingViewHolder bvh = new RecyclerBoardSettingAdapter.BoardSettingViewHolder(view);
        return bvh;
    }

    @Override
    public void onBindViewHolder(@NonNull BoardSettingViewHolder holder, final int position) {
        holder.recbs_name.setText(rbslist.get(position).name);
        holder.recbs_time.setText(rbslist.get(position).time);

        holder.recbs_b_del.setOnClickListener(new View.OnClickListener() {//삭제 버튼
            @Override
            public void onClick(View view) {
                BoardDBOpenHelper bdboh = new BoardDBOpenHelper(context);
                MainBoard.delalarm((int)rbslist.get(position).id,context);
                bdboh=bdboh.open();
                bdboh.create();
                bdboh.deletecol(rbslist.get(position).id);
                rbslist.remove(position);
                alertadapter();
                bdboh.close();
            }
        });
        bSetOnClickListener templistener = new bSetOnClickListener(holder.recbs_name, holder.recbs_time, context,rbslist.get(position));
        bsocl.add(templistener);
        holder.recbs_b_set.setOnClickListener(templistener);

        /*
        if(position==0){
            if(maintopbar==null){
                maintopbar=holder.recbs_topbar;
                maintopbar.setVisibility(View.VISIBLE);
            }
        }*/
    }

    @Override
    public int getItemCount() {
        return rbslist.size();
    }

    public class BoardSettingViewHolder extends RecyclerView.ViewHolder {
        EditText recbs_name;
        EditText recbs_time;
        ImageButton recbs_b_del;
        ImageButton recbs_b_set;
        View recbs_topbar;
        public BoardSettingViewHolder(@NonNull View itemView) {
            super(itemView);
            this.recbs_name = itemView.findViewById(R.id.recbs_name);
            this.recbs_time = itemView.findViewById(R.id.recbs_time);
            this.recbs_b_del = itemView.findViewById(R.id.recbs_b_del);
            this.recbs_b_set = itemView.findViewById(R.id.recbs_b_set);
            this.recbs_topbar = itemView.findViewById(R.id.recbs_topbar);
        }
    }

    public class bSetOnClickListener implements View.OnClickListener{
        EditText recbs_name;
        EditText recbs_time;
        KeyListener recbs_name_lisn;
        KeyListener recbs_time_lisn;
        boolean editing = false;
        Context context;
        RecyclerBoardSetting rbs;
        public bSetOnClickListener(EditText recbs_name, EditText recbs_time, Context context, RecyclerBoardSetting rbs){
            this.recbs_name = recbs_name;
            this.recbs_time = recbs_time;
            this.context = context;
            this.rbs = rbs;
            recbs_name_lisn = recbs_name.getKeyListener();
            recbs_time_lisn = recbs_time.getKeyListener();
            recbs_name.setKeyListener(null);
            recbs_time.setKeyListener(null);
        }
        public void forceKeyListen(){
            recbs_name.setKeyListener(recbs_name_lisn);
            recbs_time.setKeyListener(recbs_time_lisn);
        }
        @Override
        public void onClick(View view) {
            if(!editing){
                if(!editingone){
                    recbs_name.setKeyListener(recbs_name_lisn);
                    recbs_time.setKeyListener(recbs_time_lisn);
                    ((ImageButton)view).setImageResource(R.drawable.btn_check);
                    recbs_name.requestFocus();
                    InputMethodManager imm = (InputMethodManager) context.getSystemService(Activity.INPUT_METHOD_SERVICE);
                    imm.showSoftInput(recbs_name, InputMethodManager.SHOW_IMPLICIT);
                    editing=!editing;
                    editingone=!editingone;
                }
            }
            else{
                ArrayList<Integer> dates = new ArrayList<Integer>();
                ArrayList<Integer> times = new ArrayList<Integer>();
                if(CheckNameAndTime.CheckNameAndTime(recbs_name.getText().toString(),recbs_time.getText().toString(),dates,times,context)){
                    rbs.name = recbs_name.getText().toString();
                    rbs.time = BoardSetting.TimeStringMaker(dates, times);

                    recbs_time.setText(rbs.time);

                    BoardDBOpenHelper bdboh = new BoardDBOpenHelper(context);
                    bdboh=bdboh.open();
                    bdboh.create();
                    bdboh.updatecol(rbs.id,recbs_name.getText().toString(),recbs_time.getText().toString(),rbs.length,rbs.eaon,rbs.alarm,rbs.timer);

                    MainBoard.delalarm((int)rbs.id,context);
                    MainBoard.setalarm(recbs_name.getText().toString(),recbs_time.getText().toString(),rbs.timer,(int)rbs.id,context);

                    recbs_name.setKeyListener(null);
                    recbs_time.setKeyListener(null);

                    ((ImageButton)view).setImageResource(R.drawable.btn_settings);

                    InputMethodManager imm = (InputMethodManager) context.getSystemService(Activity.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                    editing=!editing;
                    editingone=!editingone;
                    bdboh.close();
                    alertadapter();
                }
            }

        }
    }
}
