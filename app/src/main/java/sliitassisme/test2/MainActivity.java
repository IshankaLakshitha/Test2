package sliitassisme.test2;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import sliitassisme.test2.FirstTime.FirstTimeDevicesActivity;
import sliitassisme.test2.database.DBhandler;

public class MainActivity extends AppCompatActivity {

    public static DBhandler DATABASEHANDLER;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        DATABASEHANDLER=new DBhandler(this,null,null,1);

        Intent intent=new Intent(this, FirstTimeDevicesActivity.class);
        startActivity(intent);

    }
}
