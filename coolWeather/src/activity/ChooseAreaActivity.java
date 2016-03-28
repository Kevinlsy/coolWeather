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
	 * ʡ���У����б�
	 */
	private List<Province> provinceList;
	private List<City> cityList;
	private List<County> countyList;
	
	/*
	 * ѡ�е���ݣ�����
	 */
	private Province selectedProvince;
	private City selectedCity;
	
	private  int currentLevel;//��ǰѡ�еļ���
	
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
	 * ��ѯȫ������ʡ�����ȴ����ݿ��ѯ�����û�в�ѯ����ȥ�������ϲ�ѯ
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
			titleView.setText("�й�");
			currentLevel = LEVEL_PROVINCE;
		}else
		{
			queryFromServer(null,"province");
		}
	}
	/*
	 * ��ѯѡ��ʡ���е��У����ȴ����ݿ��ѯ�����û�в�ѯ����ȥ�������ϲ�ѯ
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
	 * ���ݴ���Ĵ��ź����ʹӷ������ϲ�ѯʡ���ص�����
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
					//ͨ��runOnUiThread()�����ص����̴߳����߼�
					
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
						Toast.makeText(ChooseAreaActivity.this, "����ʧ��", Toast.LENGTH_SHORT).show();
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
			progressDialog.setMessage("���ڼ����С�����");
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
	 * ��׽back���������ݵ�ǰ�ļ����жϣ���ʱӦ�÷����б���ʡ�����м�����ֱ���˳�
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
