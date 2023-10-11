package com.mountfox.Cheque_Pickup;

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

public class ChequePickupRecyclerAdapter extends RecyclerView.Adapter<ChequePickupRecyclerAdapter.MyRateViewHolder> {
    private Context context;
    private ArrayList<Bundle> list;
    private String ce_id;
    int iCustCodeCount=0;

    public ChequePickupRecyclerAdapter(Context context, ArrayList<Bundle> list, String ce_id) {
        this.context = context;
        this.list = list;
        this.ce_id = ce_id;
    }

    @Override
    public MyRateViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        LayoutInflater mInflater = LayoutInflater.from(viewGroup.getContext());

        ViewGroup mainGroup = (ViewGroup) mInflater.inflate(
                R.layout.recycler_cheque_pickup, viewGroup, false);
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
        holder.tvPointAddress.setText(list.get(position).getString("cust_address").trim());
        holder.tvPointName.setText(list.get(position).getString("cust_name"));
        String cust_code = list.get(position).getString("cust_code");

        if (cust_code != null && !cust_code.isEmpty()) {
            String[] arrayCustCode = cust_code.split(",");
            iCustCodeCount=arrayCustCode.length;
        }
        String codeCount = "Client code: " + iCustCodeCount;

        holder.tvClientCodeCountCheck.setText(codeCount);
        holder.tvClientName.setText(list.get(position).getString("client_name"));

//        setAnimation(holder.deliveryLayout,position);

        String tranStatus = list.get(position).getString("tranStatus");
        if (tranStatus.equals("0")) {
            holder.ChequeLayout.setEnabled(false);
            holder.ChequeLayout.setBackgroundColor(ContextCompat.getColor(context, R.color.colorPrimary1));
        } else if (tranStatus.equals("1")) {
            holder.ChequeLayout.setEnabled(true);
            holder.ChequeLayout.setBackgroundColor(ContextCompat.getColor(context, R.color.recycler_bg));
        }

        holder.ChequeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, ChequePickupSubmitActivity.class);
                intent.putExtra("ce_id", ce_id);
                System.out.println("Recycler Trans Id >>> " + list.get(position).getString("trans_id"));
                intent.putExtra("trans_id", list.get(position).getString("trans_id"));
                intent.putExtra("client_name", list.get(position).getString("client_name"));
                intent.putExtra("client_codecount", list.get(position).getString("client_codecount"));
                intent.putExtra("cust_code", list.get(position).getString("cust_code"));
                intent.putExtra("dep_type", list.get(position).getString("dep_type"));
                intent.putExtra("clientId", list.get(position).getString("clientId"));
                intent.putExtra("iCustCodeCount", iCustCodeCount+"");
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {

        return list.size();
    }

    class MyRateViewHolder extends RecyclerView.ViewHolder {

        LinearLayout ChequeLayout;
        TextView tvPointAddress, tvClientCode, tvClientCodeCountCheck, tvPointName, tvClientName;

        MyRateViewHolder(View view) {
            super(view);

            ChequeLayout = (LinearLayout) view.findViewById(R.id.ChequeLayout);
            tvPointAddress = (TextView) view.findViewById(R.id.tvPointAddressCheck);
            tvPointName = (TextView) view.findViewById(R.id.tvPointNameCheck);
            tvClientCode = (TextView) view.findViewById(R.id.tvClientCodeCheck);
            tvClientCodeCountCheck = (TextView) view.findViewById(R.id.tvClientCodeCountCheck);
            tvClientName = (TextView) view.findViewById(R.id.tvClientNameCheck);
        }
    }

    private void setAnimation(View viewToAnimate, int position) {
        // If the bound view wasn't previously displayed on screen, it's animated
        viewToAnimate.animate().cancel();
        viewToAnimate.setTranslationY(50);
        viewToAnimate.setAlpha(0);
        viewToAnimate.animate().alpha(1.0f).translationY(0).setDuration(50).setStartDelay(position * 50);
    }
}
