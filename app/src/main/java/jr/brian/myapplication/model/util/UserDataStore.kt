package jr.brian.myapplication.model.util

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class UserDataStore(private val context: Context) {

    companion object {
        private val Context.dataStore:
                DataStore<Preferences> by preferencesDataStore("user-info")
        val USER_EMAIL_KEY = stringPreferencesKey("user_email")
        val USER_PASSWORD_KEY = stringPreferencesKey("user_password")
    }

    val getEmail: Flow<String?> = context.dataStore.data
        .map { preferences ->
            preferences[USER_EMAIL_KEY]
        }

    val getPassword: Flow<String?> = context.dataStore.data
        .map { preferences ->
            preferences[USER_PASSWORD_KEY]
        }

    suspend fun clear() {
        context.dataStore.edit { preferences ->
            preferences.clear()
        }
    }

    suspend fun saveUser(email: String, password: String) {
        saveEmail(email)
        savePassword(password)
    }

    private suspend fun saveEmail(email: String) {
        context.dataStore.edit { preferences ->
            preferences[USER_EMAIL_KEY] = email
        }
    }

    private suspend fun savePassword(password: String) {
        context.dataStore.edit { preferences ->
            preferences[USER_PASSWORD_KEY] = password
        }
    }
}