package activity;

import java.util.ArrayList;
import java.util.List;

import model.City;
import model.County;
import model.Province;
import util.HttpCallbackListener;
import util.HttpUtil;
import util.Utility;
import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.coolweather.R;

import db.CoolWeatherDB;

public class ChooseAreaActivity extends Activity{

	public static final int LEVEL_PROVINCE = 0 ;
	public static final int LEVEL_CITY = 1;
	public static final int LEVEL_COUNTY =2 ;
	
	private ProgressDialog progressDialog;
	private TextView titleView;
	private ListView listView;
	private ArrayAdapter<String> adapter;
	private CoolWeatherDB coolWeatherDB;
	private List<String> datalist = new ArrayList<String>();
	
	/*
	 * 省，市，县列表
	 */
	private List<Province> provinceList;
	private List<City> cityList;
	private List<County> countyList;
	
	/*
	 * 选中的身份，城市
	 */
	private Province selectedProvince;
	private City selectedCity;
	
	private  int currentLevel;//当前选中的级别
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.choose_area);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		listView =(ListView)findViewById(R.id.list_view);
		titleView = (TextView)findViewById(R.id.title_text);
		adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, datalist);
		listView.setAdapter(adapter);
		coolWeatherDB = coolWeatherDB.getInstance(this);
		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View view, int index,
					long arg3) {
				// TODO Auto-generated method stub
				if(currentLevel==LEVEL_PROVINCE)
				{
					selectedProvince = provinceList.get(index);
					queryCities();
				}else if(currentLevel == LEVEL_CITY)
				{
					selectedCity = cityList.get(index);
				    queryCounty();	
				}
			}
		});
		
		queryProvinces();
		
	}
	
	/*
	 * 查询全国所有省，优先从数据库查询，如果没有查询到再去服务器上查询
	 */
	
	private void queryProvinces ()
	{
		provinceList = coolWeatherDB.loadProvince();
		if(provinceList.size()>0)
		{
			datalist.clear();
			for(Province province : provinceList)
			{
				datalist.add(province.getProvinceName());
			}
			adapter.notifyDataSetChanged();
			listView.setSelection(0);
			titleView.setText("中国");
			currentLevel = LEVEL_PROVINCE;
		}else
		{
			queryFromServer(null,"province");
		}
	}
	/*
	 * 查询选中省所有的市，优先从数据库查询，如果没有查询到再去服务器上查询
	 */

	private void queryCities()
	{
		cityList = coolWeatherDB.loadCity(selectedProvince.getId());
		if(cityList.size()>0)
		{
			datalist.clear();
			for(City city : cityList)
			{
				datalist.add(city.getCityName());
			}
			adapter.notifyDataSetChanged();
			listView.setSelection(0);
			titleView.setText(selectedProvince.getProvinceName());
			currentLevel = LEVEL_CITY;
		}else
		{
			queryFromServer(selectedProvince.getProvinceText(), "city");
		}
	}
	
	private void queryCounty()
	{
		countyList = coolWeatherDB.loadCounty(selectedCity.getId());
		if(countyList.size() > 0)
		{
			datalist.clear();
			for(County county : countyList)
			{
			   datalist.add(county.getCountyName());
			}
			adapter.notifyDataSetChanged();
			listView.setSelection(0);
			titleView.setText(selectedCity.getCityName());
			currentLevel = LEVEL_COUNTY;
		}else
		{
			queryFromServer(selectedCity.getCityCode(), "county");
		}
	}
	
	/*
	 * 根据传入的代号和类型从服务器上查询省市县的数据
	 */
	private void queryFromServer(final String code, final String type) {
		// TODO Auto-generated method stub
		String address;
		if(!TextUtils.isEmpty(code))
		{
			Log.d("ChooseAreaActivity", "lsy");
			address = "http://www.weather.com.cn/data/list3/city"+code+".xml";
		}else
		{
			address = "http://www.weather.com.cn/data/list3/city.xml";
		}
		showProgressDialog();
		HttpUtil.sendHttpRequet(address, new HttpCallbackListener() {
			
			@Override
			public void onFinish(String response) {
				// TODO Auto-generated method stub
				boolean result = false;
				if("province".equals(type)){
					result = Utility.handleProvinceResponse(coolWeatherDB, response);
				}else if("city".equals(type))
				{
					result = Utility.handleCityResponse(coolWeatherDB, response, selectedProvince.getId());
					
				}else if ("county".equals(type))
				{
					result = Utility.handleCountyHandler(coolWeatherDB, response, selectedCity.getId());
				}
				
				if(result)
				{
					//通过runOnUiThread()方法回到主线程处理逻辑
					
					runOnUiThread(new Runnable()
					{

						@Override
						public void run() {
							// TODO Auto-generated method stub
							closeProgressDialog();
							if("province".equals(type))
							{
								queryProvinces();
							}else if ("city".equals(type))
							{
								queryCities();
							}else if ("county".equals(type))
							{
								queryCounty();
							}
						}
						
					});
				}
			}
			
			@Override
			public void onError(Exception e) {
				// TODO Auto-generated method stub
			
				runOnUiThread(new Runnable() {
					
					@Override
					public void run() {
						// TODO Auto-generated method stub
						closeProgressDialog();
						Toast.makeText(ChooseAreaActivity.this, "加载失败", Toast.LENGTH_SHORT).show();
					}
				});
			}
		});
	}

	private void showProgressDialog() {
		// TODO Auto-generated method stub
		if(progressDialog==null)
		{
			progressDialog = new ProgressDialog(this);
			progressDialog.setMessage("正在加载中。。。");
			progressDialog.setCanceledOnTouchOutside(false);
		}
		progressDialog.show();
	}
	private void closeProgressDialog()
	{
		if(progressDialog!=null)
			progressDialog.dismiss();
	}
	/*
	 * 捕捉back按键，根据当前的级别判断，此时应该返回列表是省级，市级还是直接退出
	 */
	
	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		super.onBackPressed();
		
		if(currentLevel==LEVEL_COUNTY)
		{
			queryCities();
		}else if (currentLevel==LEVEL_CITY)
		{
			queryProvinces();
		}else{
			finish();
		}
	}
	
}
