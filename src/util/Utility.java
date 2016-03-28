package util;

import model.City;
import model.County;
import model.Province;
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
}
