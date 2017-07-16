package com.inti.student.billsplitter_v2;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.Preference;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class HomeActivity extends AppCompatActivity {

    //Button variables
    Button submit_btn,create_btn2,edit_btn;
    //EditText variables
    EditText Name_text,Price_text,Price_text2;
    //Text view variables
    TextView Name_text2;
    //Stores data into Bill_list class using Array List
    static List<Bill_list> bill_list = new ArrayList<Bill_list>();
    //List view variables
    ListView billListView;
    //Array adapter to store data into Bill_list class
    ArrayAdapter<Bill_list> billadapter;
    //Find position when user click a certain area
    static int clickedItemIndex;
    //Edit and delete option
    private static final int EDIT=0, DELETE=1;
    //Database
    DatabaseHandler dbHandler;
    //Floating button
    FloatingActionButton fab_plus,fab_add,fab_delete,fab_calculator,fab_refresh;
    Animation FabOpen, FabClose, FabClock, FabAnticlock;
    boolean isOpen = false;
    //Radio button for taxes selection
    RadioGroup radio_g;
    int selected_id;
    double result=0;
    RadioButton r1,r2,r3,r4;
    int selection;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        billListView=(ListView)findViewById(R.id.list);
        //To find the position when user click a certain area
        billListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                clickedItemIndex=position;
                return false;
            }
        });

        //To pop out option such as edit and delete after user long click an item in the list view
        registerForContextMenu(billListView);

        dbHandler=new DatabaseHandler(getApplicationContext());

        setSubmit_btn();
        actionButton();

        //If the database consist data it will retrieve all data it contains and show it on the list view
        if( dbHandler.getHistoryCount() !=0){
            bill_list.addAll(dbHandler.getAllHistory());
        }

        //Refresh the list view
        populateList();

        //For app intro
        //If the app is start on first time it will pop out app intro, if on second or more times it will not show out
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                SharedPreferences getPrefs= PreferenceManager.getDefaultSharedPreferences(getBaseContext());
                boolean isFirstStart= getPrefs.getBoolean("firstStart",true);

                if(isFirstStart){
                    startActivity(new Intent(HomeActivity.this,Introduction.class));
                    SharedPreferences.Editor e = getPrefs.edit();
                    e.putBoolean("firstStart",false);
                    e.apply();
                }
            }
        });
        thread.start();

    }

    private class BillListAdapter extends ArrayAdapter<Bill_list>{
        //Show the data contain in the Bill_list class in the format of bill_list1 layout
        public BillListAdapter(){
            super(HomeActivity.this,R.layout.bill_list1,bill_list);
        }

        @Override
        public View getView (int position, View view, ViewGroup parent){
            if(view == null){
                view=getLayoutInflater().inflate(R.layout.bill_list1,parent,false);
            }

            Bill_list currentList= bill_list.get(position);

            TextView name= (TextView) view.findViewById(R.id.name_textview1);
            name.setText(currentList.getName());
            TextView price= (TextView) view.findViewById(R.id.price_textview1);
            price.setText(currentList.getPrice());

            return view;
        }
    }

    //For floating action button and animation
    public void actionButton(){
        fab_plus=(FloatingActionButton)findViewById(R.id.fab_plus);
        fab_add=(FloatingActionButton)findViewById(R.id.fab_add);
        fab_delete=(FloatingActionButton)findViewById(R.id.fab_delete);
        fab_calculator=(FloatingActionButton)findViewById(R.id.fab_calculator);
        fab_refresh=(FloatingActionButton)findViewById(R.id.fab_refresh);

        FabOpen= AnimationUtils.loadAnimation(getApplicationContext(),R.anim.fab_open);
        FabClose=AnimationUtils.loadAnimation(getApplicationContext(),R.anim.fab_close);
        FabAnticlock=AnimationUtils.loadAnimation(getApplicationContext(),R.anim.rotate_anticlockwise);
        FabClock=AnimationUtils.loadAnimation(getApplicationContext(),R.anim.rotate_clockwise);

        fab_plus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isOpen){
                    fab_delete.startAnimation(FabClose);
                    fab_add.startAnimation(FabClose);
                    fab_calculator.startAnimation(FabClose);
                    fab_refresh.startAnimation(FabClose);
                    fab_plus.startAnimation(FabAnticlock);
                    fab_add.setClickable(false);
                    fab_delete.setClickable(false);
                    fab_calculator.setClickable(false);
                    fab_refresh.setClickable(false);
                    isOpen=false;
                }else{
                    fab_delete.startAnimation(FabOpen);
                    fab_add.startAnimation(FabOpen);
                    fab_calculator.startAnimation(FabOpen);
                    fab_refresh.startAnimation(FabOpen);
                    fab_plus.startAnimation(FabClock);
                    fab_add.setClickable(true);
                    fab_delete.setClickable(true);
                    fab_calculator.setClickable(true);
                    fab_refresh.setClickable(true);
                    isOpen=true;
                }
            }
        });

        //Add new item into the list and database
        fab_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder mBuilder = new AlertDialog.Builder(HomeActivity.this);
                View mview=getLayoutInflater().inflate(R.layout.createlist,null);
                Name_text=(EditText)mview.findViewById(R.id.Name);
                Price_text=(EditText)mview.findViewById(R.id.Price);
                create_btn2=(Button)mview.findViewById(R.id.create_btn2);

                mBuilder.setView(mview);
                final AlertDialog dialog = mBuilder.create();

                dialog.setCancelable(true);
                dialog.show();

                create_btn2.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                            if (!Name_text.getText().toString().isEmpty() && !Price_text.getText().toString().isEmpty() && Double.valueOf(Price_text.getText().toString()).doubleValue()!=0) {

                                if(selection==1){
                                    result = Double.valueOf(Price_text.getText().toString()).doubleValue();
                                }else if(selection==2){
                                    result = Double.valueOf(Price_text.getText().toString()).doubleValue() + (Double.valueOf(Price_text.getText().toString()).doubleValue() * 0.06) ;
                                }else if(selection==3){
                                    result = Double.valueOf(Price_text.getText().toString()).doubleValue() + (Double.valueOf(Price_text.getText().toString()).doubleValue() * 0.10);
                                }else if(selection==4){
                                    result = Double.valueOf(Price_text.getText().toString()).doubleValue() + (Double.valueOf(Price_text.getText().toString()).doubleValue() * 0.16);
                                }

                                DecimalFormat dFormat= new DecimalFormat("0.00");
                                String formatted =dFormat.format(result);


                                Bill_list bill_list1 = new Bill_list(dbHandler.getHistoryCount(), String.valueOf(Name_text.getText()),formatted);

                                //If the name exist in the database it will prompt error and tell user to input a new name
                               if(r1.isChecked()||r2.isChecked()||r3.isChecked()||r4.isChecked()) {
                                   if(result!=0) {
                                       if (!nameExists(bill_list1)) {
                                           dbHandler.createHistory(bill_list1);
                                           bill_list.add(bill_list1);

                                           populateList();
                                           Toast.makeText(getApplicationContext(), R.string.success_create_msg, Toast.LENGTH_SHORT).show();
                                           dialog.dismiss();

                                       } else {
                                           Toast.makeText(getApplicationContext(), R.string.name_exist_msg, Toast.LENGTH_SHORT).show();
                                       }
                                   }else{
                                       Toast.makeText(getApplicationContext(), R.string.over_msg, Toast.LENGTH_SHORT).show();
                                       setSubmit_btn();
                                   }
                               }else{
                                   Toast.makeText(getApplicationContext(), R.string.over_msg, Toast.LENGTH_SHORT).show();
                                   setSubmit_btn();
                               }


                            } else {
                                Toast.makeText(getApplicationContext(), R.string.fail_create_msg, Toast.LENGTH_SHORT).show();
                            }
                        }
                });
            }
        });

        //Delete all data in the list view and database as well
        fab_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder a_builder= new AlertDialog.Builder(HomeActivity.this);
                a_builder.setMessage(R.string.deletequestion).setCancelable(false).setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                }).setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if(dbHandler.getHistoryCount()!=0) {
                            dbHandler.deleteAllHistory();
                            bill_list.clear();
                            populateList();
                            Toast.makeText(HomeActivity.this, R.string.delete_all_msg, Toast.LENGTH_SHORT).show();
                        }else{
                            Toast.makeText(HomeActivity.this, R.string.error_msg2, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                AlertDialog alert = a_builder.create();
                alert.setTitle("Alert!!!");
                alert.show();
            }
        });

        fab_calculator.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent("com.inti.student.billsplitter_v2.Calculator");
                startActivity(intent);
            }
        });

        fab_refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setSubmit_btn();
            }
        });
    }

    //Refresh the list view by retrieving data from the class through adapter
    private void populateList(){
        billadapter = new BillListAdapter();
        billListView.setAdapter(billadapter);
    }

    //Pop out 2 option when long click an item in the list view that are edit and delete
    public void onCreateContextMenu(ContextMenu menu, View view, ContextMenu.ContextMenuInfo menuInfo){
        super.onCreateContextMenu(menu, view, menuInfo);

        menu.setHeaderIcon(R.drawable.pencil_icon);
        menu.setHeaderTitle("Option");
        menu.add(Menu.NONE, EDIT, menu.NONE, "Edit");
        menu.add(Menu.NONE, DELETE, menu.NONE, "Delete");

    }

    public boolean onContextItemSelected (MenuItem item){
        switch(item.getItemId()){
            //Pop edit option to edit the list view
            case EDIT:
                //TODO: Implement editing the bill list
                //Show dialog to input new price
                AlertDialog.Builder mBuilder = new AlertDialog.Builder(HomeActivity.this);
                View mview=getLayoutInflater().inflate(R.layout.activity_edit,null);
                Name_text2=(TextView) mview.findViewById(R.id.Name2);
                Price_text2=(EditText)mview.findViewById(R.id.Price2);

                final Bill_list temp = HomeActivity.bill_list.get(HomeActivity.clickedItemIndex);
                Name_text2.setText(temp.getName());

                edit_btn=(Button)mview.findViewById(R.id.edit_btn);

                mBuilder.setView(mview);
                final AlertDialog dialog = mBuilder.create();

                //The dialog can be cancel by pressing back button or outside of the dialog screen
                dialog.setCancelable(true);
                dialog.show();

                edit_btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //If the textfield is empty it will prompt error and tell user to input empty fields
                        if(!Name_text2.getText().toString().isEmpty() && !Price_text2.getText().toString().isEmpty() && Double.valueOf(Price_text2.getText().toString()).doubleValue()!=0){

                            if(selection==1){
                                result = Double.valueOf(Price_text2.getText().toString()).doubleValue();
                            }else if(selection==2){
                                result = Double.valueOf(Price_text2.getText().toString()).doubleValue() + (Double.valueOf(Price_text2.getText().toString()).doubleValue() * 0.06) ;
                            }else if(selection==3){
                                result = Double.valueOf(Price_text2.getText().toString()).doubleValue() + (Double.valueOf(Price_text2.getText().toString()).doubleValue() * 0.10);
                            }else if(selection==4){
                                result = Double.valueOf(Price_text2.getText().toString()).doubleValue() + (Double.valueOf(Price_text2.getText().toString()).doubleValue() * 0.16);
                            }

                            //To convert the price which is string data type into double data type and set it to 2 decimal places
                            DecimalFormat dFormat= new DecimalFormat("0.00");
                            //Convert back to string data type
                            String formatted =dFormat.format(result);

                            //Get price and name and store it into bill_list2 temporarily
                            Bill_list bill_list2 = new Bill_list(temp.getId(), String.valueOf(Name_text2.getText()),formatted );

                            if(r1.isChecked()||r2.isChecked()||r3.isChecked()||r4.isChecked()) {
                                if (result != 0) {

                                    //Update list view
                                    bill_list.set(clickedItemIndex,bill_list2);
                                    //Update database
                                    dbHandler.updateHistory(bill_list2);

                                    billadapter.notifyDataSetChanged();

                                    populateList();
                                    Toast.makeText(getApplicationContext(), R.string.success_edit_msg, Toast.LENGTH_SHORT).show();
                                    dialog.dismiss();
                                } else {
                                    Toast.makeText(getApplicationContext(),R.string.over_msg,Toast.LENGTH_SHORT).show();
                                    setSubmit_btn();
                                }
                            } else{
                                Toast.makeText(getApplicationContext(),R.string.over_msg,Toast.LENGTH_SHORT).show();
                                setSubmit_btn();
                            }
                        }else{
                            Toast.makeText(getApplicationContext(),R.string.fail_edit_msg,Toast.LENGTH_SHORT).show();
                        }
                    }
                });

                break;

            case DELETE:
                //TODO: Implement deleting the bill list

                //Remove the data user click from the database
                dbHandler.deleteHistory(bill_list.get(clickedItemIndex));
                bill_list.remove(clickedItemIndex);

                Toast.makeText(getApplicationContext(),R.string.delete_msg,Toast.LENGTH_SHORT).show();
                populateList();

                break;
        }
        return super.onContextItemSelected(item);
    }

    public void setSubmit_btn(){
        AlertDialog.Builder mBuilder = new AlertDialog.Builder(HomeActivity.this);
        View mview=getLayoutInflater().inflate(R.layout.seconddialog_v2,null);

        radio_g=(RadioGroup)mview.findViewById(R.id.radiogroup);
        submit_btn=(Button)mview.findViewById(R.id.submit_btnv2);
        r1=(RadioButton)mview.findViewById(R.id.r1);
        r2=(RadioButton)mview.findViewById(R.id.r2);
        r3=(RadioButton)mview.findViewById(R.id.r3);
        r4=(RadioButton)mview.findViewById(R.id.r4);


        mBuilder.setView(mview);
        final AlertDialog dialog = mBuilder.create();

        dialog.setCancelable(true);
        dialog.show();

        submit_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selected_id = radio_g.getCheckedRadioButtonId();

                if(r1.isChecked()){
                   selection=1;
                }else if(r2.isChecked()){
                    selection=2;
                }else if(r3.isChecked()){
                    selection=3;
                }else if(r4.isChecked()){
                    selection=4;
                }

                if(r1.isChecked()||r2.isChecked()||r3.isChecked()||r4.isChecked()) {
                    Toast.makeText(HomeActivity.this,R.string.success_submit_msg, Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                }
                else{
                    Toast.makeText(HomeActivity.this,R.string.fail_submit_msg,Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    //To check whether name exists in the database
    private boolean nameExists(Bill_list bill_lists){
        String name=bill_lists.getName();
        int nameCount = bill_list.size();

        for(int i =0; i< nameCount; i++){
            if(name.compareToIgnoreCase(bill_list.get(i).getName())==0){
                return true;
            }
        }
        return false;
    }

}
