package com.inti.student.billsplitter_v2;

import java.text.DecimalFormat;

/**
 * Created by Ady on 12/6/2017.
 */

public class Bill_list {

    private String _name,_price;
    private int _id;

    public Bill_list(int id,String name, String price){
        _name=name;
        _id=id;
        _price= price;


    }

    public String getName(){
        return _name;
    }

    public String getPrice(){
        return _price;
    }

    public int getId(){return _id;}

    public void setName(String nname){
        _name=nname;
    }

    public void setPrice(String nprice){
        _price=nprice;
    }


}


