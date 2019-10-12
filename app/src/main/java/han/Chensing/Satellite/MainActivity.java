package han.Chensing.Satellite;

import android.app.*;
import android.os.*;
import android.support.v4.widget.*;
import android.view.*;
import android.widget.*;
import android.location.*;
import android.content.*;
import java.util.*;

public class MainActivity extends Activity 
{
	
	LocationManager lm;
	
	boolean fresh=false;
	boolean isOpen=false;

	ArrayAdapter<String> la;
	
	Handler han;
	
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
		
		android.app.ActionBar ab=MainActivity.this.getActionBar();
		ab.setCustomView(R.layout.action);
		ab.setDisplayShowCustomEnabled(true);
		ab.setDisplayHomeAsUpEnabled(false);
		ab.show();
		
		han=new Handler(getMainLooper());
		
		final SwipeRefreshLayout srl=(SwipeRefreshLayout)findViewById(R.id.mainsrl);
		srl.setEnabled(false);
		srl.setColorSchemeResources(
			android.R.color.holo_blue_bright,
			android.R.color.holo_green_light,
			android.R.color.holo_orange_light,
			android.R.color.holo_red_light);
		lm=(LocationManager)getSystemService(Context.LOCATION_SERVICE);

		ListView lv=(ListView)findViewById(R.id.mainlv);
		la=new ArrayAdapter<String>(this,R.layout.list,new ArrayList<String>());

		ImageButton ib=(ImageButton)findViewById(R.id.ref);
		ib.setEnabled(false);
		ib.setAlpha(100);
		
