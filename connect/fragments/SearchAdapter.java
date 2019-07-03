package com.healbe.healbe_example_andorid.connect.fragments;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.healbe.healbe_example_andorid.R;
import com.healbe.healbesdk.business_api.user_storage.entity.HealbeDevice;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

class SearchAdapter extends RecyclerView.Adapter<SearchAdapter.SearchViewHolder> {
    ArrayList<HealbeDevice> devices = new ArrayList<>();
    private HealbeDevice saved = new HealbeDevice();
    private View.OnClickListener listener;

    SearchAdapter() {
        setHasStableIds(true);
    }

    void setListener(View.OnClickListener listener) {
        this.listener = listener;
    }

    void setSaved(HealbeDevice saved) {
        this.saved = saved;
        notifyDataSetChanged();
    }

    HealbeDevice getSaved() {
        return saved;
    }

    @SuppressWarnings("unused")
    void setDevices(ArrayList<HealbeDevice> devices) {
        this.devices = devices;
    }

    @SuppressWarnings("NullableProblems")
    @NonNull
    @Override
    public SearchViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.search_list_string, parent, false);

        view.setOnClickListener(listener);
        return new SearchViewHolder(view);
    }

    @SuppressWarnings("NullableProblems")
    @Override
    public void onBindViewHolder(SearchViewHolder holder, int position) {
        holder.name.setText(devices.get(position).getName());
        if (get(position).getName().equals(saved.getName()))
            holder.image.setColorFilter(ContextCompat.getColor(holder.image.getContext(), R.color.main_purple));
        else {
            holder.image.setColorFilter(null);
        }
    }

    @Override
    public int getItemCount() {
        return devices.size();
    }

    public void add(HealbeDevice device) {
        if (!devices.contains(device)) {
            devices.add(device);
            notifyItemInserted(devices.size() - 1);
        }
    }

    HealbeDevice get(int pos) {
        return devices.get(pos);
    }

    void clear() {
        devices.clear();
    }

    class SearchViewHolder extends RecyclerView.ViewHolder {
        TextView name;
        ImageView image;

        SearchViewHolder(View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.name);
            image = itemView.findViewById(R.id.ind);
        }
    }

    @Override
    public long getItemId(int position) {
        return position;
    }
}
