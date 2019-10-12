package han.Chensing.Satellite;

import android.app.*;
import android.os.*;
import android.view.*;
import android.content.*;

public class Welcome extends Activity
{
	
	boolean isjump=false;
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
		setContentView(R.layout.welcome);
		new Handler().postDelayed(new Runnable(){
			@Override
			public void run(){
				ju();
			}
		},2000);
	}
	
	public void jump(View v){
		ju();
	}
	
	private void ju(){
		if(!isjump){
			isjump=true;
			Intent in=new Intent(this,MainActivity.class);
			startActivity(in);
			finish();
		}
	}
}
