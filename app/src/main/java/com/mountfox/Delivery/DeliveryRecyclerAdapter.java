package com.mountfox.Delivery;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.mountfox.R;

import java.util.ArrayList;

/**
 * Created by Anra-4 on 11-2-17.
 */

public class DeliveryRecyclerAdapter extends RecyclerView.Adapter<DeliveryRecyclerAdapter.MyRateViewHolder> {
    private Context context;
    private ArrayList<Bundle> list;
    private String ce_id;

    public DeliveryRecyclerAdapter(Context context, ArrayList<Bundle> list,String ce_id) {
        this.context = context;
        this.list = list;
        this.ce_id = ce_id;
    }

    @Override
    public MyRateViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        LayoutInflater mInflater = LayoutInflater.from(viewGroup.getContext());

        ViewGroup mainGroup = (ViewGroup) mInflater.inflate(
                R.layout.recycler_delivery_transaction, viewGroup, false);
        MyRateViewHolder mainHolder = new MyRateViewHolder(mainGroup) {
            @Override
            public String toString() {
                return super.toString();
            }
        };
        return mainHolder;
    }

    @Override
    public void onBindViewHolder(final MyRateViewHolder holder, final int position) {
        holder.tvPointAddress.setText(list.get(position).getString("delivery_address").trim());
        holder.tvPointName.setText(list.get(position).getString("cust_name"));
        String codeCount="Client code: "+list.get(position).getString("client_codecount");
        holder.tvClientCode.setText(codeCount);
        holder.tvRequestAmount.setText(list.get(position).getString("request_amount"));
        holder.tvClientName.setText(list.get(position).getString("client_name"));
        holder.tvDepType.setText(list.get(position).getString("DepType"));

        setAnimation(holder.deliveryLayout,position);

        String tranStatus=list.get(position).getString("tranStatus");
        if(tranStatus.equals("0"))
        {
            holder.deliveryLayout.setEnabled(false);
            holder.deliveryLayout.setBackgroundColor(ContextCompat.getColor(context,R.color.colorPrimary1));
        }else if(tranStatus.equals("1")) {
            holder.deliveryLayout.setEnabled(true);
            holder.deliveryLayout.setBackgroundColor(ContextCompat.getColor(context,R.color.recycler_bg));
        }
        holder.deliveryLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(context,DeliverySubmitActivity.class);
                intent.putExtra("ce_id",ce_id);
                intent.putExtra("request_amount",list.get(position).getString("request_amount"));
                intent.putExtra("trans_id",list.get(position).getString("trans_id"));
                intent.putExtra("client_name",list.get(position).getString("client_name"));
                intent.putExtra("client_codecount",list.get(position).getString("client_codecount"));
                intent.putExtra("DepType",list.get(position).getString("DepType"));
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {

        return list.size();
    }

    class MyRateViewHolder extends RecyclerView.ViewHolder {

        LinearLayout deliveryLayout;
        TextView tvPointAddress, tvClientCode, tvPointName,tvRequestAmount,tvClientName,tvDepType;

        MyRateViewHolder(View view) {
            super(view);

            deliveryLayout = (LinearLayout) view.findViewById(R.id.deliveryLayout);
            tvPointAddress = (TextView) view.findViewById(R.id.tvPointAddress);
            tvPointName = (TextView) view.findViewById(R.id.tvPointName);
            tvClientCode = (TextView) view.findViewById(R.id.tvClientCode);
            tvRequestAmount = (TextView) view.findViewById(R.id.tvRequestAmount);
            tvClientName = (TextView) view.findViewById(R.id.tvClientName);
            tvDepType = (TextView) view.findViewById(R.id.tvDepType);
        }
    }

    private void setAnimation(View viewToAnimate, int position)
    {
        // If the bound view wasn't previously displayed on screen, it's animated
        viewToAnimate.animate().cancel();
        viewToAnimate.setTranslationY(50);
        viewToAnimate.setAlpha(0);
        viewToAnimate.animate().alpha(1.0f).translationY(0).setDuration(50).setStartDelay(position * 50);
    }
}
