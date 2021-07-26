package challenge.paypaycodechallenge.userpreferences

import android.content.Context
import android.content.SharedPreferences
import android.preference.PreferenceManager

object AppPreferenceHelper {
    val CUSTOM_PREF_NAME = "Currencies_Data"
    fun appPreferences(context: Context, name: String = CUSTOM_PREF_NAME): SharedPreferences =
        context.getSharedPreferences(name, Context.MODE_PRIVATE)

}

inline fun SharedPreferences.editMe(operation: (SharedPreferences.Editor) -> Unit) {
    val editMe = edit()
    operation(editMe)
    editMe.apply()
}

inline fun SharedPreferences.Editor.put(pair: Pair<String, Any>) {
    val key = pair.first
    val value = pair.second
    when (value) {
        is String -> putString(key, value)
        is Int -> putInt(key, value)
        is Boolean -> putBoolean(key, value)
        is Long -> putLong(key, value)
        is Float -> putFloat(key, value)
        else -> error("Only primitive types can be stored in SharedPreferences")
    }
}


//var SharedPreferences.proximity
//    get() = getInt(PreferenceHelper.PROXIMITY, 10)
//    set(value) {
//        editMe {
//            it.putInt(PreferenceHelper.PROXIMITY, value)
//        }
//    }

var SharedPreferences.clearValues
    get() = { }
    set(value) {
        editMe {
            /*it.remove(USER_ID)
            it.remove(USER_PASSWORD)*/
            it.clear()
        }
    }
