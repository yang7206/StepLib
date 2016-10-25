package cn.ezon.www.ezonstepsdk;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import cn.ezon.www.steplib.ui.StepActivity;
import cn.ezon.www.steplib.ui.StepLibManager;

public class MainActivity extends AppCompatActivity implements StepLibManager.OnButtonClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("MainActivity", "onCreate");
        setContentView(R.layout.activity_main);
        findViewById(R.id.btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, StepActivity.class);
                startActivity(intent);
            }
        });
        StepLibManager.getInstance().init(MainActivity.this).registerListener(this);
        StepLibManager.getInstance().setUserMale(MainActivity.this, true);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        StepLibManager.getInstance().unregisterListener(this);
    }

    @Override
    public void onCompetitionClick() {
        Toast.makeText(MainActivity.this, "点击赛事", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onStadiumClick() {
        Toast.makeText(MainActivity.this, "点击场馆", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onBuyEzonWatchClick() {
        Toast.makeText(MainActivity.this, "点击购买宜准手表", Toast.LENGTH_LONG).show();
    }

}
