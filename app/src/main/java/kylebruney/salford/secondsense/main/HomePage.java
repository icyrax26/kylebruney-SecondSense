package kylebruney.salford.secondsense.main;

import kylebruney.salford.secondsense.R;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;


public class HomePage extends Activity {
	
	Button btnMain;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.homepage);
        
        btnMain = (Button)findViewById(R.id.button1);
        btnMain.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent i = new Intent(HomePage.this, RecordPage.class);
				startActivity(i);
				finish();
			}
        	
        });
        
    }

}