package com.mountfox;

import android.util.Log;

import java.util.ArrayList;

/**
 * Created by Sathish canine on 05-07-2016.
 */
public class HistoryDatacenter {
    public static ArrayList<String> CanceledpickupAmountArrayList = new ArrayList<>();
    public static ArrayList<String> CanceledDepositeSlipNumberArrayList = new ArrayList<>();
    public static ArrayList<String> CanceledPISnumberArrayList = new ArrayList<>();
    public static ArrayList<String> CanceledHCInumberArrayList = new ArrayList<>();
    public static ArrayList<String> CanceledClientCodeArrayList = new ArrayList<>();

    public static ArrayList<String> CanceledSealTagArrayList = new ArrayList<>();

    public HistoryDatacenter(ArrayList<String> CanceledpickupAmountArrayList,ArrayList<String> CanceledDepositeSlipNumberArrayList,ArrayList<String> CanceledPISnumberArrayList,ArrayList<String> CanceledHCInumberArrayList,ArrayList<String> CanceledClientCodeArrayList, ArrayList<String> CanceledSealTagArrayList)
    {
        this.CanceledpickupAmountArrayList=CanceledpickupAmountArrayList;
        Log.d("HistoryClass","HistoryClas            :"+this.CanceledpickupAmountArrayList);
        this.CanceledDepositeSlipNumberArrayList=CanceledDepositeSlipNumberArrayList;
        this.CanceledPISnumberArrayList=CanceledPISnumberArrayList;
        this.CanceledHCInumberArrayList=CanceledHCInumberArrayList;
        this.CanceledClientCodeArrayList=CanceledClientCodeArrayList;
        this.CanceledSealTagArrayList=CanceledSealTagArrayList;
    }
}