package k.k.helper;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class CrawlingAdapter1 extends RecyclerView.Adapter<CrawlingAdapter1.ViewHolder> {

    private ArrayList<Dinner.ItemObject> mList;

    public  class ViewHolder extends RecyclerView.ViewHolder {
        private TextView textView_title, textView_reward, textView_place, textView_view;

        public ViewHolder(View itemView) {
            super(itemView);

            textView_title = (TextView) itemView.findViewById(R.id.textView_title);
            textView_reward = (TextView) itemView.findViewById(R.id.textView_reward);
            textView_place = (TextView) itemView.findViewById(R.id.textView_place);
            textView_view = (TextView) itemView.findViewById(R.id.textView_view);
        }
    }

    public CrawlingAdapter1(ArrayList<Dinner.ItemObject> list) {
        this.mList = list;
    }

    @NonNull
    @Override
    public CrawlingAdapter1.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.crawling_item, parent, false);
        return new CrawlingAdapter1.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CrawlingAdapter1.ViewHolder holder, int position) {

        holder.textView_title.setText(String.valueOf(mList.get(position).getTitle()));
        holder.textView_reward.setText(String.valueOf(mList.get(position).getReward()));
        holder.textView_place.setText(String.valueOf(mList.get(position).getPlace()));
        holder.textView_view.setText(String.valueOf(mList.get(position).getView()));
    }

    @Override
    public int getItemCount() { return mList.size(); }

}