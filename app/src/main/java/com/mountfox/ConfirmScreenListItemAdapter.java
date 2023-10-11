package com.mountfox;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

/**
 * Created by Sathish canine on 29-06-2016.
 */
public class ConfirmScreenListItemAdapter extends BaseAdapter {

//    public String[] pickupamount_stng,DepositeSlipnumber_stng,PISnum_strng,HCInumber_strng,SealTagNumber_strng,ClientCode_strng;

    ArrayList<String> pickupamount_stng, DepositeSlipnumber_stng, PISnum_strng, HCInumber_strng, SealTagNumber_strng, ClientCode_strng = new ArrayList<>();
    Context cc;

    public String captions;
    public String[] caps_arr;
    LayoutInflater mInflater;

    public ConfirmScreenListItemAdapter(Context appContext, ArrayList<String> pickupamount, ArrayList<String> DepositeSlipnumber, ArrayList<String> PISnum, ArrayList<String> HCInumber, ArrayList<String> SealTagNumber, ArrayList<String> ClientCode) {
        mInflater = LayoutInflater.from(appContext);
        cc = appContext;
        pickupamount_stng = pickupamount;
        DepositeSlipnumber_stng = DepositeSlipnumber;
        PISnum_strng = PISnum;

        HCInumber_strng = HCInumber;
        SealTagNumber_strng = SealTagNumber;
        ClientCode_strng = ClientCode;
    }

    @Override
    public int getCount() {
        return pickupamount_stng.size();
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    static class ViewHolder {
        TextView two_text_view, three_text_view, four_text_view, five_text_view, seal_tag_number_view, hci_number_view, pis_number_view, pickup_amount_textView, deposite_slip_number, pis_number, HCInumber, seal_tag_number, client_code, deposit_sleep_number_view, one_text_view;

        LinearLayout dep_slip_lin_layout, pis_number_lin_layout, hci_number_lin_layout, seal_tag_number_lin_layout;
        LinearLayout one_layout, two_lin_layout, three_lin_layout, four_lin_layout, fiveLinear;
        TextView pickup_amount_viewww;
        ImageView edit_icon;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.confirm_dialog_screen, null);

            holder = new ViewHolder();
            holder.pickup_amount_textView = (TextView) convertView.findViewById(R.id.pickup_amount11);
            holder.deposite_slip_number = (TextView) convertView.findViewById(R.id.deposite_slip_number);
            holder.pis_number = (TextView) convertView.findViewById(R.id.pis_number);
            holder.HCInumber = (TextView) convertView.findViewById(R.id.HCInumber);

            holder.one_layout = (LinearLayout) convertView.findViewById(R.id.one_layout);

            holder.two_lin_layout = (LinearLayout) convertView.findViewById(R.id.two_lin_layout);

            holder.three_lin_layout = (LinearLayout) convertView.findViewById(R.id.three_lin_layout);

            holder.four_lin_layout = (LinearLayout) convertView.findViewById(R.id.four_lin_layout);

            holder.fiveLinear = (LinearLayout) convertView.findViewById(R.id.fiveLinear);

            holder.pickup_amount_viewww = (TextView) convertView.findViewById(R.id.pickup_amount_viewww);
            holder.one_text_view = (TextView) convertView.findViewById(R.id.one_text_view);
            holder.two_text_view = (TextView) convertView.findViewById(R.id.two_text_view);
            holder.three_text_view = (TextView) convertView.findViewById(R.id.three_text_view);
            holder.four_text_view = (TextView) convertView.findViewById(R.id.four_text_view);
            holder.five_text_view = (TextView) convertView.findViewById(R.id.five_text_view);

            captions = TransactionSingleItemDataCenter.captions;
            Log.v("ReceivePayment", "captions" + captions);
            caps_arr = new String[captions.split(",").length + 2];
            caps_arr = captions.split(",");


            holder.edit_icon = (ImageView) convertView.findViewById(R.id.edit_icon);

            holder.seal_tag_number = (TextView) convertView.findViewById(R.id.seal_tag_number);
            holder.client_code = (TextView) convertView.findViewById(R.id.client_code);


            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }


        if (!caps_arr[1].equalsIgnoreCase("0")) {
            holder.one_text_view.setText(caps_arr[1]);
        } else
            holder.one_layout.setVisibility(View.GONE);
        if (!caps_arr[2].equalsIgnoreCase("0")) {
            holder.two_text_view.setText(caps_arr[2]);
        } else
            holder.two_lin_layout.setVisibility(View.GONE);
        if (!caps_arr[3].equalsIgnoreCase("0")) {
            holder.three_text_view.setText(caps_arr[3]);
        } else
            holder.three_lin_layout.setVisibility(View.GONE);
        if (!caps_arr[4].equalsIgnoreCase("0")) {
            holder.four_text_view.setText(caps_arr[4]);
        } else
            holder.four_lin_layout.setVisibility(View.GONE);
        if (!caps_arr[5].equalsIgnoreCase("0")) {
            holder.five_text_view.setText(caps_arr[5]);
        } else
            holder.fiveLinear.setVisibility(View.GONE);

        // Bind the data efficiently with the holder.
        holder.pickup_amount_textView.setText(pickupamount_stng.get(position));

        holder.deposite_slip_number.setText(DepositeSlipnumber_stng.get(position));

        holder.pis_number.setText(PISnum_strng.get(position));

        holder.HCInumber.setText(HCInumber_strng.get(position));

        holder.seal_tag_number.setText(SealTagNumber_strng.get(position));

        holder.client_code.setText(ClientCode_strng.get(position));

        holder.edit_icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("Item Selected", "Item Selected : " + position);
//                Toast.makeText(cc, "Selected potion is" + position, Toast.LENGTH_LONG).show();
                new EditItemSelectedPosition(position);
                Intent iii = new Intent(cc, EditBeforeConfirmation.class);
//                iii.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                iii.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                cc.startActivity(iii);
//                cc.getApplicationContext().startActivity(iii);
            }
        });

        return convertView;
    }
}