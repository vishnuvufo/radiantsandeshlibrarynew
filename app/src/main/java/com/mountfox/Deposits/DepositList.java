package com.mountfox.Deposits;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AutoCompleteTextView;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.mountfox.Login;
import com.mountfox.ModeOfTransactionActivity;
import com.mountfox.R;
import com.mountfox.Retrofit.ApiInterface;
import com.mountfox.Retrofit.Constants;
import com.mountfox.response.BankDepositResponse;
import com.mountfox.response.PickupTransaction;
import com.mountfox.response.PickupTransactions;
import com.mountfox.sharedPref.ConstantValues;
import com.mountfox.sharedPref.SharedPreference;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DepositList extends AppCompatActivity implements View.OnClickListener{

    public static final String TAG = DepositList.class.getSimpleName();
    LinearLayout ln_deposit_one;
    RecyclerView recyclerview;
    ApiInterface apiInterface;
    ArrayList<PickupTransactions> pickupTransactionArrayList;
    AutoCompleteTextView ed_bank_name;
    FloatingActionButton nextButton;
    int selectedPosition = 0;
    List selectedPositions;
    ArrayList<Bundle> dataList;
    ArrayList<String>transidarraylist = new ArrayList<>();
    ArrayList<String>multitypesarraylist = new ArrayList<>();
    ArrayList<String>singletypesarraylist = new ArrayList<>();
    ArrayList<String>multisize = new ArrayList<>();
    ArrayList<Integer> amountarraylist = new ArrayList<Integer>();
    ArrayList<Integer> depamountarraylist = new ArrayList<Integer>();
    ArrayList<Integer> balamountarraylist = new ArrayList<Integer>();
    ArrayList<String>multitypearraylist = new ArrayList<>();
    public int sum = 0;
    public int depamountsum = 0;
    public int balamountsum = 0;
    String accountnumbertostring = "";
    String transidtostring = "",counttostring="",ce_id="",datetime="",depamount="",balamount="",totalcih="",multiidstring="",multitypes="";
    TextView tv_cih;
    public BankDeposiAdapter bankDeposiAdapter;
    ImageView back;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
        setContentView(R.layout.activity_deposit_entry);
        String Urlstatus = SharedPreference.getDefaults(DepositList.this, ConstantValues.TAG_URLVALIDATE);

        if(Urlstatus.equals("dontswap")){
            apiInterface = Constants.getClient().create(ApiInterface.class);
        }else if(Urlstatus.equals("swap")) {
            apiInterface = Constants.getClientTwo().create(ApiInterface.class);
        }
        recyclerview = findViewById(R.id.recyclerview);
        back = findViewById(R.id.back);
        back.setOnClickListener(this);
        nextButton = findViewById(R.id.nextButton);
        tv_cih = findViewById(R.id.tv_cih);

        GridLayoutManager mGridLayoutManagerCategoriesBrand = new GridLayoutManager(DepositList.this, 1);
        recyclerview.setLayoutManager(mGridLayoutManagerCategoriesBrand);
        recyclerview.setHasFixedSize(true);
        recyclerview.setNestedScrollingEnabled(true);
        nextButton.setOnClickListener(this);



        ce_id =  Login.ce_id_main;

        Log.e(TAG,"ce_id->"+ce_id);

        try{
            Date today = new Date();
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
            datetime = format.format(today);
            Log.e(TAG,"dateToStr ->"+datetime);
            onDeposit(Login.ce_id_main,datetime);
        }catch (Exception e){
            Log.e(TAG,"exception e"+e.getMessage());
        }




    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.back:
                startActivity(new Intent(DepositList.this, ModeOfTransactionActivity.class));
                break;
            case R.id.nextButton:
                for(int i=0;i<multitypearraylist.size();i++){
                    multiidstring = multitypearraylist.get(0);
                }

                if(multiidstring.equals("Yes")){

                    Log.e(TAG,"inside yes");

                    try {
                        sum = 0;
                        for(int i = 0; i < this.amountarraylist.size(); i++){
                            sum += this.amountarraylist.get(i);
                        }
                        accountnumbertostring = Integer.toString(sum);
                        for (String s : transidarraylist)
                        {
                            transidtostring +=s+"," ;

                        }
                        depamountsum = 0;
                        for(int i = 0; i < this.depamountarraylist.size(); i++){
                            depamountsum = this.depamountarraylist.get(0);
                        }
                        depamount = Integer.toString(depamountsum);

                        balamountsum = 0;
                        for(int i = 0; i < this.balamountarraylist.size(); i++){
                            balamountsum = this.balamountarraylist.get(0);
                        }
                        balamount = Integer.toString(balamountsum);
                        counttostring = String.valueOf(transidarraylist.size());





                        Intent intent = new Intent(DepositList.this, Deposit.class);
                        intent.putExtra("trans_id", transidtostring);
                        intent.putExtra("pickup_amount",accountnumbertostring);
                        intent.putExtra("noofcount",counttostring);
                        intent.putExtra("deptype","Burial");
                        intent.putExtra("depositedamount",depamount);
                        intent.putExtra("balanceamount",balamount);
                        intent.putExtra("transactiontype","Multiple Transaction Entry");
                        intent.putExtra("type","Yes");
                        startActivity(intent);
                    } catch (Exception e) {
                        e.printStackTrace();
                        Toast.makeText(DepositList.this, "Can't open this transaction", Toast.LENGTH_SHORT).show();
                    }

                }else {

                    Log.e(TAG,"not inside yes");

                    try {
                        sum = 0;
                        for(int i = 0; i < this.amountarraylist.size(); i++){
                            sum += this.amountarraylist.get(i);
                        }
                        accountnumbertostring = Integer.toString(sum);
                        for (String s : transidarraylist)
                        {
                            transidtostring +=s+"," ;

                        }
                        depamountsum = 0;
                        for(int i = 0; i < this.depamountarraylist.size(); i++){
                            depamountsum += this.depamountarraylist.get(i);
                        }
                        depamount = Integer.toString(depamountsum);

                        balamountsum = 0;
                        for(int i = 0; i < this.balamountarraylist.size(); i++){
                            balamountsum += this.balamountarraylist.get(i);
                        }
                        balamount = Integer.toString(balamountsum);
                        counttostring = String.valueOf(transidarraylist.size());

                        Intent intent = new Intent(DepositList.this, Deposit.class);
                        intent.putExtra("trans_id", transidtostring);
                        intent.putExtra("pickup_amount",accountnumbertostring);
                        intent.putExtra("noofcount",counttostring);
                        intent.putExtra("deptype","Burial");
                        intent.putExtra("depositedamount",depamount);
                        intent.putExtra("balanceamount",balamount);
                        intent.putExtra("transactiontype","Multiple Transaction Entry");
                        intent.putExtra("type","Yes");
                        startActivity(intent);
                    } catch (Exception e) {
                        e.printStackTrace();
                        Toast.makeText(DepositList.this, "Can't open this transaction", Toast.LENGTH_SHORT).show();
                    }
                }







                break;
        }
    }


    public void onDeposit(String ce_id,String date){
        try{


             Log.e(TAG,"inside ce_id ->"+Login.ce_id_main);
             Log.e(TAG,"inside date ->"+datetime);

       Call<BankDepositResponse>bankDepositResponseCall = apiInterface.doBankDepositResponse("PickupTransactions",ce_id,date);
            bankDepositResponseCall.enqueue(new Callback<BankDepositResponse>() {
                @Override
                public void onResponse(Call<BankDepositResponse> call, Response<BankDepositResponse> response) {
                    if(response.code()==200){
                        if(response.body().getStatus().equals("1")){
                            if(response.body().getCashInHand().equals("")){
                                tv_cih.setText("Rs : "+"0");
                            }else {
                                tv_cih.setText("Rs : "+response.body().getCashInHand());
                            }

                            List<PickupTransaction>pickupTransactionList = response.body().getPickupTransactions();
                            if(pickupTransactionList.size()==0){
                                Toast.makeText(DepositList.this, "No Bank Deposit Found", Toast.LENGTH_SHORT).show();
                            }else {
                                bankDeposiAdapter = new BankDeposiAdapter(DepositList.this,pickupTransactionList);
                                recyclerview.setAdapter(bankDeposiAdapter);
                              //  recyclerview.setAdapter(new BankDeposiAdapter(DepositList.this,pickupTransactionList));
                            }
                        }else {
                            Toast.makeText(DepositList.this, ""+response.body().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                }
                @Override
                public void onFailure(Call<BankDepositResponse> call, Throwable t) {
                    Toast.makeText(DepositList.this, ""+t.getMessage(), Toast.LENGTH_SHORT).show();
                   Log.e(TAG,"throwable->"+t.getMessage());
                }
            });
        }catch (Exception e){
            Toast.makeText(DepositList.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
            Log.e(TAG,"Exception->"+e.getMessage());
        }
    }

    @SuppressLint("RestrictedApi")
    public void isFabEnable(List positionArray){
        selectedPositions = new ArrayList();
        if (positionArray.size() > 1) {
            nextButton.setVisibility(View.VISIBLE);
        } else if (positionArray.size() == 0||positionArray.size()==1) {
            nextButton.setVisibility(View.GONE);
        }

    }



    public class BankDeposiAdapter extends RecyclerView.Adapter<BankDeposiAdapter.CategoriesViewHolder> {

        private Context mContext;
        private List<PickupTransaction> pickupTransactionList;
        private ArrayList<Bundle> list;
        int i = 0;
        List positionArray = new ArrayList();
         Boolean multiplecheckboxstatus = false;
         Boolean singlecheckboxstatus = false;
         Boolean singlewithoutdepcheckboxstatus = false;
        public String multiplevalue ="",singlevalue="",singlepartial="",uniqueid="",clientfirststringremove="",
                clientreplacestring="",customerfirststringremove="",
                customerreplacestring="",pointidfirststringremove="",
                pointidreplacestring="",pickupnamefirststringremove="",
                pickupnamereplacestring="";




        public BankDeposiAdapter(Context mContext, List<PickupTransaction> pickupTransactionList) {
            this.mContext = mContext;
            this.pickupTransactionList = pickupTransactionList;
            notifyDataSetChanged();
        }

        @Override
        public BankDeposiAdapter.CategoriesViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View mView = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_depositlist, parent, false);
            return new BankDeposiAdapter.CategoriesViewHolder(mView);
        }

        @Override
        public void onBindViewHolder(final CategoriesViewHolder holder, @SuppressLint("RecyclerView") final int position) {







            holder.tv_transtype.setText(pickupTransactionList.get(position).getTransactionType());
            holder.tv_collamount.setText("Rs. " + pickupTransactionList.get(position).getCollectionAmount());


            if(pickupTransactionList.get(position).getMulti().equals("Yes")){
                clientfirststringremove =pickupTransactionList.get(position).getClientName().substring(1);
                clientreplacestring = clientfirststringremove.replace("#",",");
                Log.e(TAG,"replace string ->"+clientreplacestring);
                holder.tv_clientname.setText(clientreplacestring);
            }else {
                holder.tv_clientname.setText(pickupTransactionList.get(position).getClientName());
            }

            if(pickupTransactionList.get(position).getMulti().equals("Yes")){
                customerfirststringremove =pickupTransactionList.get(position).getCustomerName().substring(1);
                customerreplacestring = customerfirststringremove.replace("#",",");
                Log.e(TAG,"replace string ->"+customerreplacestring);
                holder.tv_customername.setText(customerreplacestring);
            }else {
                holder.tv_customername.setText(pickupTransactionList.get(position).getCustomerName());
            }


            if(pickupTransactionList.get(position).getMulti().equals("Yes")){
                pointidfirststringremove =pickupTransactionList.get(position).getPointID().substring(1);
                pointidreplacestring = pointidfirststringremove.replace("#",",");
                Log.e(TAG,"replace string ->"+pointidreplacestring);
                holder.tv_pointid.setText(pointidreplacestring);
            }else {
                holder.tv_pointid.setText(pickupTransactionList.get(position).getPointID());
            }

            if(pickupTransactionList.get(position).getMulti().equals("Yes")){
                pickupnamefirststringremove =pickupTransactionList.get(position).getPickupName().substring(1);
                pickupnamereplacestring = pickupnamefirststringremove.replace("#",",");
                Log.e(TAG,"replace string ->"+pickupnamereplacestring);
                holder.tv_pickupname.setText(pickupnamereplacestring);
            }else {
                holder.tv_pickupname.setText(pickupTransactionList.get(position).getPickupName());
            }





            if (pickupTransactionList.get(position).getCheckBoxStatus().equals("0")) {
                holder.checkbox.setVisibility(View.VISIBLE);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    holder.ln_border.setBackground(getResources().getDrawable(R.drawable.border_grey_square));
                }
            } else if (pickupTransactionList.get(position).getCheckBoxStatus().equals("1")) {
                holder.checkbox.setVisibility(View.GONE);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    holder.ln_border.setBackground(getResources().getDrawable(R.drawable.border_hidden));
                }
            } else if (pickupTransactionList.get(position).getCheckBoxStatus().equals("2") && pickupTransactionList.get(position).getMulti().equals("Yes")) {
                holder.checkbox.setVisibility(View.VISIBLE);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    holder.ln_border.setBackground(getResources().getDrawable(R.drawable.border_grey_square));
                }
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    holder.checkbox.setButtonTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorPrimaryLite)));
                }

            } else if (pickupTransactionList.get(position).getCheckBoxStatus().equals("2") && pickupTransactionList.get(position).getMulti().equals("No")) {
                holder.checkbox.setVisibility(View.VISIBLE);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    holder.ln_border.setBackground(getResources().getDrawable(R.drawable.border_grey_square));
                }
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    holder.checkbox.setButtonTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorwarning)));
                }
            } else {

            }






            holder.checkbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    CheckBox multicheckBox = ((CheckBox) buttonView);
                    CheckBox singlecheckBox = ((CheckBox) buttonView);
                    CheckBox singlepartialcheckBox = ((CheckBox) buttonView);


                    if(pickupTransactionList.get(position).getCheckBoxStatus().equals("2")&&pickupTransactionList.get(position).getMulti().equals("Yes")){

                        if(singlevalue.equals("1")){
                            Toast.makeText(mContext, "you cannot select this Transaction!", Toast.LENGTH_SHORT).show();
                            holder.checkbox.setChecked(false);
                        }else if(singlepartial.equals("1")){
                            Toast.makeText(mContext, "you cannot select this Transaction!", Toast.LENGTH_SHORT).show();
                            holder.checkbox.setChecked(false);
                        }
                        else {
                            if(multicheckBox.isChecked()){

                                if(multitypesarraylist.size()==1){
                                    Toast.makeText(mContext, "you cannot select this Transaction!", Toast.LENGTH_SHORT).show();
                                    holder.checkbox.setChecked(false);
                                }else {
                                    multiplevalue = "1";
                                    transidarraylist.add(pickupTransactionList.get(position).getTransId());
                                    multitypearraylist.add(pickupTransactionList.get(position).getMulti());
                                    amountarraylist.add(Integer.valueOf(pickupTransactionList.get(position).getCollectionAmount()));
                                    depamountarraylist.add(Integer.valueOf(pickupTransactionList.get(position).getDepositedAmount()));
                                    balamountarraylist.add(Integer.valueOf(pickupTransactionList.get(position).getBalanceAmount()));
                                    ((DepositList) mContext).isFabEnable(transidarraylist);
                                    uniqueid = pickupTransactionList.get(position).getUniqueID();
                                    Log.e(TAG,"select multiple value->"+multiplevalue);
                                    Log.e(TAG,"unique select id ==>"+uniqueid);
                                    multitypesarraylist.add(pickupTransactionList.get(position).getUniqueID());
                                    Log.e(TAG,"unique array select id ==>"+multitypesarraylist.toString());
                                }



                            }else {
                                multiplevalue = "0";
                                multitypearraylist.remove(pickupTransactionList.get(position).getMulti());
                                transidarraylist.remove(pickupTransactionList.get(position).getTransId());
                                amountarraylist.remove(Integer.valueOf(pickupTransactionList.get(position).getCollectionAmount()));
                                depamountarraylist.remove(Integer.valueOf(pickupTransactionList.get(position).getDepositedAmount()));
                                balamountarraylist.remove(Integer.valueOf(pickupTransactionList.get(position).getBalanceAmount()));
                                ((DepositList) mContext).isFabEnable(transidarraylist);
                                uniqueid = "0";
                                Log.e(TAG,"unselect multiple value->"+multiplevalue);
                                Log.e(TAG,"unique unselect id ==>"+uniqueid);
                                multitypesarraylist.remove(pickupTransactionList.get(position).getUniqueID());
                                Log.e(TAG,"unique array unselect id ==>"+multitypesarraylist.toString());
                            }


                        }

                    }

                    if(pickupTransactionList.get(position).getMulti().equals("No")){
                        if(multiplevalue.equals("1")){
                            Toast.makeText(mContext, "you cannot select this Transaction!", Toast.LENGTH_SHORT).show();
                            holder.checkbox.setChecked(false);
                        }else if(singlevalue.equals("1")){
                            Toast.makeText(mContext, "you cannot select this Transaction!", Toast.LENGTH_SHORT).show();
                            holder.checkbox.setChecked(false);
                        }
                        else {
                            if(singlepartialcheckBox.isChecked()){

                                if(singletypesarraylist.size()==1){
                                    Toast.makeText(mContext, "you cannot select this Transaction!", Toast.LENGTH_SHORT).show();
                                    holder.checkbox.setChecked(false);
                                }else {
                                    singlepartial = "1";
                                    transidarraylist.add(pickupTransactionList.get(position).getTransId());
                                    multitypearraylist.add(pickupTransactionList.get(position).getMulti());
                                    amountarraylist.add(Integer.valueOf(pickupTransactionList.get(position).getCollectionAmount()));
                                    depamountarraylist.add(Integer.valueOf(pickupTransactionList.get(position).getDepositedAmount()));
                                    balamountarraylist.add(Integer.valueOf(pickupTransactionList.get(position).getBalanceAmount()));
                                    ((DepositList) mContext).isFabEnable(transidarraylist);
                                    Log.e(TAG,"select single partial value->"+singlepartial);
                                    singletypesarraylist.add(pickupTransactionList.get(position).getUniqueID());

                                }


                            }else
                            {
                                singlepartial = "0";
                                multitypearraylist.remove(pickupTransactionList.get(position).getMulti());
                                transidarraylist.remove(pickupTransactionList.get(position).getTransId());
                                amountarraylist.remove(Integer.valueOf(pickupTransactionList.get(position).getCollectionAmount()));
                                depamountarraylist.remove(Integer.valueOf(pickupTransactionList.get(position).getDepositedAmount()));
                                balamountarraylist.remove(Integer.valueOf(pickupTransactionList.get(position).getBalanceAmount()));
                                ((DepositList) mContext).isFabEnable(transidarraylist);
                                Log.e(TAG,"select single partial value->"+singlepartial);
                                singletypesarraylist.remove(pickupTransactionList.get(position).getUniqueID());

                            }
                        }
                    }



                    if(pickupTransactionList.get(position).getCheckBoxStatus().equals("0")){

                        if(multiplevalue.equals("1")){
                            Toast.makeText(mContext, "you cannot select this Transaction!", Toast.LENGTH_SHORT).show();
                            holder.checkbox.setChecked(false);
                        }else if(singlepartial.equals("1")){
                            Toast.makeText(mContext, "you cannot select this Transaction!", Toast.LENGTH_SHORT).show();
                            holder.checkbox.setChecked(false);
                        }
                        else {
                            if(singlecheckBox.isChecked()){
                                singlevalue = "1";
                                transidarraylist.add(pickupTransactionList.get(position).getTransId());
                                multitypearraylist.add(pickupTransactionList.get(position).getMulti());
                                amountarraylist.add(Integer.valueOf(pickupTransactionList.get(position).getCollectionAmount()));
                                depamountarraylist.add(Integer.valueOf(pickupTransactionList.get(position).getDepositedAmount()));
                                balamountarraylist.add(Integer.valueOf(pickupTransactionList.get(position).getBalanceAmount()));
                                ((DepositList) mContext).isFabEnable(transidarraylist);
                                Log.e(TAG,"select single value->"+singlevalue);
                                Log.e(TAG,"transid->"+transidarraylist);
                                Log.e(TAG,"amount->"+amountarraylist);
                            }else
                            {
                                singlevalue = "0";
                                multitypearraylist.remove(pickupTransactionList.get(position).getMulti());
                                transidarraylist.remove(pickupTransactionList.get(position).getTransId());
                                amountarraylist.remove(Integer.valueOf(pickupTransactionList.get(position).getCollectionAmount()));
                                depamountarraylist.remove(Integer.valueOf(pickupTransactionList.get(position).getDepositedAmount()));
                                balamountarraylist.remove(Integer.valueOf(pickupTransactionList.get(position).getBalanceAmount()));
                                ((DepositList) mContext).isFabEnable(transidarraylist);
                                Log.e(TAG,"select single value->"+singlevalue);
                                Log.e(TAG,"transid->"+transidarraylist);
                                Log.e(TAG,"amount->"+amountarraylist);

                            }
                        }
                    }
                }
            });







            Log.e(TAG,"deposit amount ->"+pickupTransactionList.get(position).getDepositedAmount());

            holder.ln_layout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(pickupTransactionList.get(position).getCheckBoxStatus().equals("0")||pickupTransactionList.get(position).getCheckBoxStatus().equals("2")) {
                        Intent intent = new Intent(mContext,Deposit.class);
                        intent.putExtra("trans_id",pickupTransactionList.get(position).getTransId());
                        intent.putExtra("pickup_amount",pickupTransactionList.get(position).getCollectionAmount());
                        intent.putExtra("noofcount","1");
                        intent.putExtra("deptype",pickupTransactionList.get(position).getDepositType());
                        intent.putExtra("depositedamount",pickupTransactionList.get(position).getDepositedAmount());
                        intent.putExtra("balanceamount",pickupTransactionList.get(position).getBalanceAmount());
                        intent.putExtra("transactiontype","Single Transaction Entry");
                        intent.putExtra("type",pickupTransactionList.get(position).getMulti());
                        mContext.startActivity(intent);
                    }else {
                        Toast.makeText(mContext, "Already Deposited Entry Transaction cannot view this!!!", Toast.LENGTH_SHORT).show();
                  }
                }
            });

        }

        @Override
        public int getItemCount() {
            return pickupTransactionList.size();
        }

        class CategoriesViewHolder extends RecyclerView.ViewHolder {
            TextView tv_clientname,tv_transtype,tv_customername,tv_pointid,tv_pickupname,tv_collamount;
            CheckBox checkbox;
            LinearLayout ln_layout,ln_border;

            CategoriesViewHolder(View itemView) {
                super(itemView);
                tv_clientname = itemView.findViewById(R.id.tv_clientname);
                tv_transtype = itemView.findViewById(R.id.tv_transtype);
                tv_customername = itemView.findViewById(R.id.tv_customername);
                tv_pointid = itemView.findViewById(R.id.tv_pointid);
                tv_pickupname = itemView.findViewById(R.id.tv_pickupname);
                tv_collamount = itemView.findViewById(R.id.tv_collamount);
                checkbox = itemView.findViewById(R.id.checkbox);
                ln_layout = itemView.findViewById(R.id.ln_layout);
                ln_border = itemView.findViewById(R.id.ln_border);
            }
        }
    }
}
