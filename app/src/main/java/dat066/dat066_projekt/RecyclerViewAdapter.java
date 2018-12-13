package dat066.dat066_projekt;


import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.MyViewHolder> {


    Context mContext;
    List<Goals> mData;

    public RecyclerViewAdapter(Context mContext, List<Goals> mData) {
        this.mContext = mContext;
        this.mData = mData;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

        View v;
        v = LayoutInflater.from(mContext).inflate(R.layout.goals_example,viewGroup,false);
        MyViewHolder vHolder = new MyViewHolder(v);

        return vHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder myViewHolder, int i) {

        myViewHolder.tv_name.setText(mData.get(i).getName());
        myViewHolder.tv_type.setText(mData.get(i).getType());
        myViewHolder.tv_endGoal.setText(mData.get(i).getEndGoal());
        myViewHolder.tv_time.setText(mData.get(i).getTime());

    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder{

        private TextView tv_name;
        private TextView tv_type;
        private TextView tv_endGoal;
        private TextView tv_time;
        private TextView tv_goalProgres;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            tv_name = (TextView) itemView.findViewById(R.id.goalTitle);
            tv_type = (TextView) itemView.findViewById(R.id.goalType);
            tv_endGoal = (TextView) itemView.findViewById(R.id.progressTextMax);
            tv_time = (TextView) itemView.findViewById(R.id.goalTime);
            tv_goalProgres = (TextView) itemView.findViewById(R.id.progressTextMin);

        }
    }




}


