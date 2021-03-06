package util;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import model.City;
import model.County;
import model.Province;

import org.json.JSONObject;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import db.CoolWeatherDB;


public class Utility {

	/*
	 * 解析和处理服务器返回来的省级数据
	 */
	public synchronized static boolean handleProvinceResponse(CoolWeatherDB coolWeatherDB,String response)
	{
		/*
		 * 对于字符串处理Android为我们提供了一个简单实用的TextUtils类，
		 * 如果处理比较简单的内容不用去思考正则表达式不妨试试这个在android.text.TextUtils的类，主要的功能如下:
                             是否为空字符 boolean android.text.TextUtils.isEmpty(CharSequence str) 
		 */
		if(!TextUtils.isEmpty(response))
		{
			String[] allProvinces = response.split(",");
			if(allProvinces!=null && allProvinces.length>0)
			{
				for(String p :allProvinces)
				{
					String[] array = p.split("\\|");
					Province province = new Province();
					province.setProvinceText(array[0]);
					province.setProvinceName(array[1]);
					coolWeatherDB.saveProvince(province);
				}
				return true;
			}
		}
		return false;
	}
	
	public synchronized static boolean handleCityResponse(CoolWeatherDB coolWeatherDB,String response,int provinceId)
	
	{
		String[] allCitys = response.split(",");
		if(allCitys != null && allCitys.length >= 0)
		{
			for(String p :allCitys)
			{
				City city = new City();
				String[] array = p.split("\\|");
				city.setCityCode(array[0]);
				city.setCityName(array[1]);
				city.setProvinceId(provinceId);
				coolWeatherDB.saveCity(city);
				return true;
				
			}
		}
		
		return false;
	}
	
	public synchronized static boolean handleCountyHandler(CoolWeatherDB coolWeatherDB,String response,int CityId)
	{
		String allCounty[] = response.split(",");
		if(allCounty != null && allCounty.length>=0)
		{
			for ( String C : allCounty)
			{
				County county = new County();
				String array[] = C.split("\\|");
				county.setCountyCode(array[0]);
				county.setCountyName(array[1]);
				county.setCityId(CityId);
				
				return true;
			}
		}
		return false;
	}
	/*
	 * 解析服务器返回的JSON数据，并将解出的数据存储到本地
	 */
	public static  void handleWeatherResponse(Context context , String response)
	{
		try
		{
			JSONObject jsonObject = new JSONObject(response);
			JSONObject weatherInfo = jsonObject.getJSONObject("weatherinfo");
			String cityName = weatherInfo.getString("city");
			String weatherCode = weatherInfo.getString("cityid");
			String temp1 = weatherInfo.getString("temp1");
			String temp2 = weatherInfo.getString("temp2");
			String weatherDesp = weatherInfo.getString("weather");
			String publishTime = weatherInfo.getString("ptime");
			saveWeatherInfo(context,cityName,weatherCode,temp1,temp2,weatherDesp,publishTime);
			
		}catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	
	/*
	 * 将服务器返回的所有信息存储到SharedPreferences文件中
	 */
	
	public static void saveWeatherInfo(Context context,String cityName,String weatherCode,String temp1,String temp2,String weatherDesp,String publishTime)
	{
		SimpleDateFormat sdf = new SimpleDateFormat ("yyyy年M月d日",Locale.CHINA);
		
		SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(context).edit();
		editor.putBoolean("city_selected", true);
		editor.putString("city_name", cityName);
		editor.putString("weather_code", weatherCode);
		editor.putString("temp1", temp1);
		editor.putString("temp2", temp2);
		editor.putString("weather_desp", weatherDesp);
		editor.putString("publish_time", publishTime);
		editor.putString("current_date", sdf.format(new Date()));
		editor.commit();
	}
}
