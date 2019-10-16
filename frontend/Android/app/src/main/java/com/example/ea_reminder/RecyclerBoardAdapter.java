package com.example.ea_reminder;

import android.app.Application;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class RecyclerBoardAdapter extends RecyclerView.Adapter<RecyclerBoardAdapter.BoardViewHolder> {

    static ArrayList<RecyclerBoard> rblist; //내용 담긴 리스트

    static ArrayList<Switch> swlist; //RecyclerBoard의 스위치들 리스트=일괄적용 시 스위치 찾는 곳
    static ArrayList<Button> btlist; //RecyclerBoard의 타이머들 리스트=일괄적용 시 타이머 찾는 곳
    MainBoard mb;
    public boolean calledoutside = false;
    public RecyclerBoardAdapter(ArrayList<RecyclerBoard> rblist,MainBoard mb){
        this.rblist = rblist;
        swlist = new ArrayList<Switch>();
        btlist = new ArrayList<Button>();
        this.mb = mb;
    }

    @NonNull
    @Override
    public BoardViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_board, parent, false);
        BoardViewHolder bvh = new BoardViewHolder(view);
        return bvh;
    }

    @Override
    public void onBindViewHolder(@NonNull final BoardViewHolder holder, final int position) {
        holder.rec_name.setText(rblist.get(position).name);
        holder.rec_time.setText(rblist.get(position).time);

        holder.rec_sw.setChecked(rblist.get(position).on);
        swOnClickListener.setTimerButton(holder.rec_sw,holder.rec_b_time);
        swlist.add(holder.rec_sw);
        holder.rec_sw.setOnClickListener(new swOnClickListener(position,mb));

        btOnClickListener.setTimer(holder.rec_b_time,rblist.get(position).timer);
        btlist.add(holder.rec_b_time);
        holder.rec_b_time.setOnClickListener(new btOnClickListener(position,mb));

        if(position==0){
            holder.rec_topbar.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public int getItemCount() {
        return rblist.size();
    }


    public class BoardViewHolder extends RecyclerView.ViewHolder {
        TextView rec_name;
        TextView rec_time;
        Switch rec_sw;
        Button rec_b_time;
        View rec_topbar;
        public BoardViewHolder(@NonNull View itemView) {
            super(itemView);
            this.rec_name = (TextView)itemView.findViewById(R.id.rec_name);
            this.rec_time = (TextView)itemView.findViewById(R.id.rec_time);
            this.rec_sw = (Switch)itemView.findViewById(R.id.rec_sw);
            rec_b_time = (Button)itemView.findViewById(R.id.rec_b_time);
            rec_topbar = (View)itemView.findViewById(R.id.rec_topbar);
        }
    }

    public static class swOnClickListener implements View.OnClickListener {
        public int index = 0;//생성 순서
        MainBoard mb;
        public swOnClickListener(int index, MainBoard mb){//리스너 생성 시 인덱스를 넘겨줄 것
            this.index = index;
            this.mb=mb;
        }
        //스위치 값에 따른 타이머의 가시 여부 조절=일괄 조절 시 함수 호출할 것
        static public void setTimerButton(Switch sw, Button bt){
            if(sw.isChecked()){
                bt.setVisibility(View.VISIBLE);
            }
            else{
                bt.setVisibility(View.INVISIBLE);
            }
        }
        @Override
        public void onClick(View view) {
            rblist.get(index).on = ((Switch)view).isChecked();
            setTimerButton((Switch)view,btlist.get(index));
            //TODO: 스위치 클릭 시 변경 값 저장
            Context context = mb.getApplicationContext();
            BoardDBOpenHelper bdboh = new BoardDBOpenHelper(context);
            bdboh=bdboh.open();
            bdboh.create();
            RecyclerBoard rb = rblist.get(index);
            bdboh.updatecol(rb.id,rb.name,rb.time,"1:30",true,rb.on,rb.timer);
            Toast.makeText(context,rb.name+" 강좌의 알림이 "+(rb.on?"켜졌습니다.":"꺼졌습니다."),Toast.LENGTH_LONG).show();
            if(rb.on)mb.setalarm(rb.name,rb.time,rb.timer,(int)rb.id);
            else mb.delalarm((int)rb.id);
        }
    }

    public static class btOnClickListener implements View.OnClickListener {
        public int index = 0;//생성 순서
        MainBoard mb;
        public btOnClickListener(int index,MainBoard mb){//리스너 생성 시 인덱스를 넘겨줄 것
            this.index = index;
            this.mb=mb;
        }
        //타이머 표시 값 변경=일괄 조절 시 함수 호출할 것
        static public void setTimer(Button bt,int timer){
            String timebuttontext="";
            switch (timer){
                case RecyclerBoard.TIMER_10MIN:
                    timebuttontext="10분전";
                    break;
                case RecyclerBoard.TIMER_5MIN:
                    timebuttontext="5분전";
                    break;
                case RecyclerBoard.TIMER_1MIN:
                    timebuttontext="1분전";
                    break;
            }
            bt.setText(timebuttontext);
        }
        //타이머의 값이 최대 범위 내로 순환하도록 제한
        static public int incTimer(int timer){
            int temptimer = timer;
            temptimer++;
            if(temptimer>RecyclerBoard.MAXTIMER)temptimer=0;
            return temptimer;
        }
        @Override
        public void onClick(View view) {
            rblist.get(index).timer = incTimer(rblist.get(index).timer);
            setTimer((Button)view, rblist.get(index).timer);
            //TODO: 타이머 클릭 시 변경 값 저장
            Context context = mb.getApplicationContext();
            BoardDBOpenHelper bdboh = new BoardDBOpenHelper(context);
            bdboh=bdboh.open();
            bdboh.create();
            RecyclerBoard rb = rblist.get(index);
            bdboh.updatecol(rb.id,rb.name,rb.time,"1:30",true,rb.on,rb.timer);
            Toast.makeText(context,rb.name+" 강좌의 알림이 "+returntimestring(rb.timer)+"으로 설정되었습니다",Toast.LENGTH_LONG).show();
            mb.setalarm(rb.name,rb.time,rb.timer,(int)rb.id);
        }
    }
    public static String returntimestring(int timer){
        String returner="";
        switch (timer){
            case RecyclerBoard.TIMER_10MIN:
                returner= "10분전";
                break;
            case RecyclerBoard.TIMER_5MIN:
                returner= "5분전";
                break;
            case RecyclerBoard.TIMER_1MIN:
                returner= "1분전";
                break;
        }
        return returner;
    }
}
