package com.nicolas.supplier.data;

import android.os.Parcel;
import android.os.Parcelable;

import com.nicolas.supplier.R;
import com.nicolas.supplier.app.SupplierApp;

import org.json.JSONException;
import org.json.JSONObject;

public class OrderGoodsIDClass implements Parcelable {

    public static final String sort_order_code_swinted = SupplierApp.getInstance().getString(R.string.sort_order_code_swited);
    public static final String sort_order_code_id = SupplierApp.getInstance().getString(R.string.sort_order_code_id);

    public String Goodsldd;     //(货号id)
    public String Goodsld;      //(货号)
    public String OldGoodsld;   //(旧货号)
    public String Img;          //(图片地址)
    public int SendAmount;      //(待确认件数)

    public boolean switen;      //是否确认

    public OrderGoodsIDClass(){
        this.Goodsldd = " ";
        this.Goodsld = " ";
        this.OldGoodsld = " ";
        this.Img = " ";
        this.SendAmount = 0;
        this.switen  = false;
    }

    public void setSwiten(boolean switen) {
        this.switen = switen;
    }

    public boolean isSwiten() {
        return switen;
    }

    /**
     * b g Goodsldd(货号id),qoodsld(货号)oldGoodsld(旧货号),img(图片地址),sendAmount(待确认件数)
     * @param json
     */
    public OrderGoodsIDClass(String json){
        try {
            JSONObject object = new JSONObject(json);
            this.Goodsldd = object.optString("b_g_GoodsId_Id","");
            this.Goodsld = object.optString("goodsId","");
            this.OldGoodsld = object.optString("oldGoodsId","");
            this.Img = object.optString("img","");
            this.SendAmount = object.optInt("sendAmount",0);
            this.switen  = false;
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }

    protected OrderGoodsIDClass(Parcel in) {
        Goodsldd = in.readString();
        Goodsld = in.readString();
        OldGoodsld = in.readString();
        Img = in.readString();
        SendAmount = in.readInt();
        switen = in.readByte() != 0;
    }

    public static final Creator<OrderGoodsIDClass> CREATOR = new Creator<OrderGoodsIDClass>() {
        @Override
        public OrderGoodsIDClass createFromParcel(Parcel in) {
            return new OrderGoodsIDClass(in);
        }

        @Override
        public OrderGoodsIDClass[] newArray(int size) {
            return new OrderGoodsIDClass[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(Goodsldd);
        dest.writeString(Goodsld);
        dest.writeString(OldGoodsld);
        dest.writeString(Img);
        dest.writeInt(SendAmount);
        dest.writeByte((byte) (switen ? 1 : 0));
    }
}
