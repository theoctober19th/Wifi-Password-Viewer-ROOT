package com.theoctober19th.wifipasswordviewer;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.theoctober19th.wifipasswordviewer.models.Network;

import java.util.ArrayList;
import java.util.List;

public class WifiListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

    private List<Network> mNetworkList;
//    ArrayList<String> mSSIDList = new ArrayList<>();
//    ArrayList<String> mPasswordList  = new ArrayList<>();
    private Context mContext;

    public WifiListAdapter(Context context, List<Network> list){
        mNetworkList  = list;
        mContext = context;
    }

//    public WifiListAdapter(ArrayList<String> mSSIDList, ArrayList<String> mPasswordList, Context mContext) {
//        this.mSSIDList = mSSIDList;
//        this.mPasswordList = mPasswordList;
//        this.mContext = mContext;
//    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.wifi_entry_layout, viewGroup, false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
        ViewHolder holder = (ViewHolder) viewHolder;
        Network network = mNetworkList.get(i);
        holder.ssid.setText(network.getSsid());
        holder.password.setText(network.getPassword());
    }

    @Override
    public int getItemCount() {
        return mNetworkList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        ImageView wifiIcon;
        TextView ssid;
        TextView password;
        ConstraintLayout parentLayout;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            wifiIcon = itemView.findViewById(R.id.wifiIcon);
            ssid = itemView.findViewById(R.id.ssid_textview);
            password = itemView.findViewById(R.id.password_textview);
            parentLayout = itemView.findViewById(R.id.parent_layout);
        }
    }
}
