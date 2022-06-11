package k.k.helper;

import android.app.Activity;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

public class ItemAdapter extends RecyclerView.Adapter<ItemAdapter.CustomViewHolder> {

    private ArrayList<PersonalData> mList = null;
    private Activity context = null;

    public ItemAdapter(Activity context, ArrayList<PersonalData> list) {
        this.context = context;
        this.mList = list;
    }

    class CustomViewHolder extends RecyclerView.ViewHolder {
        protected TextView name;
        protected TextView itemClass;
        protected TextView address;


        public CustomViewHolder(View view) {
            super(view);
            this.name = (TextView) view.findViewById(R.id.textView_name);
            this.itemClass = (TextView) view.findViewById(R.id.textView_ItemClass);
            this.address = (TextView) view.findViewById(R.id.textView_address);
        }
    }


    @Override
    public CustomViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.view_item_list, null);
        CustomViewHolder viewHolder = new CustomViewHolder(view);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull CustomViewHolder viewholder, int position) {

        viewholder.name.setText(mList.get(position).getName());
        viewholder.itemClass.setText(mList.get(position).getItemClass());
        viewholder.address.setText(mList.get(position).getAddress());
    }

    @Override
    public int getItemCount() {
        return (null != mList ? mList.size() : 0);
    }
}