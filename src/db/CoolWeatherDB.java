package db;

import java.util.ArrayList;
import java.util.List;

import model.City;
import model.County;
import model.Province;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

/*
 * 这个类将会把一些常用的数据库操作封装起来，以便之后调用
 */
public class CoolWeatherDB {

	//数据库名
	public static final String DB_NAME="cool_weather";
	
	//数据库版本
	public static final int VERSION = 1;
	
	private static CoolWeatherDB coolWeatherDB;
	
	private SQLiteDatabase db;
	
	/*
	 * 构造方法私有化
	 */
	
	private CoolWeatherDB(Context context)
	{
		CoolWeatherOpenHelper dbHelper = new CoolWeatherOpenHelper(context, DB_NAME, null, VERSION);
		db = dbHelper.getWritableDatabase();
	}
	
	/*
	 *获取CoolWeatherDB实例
	 */
	
	public synchronized static CoolWeatherDB getInstance(Context context)
	{
		if(coolWeatherDB==null)
			coolWeatherDB = new CoolWeatherDB(context);
		
		return coolWeatherDB;
	}
	
	/*
	 * 将Province实例存储到数据库中
	 */
   public void saveProvince(Province province)
   {
	   if(province!=null)
	   {
		   ContentValues values = new ContentValues();
		   values.put("province_name", province.getProvinceName());
		   values.put("province_code", province.getProvinceText());
		   db.insert(DB_NAME, null, values);
	   }
   }
   
   /*
    * 从数据库中读取全国所有身份的信息
    */
   
   public List<Province> loadProvince()
   {
	   List<Province> list = new ArrayList<Province>();
	   Cursor cursor = db.query(DB_NAME, null, null, null, null, null, null);
			   if(cursor.moveToNext())
			   {
				   do{
					   Province province = new Province();
					   province.setId(cursor.getInt(cursor.getColumnIndex("id")));
					   province.setProvinceName(cursor.getString(cursor.getColumnIndex("province_name")));
					   province.setProvinceText(cursor.getString(cursor.getColumnIndex("province_code")));
					   list.add(province);
					   
				   }while(cursor.moveToNext());
				   if(cursor!=null)
					   cursor.close();
			   }
	   return list;
   }
   
   public void saveCity(City city)
   {
	   if(city!=null)
	   {
		   ContentValues values = new ContentValues();
		   values.put("city_name", city.getCityName());
		   values.put("city_code", city.getCityCode());
		   values.put("province_id", city.getProvinceId());
		   db.insertOrThrow("City", null, values);
	   }
   }
   
   public List<City> loadCity(int provinceId)
   {
	   
	   List<City> list = new ArrayList<City>();
	   Cursor cursor = db.query("City", null, "province_id=?", new String[]{String.valueOf(provinceId)}, null, null, null);
	   
	   if(cursor.moveToNext())
	   {
		   do{
		   City city = new City();
		   city.setId(cursor.getInt(cursor.getColumnIndex("id")));
		   city.setCityName(cursor.getString(cursor.getColumnIndex("city_name")));
		   city.setCityCode(cursor.getString(cursor.getColumnIndex("city_code")));
		   city.setProvinceId(provinceId);
		   list.add(city);
		   }while(cursor.moveToNext());
	   }
	   if(cursor!=null)
	   {
		   cursor.close();
	   }
	   return list;
   }
   
   public void saveCounty(County county)
   {
	   if(county!=null)
	   {
		   ContentValues values = new ContentValues();
		   values.put("county_name", county.getCountyName());
		   values.put("county_code", county.getCountyCode());
		   values.put("city_id", county.getCityId());
		   db.insert("county", null, values);
	   }
   }
   
   public List<County> loadCounty(int cityId)
   {
	
	   List<County> list = new ArrayList<County>();
	   Cursor cursor = db.query("County", null, "city_id = ?", new String[] {String.valueOf(cityId)}, null, null, null);
	   if(cursor.moveToNext())
	   {
		   do{
			   County county = new County();
			   county.setCountyCode(cursor.getString(cursor.getColumnIndex("county_code")));
			   county.setCityId(cityId);
			   county.setId(cursor.getInt(cursor.getColumnIndex("id")));
			   county.setCountyName(cursor.getString(cursor.getColumnIndex("county_name")));
			   list.add(county);
		   }while(cursor.moveToNext());
		   
		   
	   }
	   if(cursor!=null){
		   cursor.close();
	   }
	   
	   return list;
   }
  
	
}