		lv.setAdapter(la);
		srl.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener(){
			@Override
			public void onRefresh(){
				ref();
			}
		});
		
		
		DrawerLayout dl=(DrawerLayout)findViewById(R.id.dr);
		dl.setDrawerListener(new DrawerLayout.DrawerListener(){
			@Override
			public void onDrawerStateChanged(int in){}
			
			@Override
			public void onDrawerOpened(View v){
				ImageButton ib=(ImageButton)findViewById(R.id.set);
				ib.setImageDrawable(getResources().getDrawable(R.drawable.ic_close_black_48dp));
				isOpen=true;
			}
			
			@Override
			public void onDrawerClosed(View v){
				ImageButton ib=(ImageButton)findViewById(R.id.set);
				ib.setImageDrawable(getResources().getDrawable(R.drawable.ic_menu_black_48dp));
				isOpen=false;
			}
			
			@Override
			public void onDrawerSlide(View v,float fl){}
		});
		
    }
	
	public void backno(View v){
		EditText et=(EditText)findViewById(R.id.menurefjg);
		CheckBox auto=(CheckBox)findViewById(R.id.menuautoref);
		CheckBox bjd=(CheckBox)findViewById(R.id.menubjd);
		et.setText("1000");
		et.setEnabled(true);
		auto.setChecked(true);
		auto.setEnabled(true);
		bjd.setChecked(false);
	}

	@Override
	protected void onDestroy()
	{
		super.onDestroy();
		lm.removeGpsStatusListener(gs);
		lm.removeUpdates(ll);
	}
	
	
	
	public void help(View v){
		AlertDialog.Builder ab=new AlertDialog.Builder(this);
		ab.setView(R.layout.help);
		ab.show();
	}
	
	public void about(View v){
		AlertDialog.Builder ab=new AlertDialog.Builder(this);
		ab.setView(R.layout.about);
		ab.show();
	}
	
	
	
	
	public void refh(View v){
		ref();
	}
	
	public void menu(View v){
		DrawerLayout dl=(DrawerLayout)findViewById(R.id.dr);
		if(!isOpen){
			dl.openDrawer(Gravity.RIGHT);
		}else{
			dl.closeDrawer(Gravity.RIGHT);
		}
	}
	
	public void bjd(View v){
		EditText et=(EditText)findViewById(R.id.menurefjg);
		CheckBox auto=(CheckBox)findViewById(R.id.menuautoref);
		CheckBox bjd=(CheckBox)findViewById(R.id.menubjd);
		if(bjd.isChecked()){
			auto.setEnabled(false);
			auto.setChecked(true);
			et.setEnabled(false);
		}else{
			auto.setEnabled(true);
			et.setEnabled(true);
		}
	}
	
	public void autof(View v){
		EditText et=(EditText)findViewById(R.id.menurefjg);
		CheckBox auto=(CheckBox)findViewById(R.id.menuautoref);
		if(auto.isChecked()){
			et.setEnabled(true);
		}else{
			et.setEnabled(false);
		}
	}
	
	public void start(View v){
		final SwipeRefreshLayout srl=(SwipeRefreshLayout)findViewById(R.id.mainsrl);
		ToggleButton tb=(ToggleButton)findViewById(R.id.mainstart);
		ImageButton ib=(ImageButton)findViewById(R.id.ref);
		EditText auto=(EditText)findViewById(R.id.menurefjg);
		CheckBox cb=(CheckBox)findViewById(R.id.menuautoref);
		CheckBox bjd=(CheckBox)findViewById(R.id.menubjd);
		if(tb.isChecked()){
			if(!lm.isProviderEnabled(LocationManager.GPS_PROVIDER)){
				Toast.makeText(this,"请开启GPS",Toast.LENGTH_LONG).show();
				tb.setChecked(false);
				return;
			}
			ib.setEnabled(true);
			ib.setAlpha(255);
			srl.setEnabled(true);
			
			int time;
			if(!cb.isChecked()){
				time=0;
			}else{
				time=Integer.valueOf(auto.getText().toString());
			}
			if(bjd.isChecked()){
				time=0;
			}
			lm.requestLocationUpdates(lm.GPS_PROVIDER,time,0,ll);
			lm.addGpsStatusListener(gs);
		}else{
			ib.setEnabled(false);
			ib.setAlpha(100);
			lm.removeUpdates(ll);
			lm.removeGpsStatusListener(gs);
			srl.setEnabled(false);
		}
	}
	
	private LocationListener ll=new LocationListener(){
		@Override
		public void onLocationChanged(Location lo){}
		@Override
		public void onStatusChanged(String pro,int stat,Bundle ext){}
		@Override
		public void onProviderEnabled(String pro){}
		@Override
		public void onProviderDisabled(String pro){}
	};
	
	private GpsStatus.Listener gs=new GpsStatus.Listener(){

		@Override
		public void onGpsStatusChanged(int p1)
		{
			if(GpsStatus.GPS_EVENT_SATELLITE_STATUS==p1){
				ArrayList<String> al=new ArrayList<>();
				GpsStatus gs=lm.getGpsStatus(null);
				int maxwx=gs.getMaxSatellites();
				java.util.Iterator<GpsSatellite> it=gs.getSatellites().iterator();
				try{
					for(int i=0;it.hasNext() && i<=maxwx;i++){
						GpsSatellite gsa=it.next();
						al.add(get(gsa,i+1));
					}
				}catch(Exception e){
					e.printStackTrace();
				}
				change(al);
				//srl.setRefreshing(false);
			}
		}
	};
	
	private String get(GpsSatellite gs,int num){
		StringBuilder al=new StringBuilder();
		al.append(" 卫星"+num);
		al.append("\n     方位角:"+gs.getAzimuth());
		al.append("\n     高度角:"+gs.getElevation());
		al.append("\n     信噪比:"+gs.getSnr());
		al.append("\n     伪随机噪声码:"+gs.getPrn());
		if(gs.hasAlmanac()){
			al.append("\n     GPS引擎有近似轨道信息");
		}else{
			al.append("\n     GPS引擎无近似轨道信息");
		}
		if(gs.hasEphemeris()){
			al.append("\n     GPS引擎有卫星星历");
		}else{
			al.append("\n     GPS引擎无卫星星历");
		}
		if(gs.usedInFix()){
			al.append("\n     卫星被GPS引擎用于计算最近位置");
		}else{
			al.append("\n     卫星不被GPS引擎用于计算最近位置");
		}
		return al.toString();
	}
	
	private void change(List<String> li){
		CheckBox cb=(CheckBox)findViewById(R.id.menuautoref);
		if(fresh||cb.isChecked()){
			SwipeRefreshLayout srl=(SwipeRefreshLayout)findViewById(R.id.mainsrl);
			ImageButton ib=(ImageButton)findViewById(R.id.ref);
			ToggleButton tbn=(ToggleButton)findViewById(R.id.mainstart);
			la.clear();
			la.addAll(li);
			fresh=false;
			srl.setRefreshing(false);
			ib.setEnabled(true);
			ib.setAlpha(255);
			tbn.setEnabled(true);
		}
	}
	
	private void ref(){
		SwipeRefreshLayout srl=(SwipeRefreshLayout)findViewById(R.id.mainsrl);
		ImageButton ib=(ImageButton)findViewById(R.id.ref);
		ToggleButton tbn=(ToggleButton)findViewById(R.id.mainstart);
		ib.setEnabled(false);
		ib.setAlpha(100);
		srl.setRefreshing(true);
		fresh=true;
		tbn.setEnabled(false);
	}
}
