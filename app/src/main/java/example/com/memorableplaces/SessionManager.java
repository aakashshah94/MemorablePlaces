package example.com.memorableplaces;

import android.content.Context;
import android.content.SharedPreferences;

public class SessionManager {

   private SharedPreferences pref;
   private int PRIVATE_MODE =0;
   private static final String PREF_NAME = "Memorable Locations";
   static final String KEY_PLACES_NAME = "places";
   static final String KEY_LAT_LONG ="latlong";
   private Context _context;

    SessionManager(Context context)
   {
       this._context=context;
       pref = context.getSharedPreferences(PREF_NAME,PRIVATE_MODE);
   }

   public void storeLocations(String placesNames, String latlong)
   {
        pref.edit().putString(KEY_PLACES_NAME,placesNames).apply();
        pref.edit().putString(KEY_LAT_LONG,latlong).apply();
   }

   public String getKeyPlacesName()
   {
       return pref.getString(KEY_PLACES_NAME,null);
   }

   public String getKeyLatLong()
   {
       return pref.getString(KEY_LAT_LONG,null);
   }


}
