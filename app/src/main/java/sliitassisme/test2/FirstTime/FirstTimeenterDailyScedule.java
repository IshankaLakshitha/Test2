package sliitassisme.test2.FirstTime;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import sliitassisme.test2.R;
import sliitassisme.test2.database.DBhandler;

public class FirstTimeenterDailyScedule extends AppCompatActivity {

    TextView test;
    TextView DAY;
    Button GOPREV,GONEXT;
    DBhandler DATABASEHANDLER;
    int DayID=1;
    CheckBox CB;
    EditText Location;
    EditText Time;
    Spinner spinner;
    int index=0;
    List<String> Items = new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first_timeenter_daily_scedule);

        DATABASEHANDLER=new DBhandler(this,null,null,1);

        //test=(TextView) findViewById(R.id.txtTest);
        DAY=(TextView) findViewById(R.id.txtDay);
        Location=(EditText)findViewById(R.id.txtLocation);
        Time=(EditText)findViewById(R.id.txtTime);
        spinner=(Spinner)findViewById(R.id.spinner);

        DAY.setText("MONDAY");
        //addDays();
        GOPREV=(Button) findViewById(R.id.btnPrev);
        GOPREV.setVisibility(View.INVISIBLE);

        GONEXT= (Button) findViewById(R.id.btnNext);

        GONEXT.setText("TUESDAY");

        CB= (CheckBox) findViewById(R.id.IdCheckBox);

        Spinner spinner = (Spinner) findViewById(R.id.spinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.Transportation_array, android.R.layout.simple_spinner_item);
// Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
// Apply the adapter to the spinner
        spinner.setAdapter(adapter);
    }


    //"WED","THU","FRI","SAT","SUN"
    public void goNEXT(View view) {
        //setContentView(R.layout.activity_enter_scedule);
        switch (DayID){
            case 1:
                ///CB.setChecked(false);
                DATABASEHANDLER.addProductSedule("MON");
                addData("MON");
                GOPREV.setVisibility(View.VISIBLE);
                DAY.setText("TUESDAY");GONEXT.setText("WEDSDAY");GOPREV.setText("MONDAY");
                //String test1=DATABASEHANDLER.databasetostringSedule(1);
                //test.setText(test1);
                DayID++;
                break;
            case 2:
                DATABASEHANDLER.addProductSedule("TUE");
                addData("TUE");
                //CB.setChecked(false);
                //setContentView(R.layout.activity_enter_scedule);
                DAY.setText("WEDSDAY");GONEXT.setText("THURSDAY");GOPREV.setText("TUESDAY");
                DayID++;
                break;
            case 3:
                DATABASEHANDLER.addProductSedule("WED");
                addData("WED");
                //CB.setChecked(false);
                DAY.setText("THURSDAY");GONEXT.setText("FRIDAY");GOPREV.setText("WEDSDAY");
                DayID++;
                break;
            case 4:
                DATABASEHANDLER.addProductSedule("THU");
                addData("THU");
                //CB.setChecked(false);
                DAY.setText("FRIDAY");GONEXT.setText("SATERDAY");GOPREV.setText("THURSDAY");
                DayID++;
                break;
            case 5:
                DATABASEHANDLER.addProductSedule("FRI");
                addData("FRI");
                //CB.setChecked(false);
                DAY.setText("SATERDAY");GONEXT.setText("SUNDAY");GOPREV.setText("FRIDAY");
                DayID++;
                break;
            case 6:
                DATABASEHANDLER.addProductSedule("SAT");
                addData("SAT");
                //CB.setChecked(false);
                DAY.setText("SUNDAY");GOPREV.setText("SATERDAY");
                GONEXT.setText("FINISH");DayID++;
                break;
            case 7:
                DATABASEHANDLER.addProductSedule("SUN");
                addData("SUN");
                Intent i = getBaseContext().getPackageManager().
                        getLaunchIntentForPackage(getBaseContext().getPackageName());
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(i);
                finish();
                break;

        }
    }

    public void goPREV(View view) {
        DayID--;
        switch (DayID){

            case 1:
                ///CB.setChecked(false);
                DAY.setText("TUESDAY");GONEXT.setText("WEDSDAY");GOPREV.setText("MONDAY");
                //DayID--;
                break;
            case 2:
                //CB.setChecked(false);
                //setContentView(R.layout.activity_enter_scedule);
                DAY.setText("WEDSDAY");GONEXT.setText("THURSDAY");GOPREV.setText("TUESDAY");
                //DayID--;
                break;
            case 3:
                //CB.setChecked(false);
                DAY.setText("THURSDAY");GONEXT.setText("FRIDAY");GOPREV.setText("WEDSDAY");
                //DayID--;
                break;
            case 4:
                //CB.setChecked(false);
                DAY.setText("FRIDAY");GONEXT.setText("SATERDAY");GOPREV.setText("THURSDAY");
                //DayID--;
                break;
            case 5:
                //CB.setChecked(false);
                DAY.setText("SATERDAY");GONEXT.setText("SUNDAY");GOPREV.setText("FRIDAY");
                //DayID--;
                break;
            case 6:
                //CB.setChecked(false);
                DAY.setText("SUNDAY");GOPREV.setText("SATERDAY");
                GONEXT.setText("FINISH");//DayID--;

                break;
        }
    }

    /*public void addDays(){
        String []Days={"MON","TUE","WED","THU","FRI","SAT","SUN"};
        for (int d=0;d<=6;d++){
            DATABASEHANDLER.addProductSedule(Days[d]);
        }
    }*/

    public void addData(String day){
        int index1=0;
        String allItems="";//get all item to upadate database(ex: Purse#Umbrella#....)
        int len=Items.size();
        while (index1<len){
            allItems=Items.get(index1)+"#"+allItems;

            index1++;
        }
        DATABASEHANDLER.updateDataSedule(day,allItems,Location.getText().toString(),Time.getText().toString(),spinner.getSelectedItem().toString());
    }

    public void aaa(View view) {
        boolean checked=((CheckBox)view).isChecked();

        switch (((CheckBox) view).getText().toString()){

            case "Purse": if(checked){
                Items.add("Purse");
                // test.setText(Items.get(2));

            }else {
                Items.remove("Purse");
            }index++;
                break;

            case  "Car Keys": if(checked){
                Items.add("Car Keys");

            }else {
                Items.remove("Car Keys");
            }index++;
                break;

            case "Door Keys": if(checked){
                Items.add("Door Keys");

            }else {
                Items.remove("Door Keys");
            }index++;
                break;

            case  "Umbrella": if(checked){
                Items.add("Umbrella");

            }else {
                Items.remove("Umbrella");
            }index++;
                break;

            case "Water Bottle": if(checked){
                Items.add("Water Bottle");
            }else {
                Items.remove("Water Bottle");
            }index++;

                break;

        }
    }
}
