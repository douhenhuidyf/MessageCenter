package com.example.messagecenter.data.repository

import android.content.Context
import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "Settings")

class PreferencesRepository(private val context: Context) {
    private val dataStore = context.dataStore

    private companion object {
        val AUTO_DARK = booleanPreferencesKey("auto_dark")
        val THEME_MODE = intPreferencesKey("theme_mode")
        val LANGUAGE_CODE = stringPreferencesKey("language_code")

        val ENABLE_DEV_MODE = booleanPreferencesKey("enable_dev_mode")
        val ENABLE_RECEIVING = booleanPreferencesKey("enable_receiving")

        val RECEIVED_MESSAGE_ID = intPreferencesKey("received_message_id")
    }

    val autoDark: Flow<Boolean> = dataStore.data
        .catch { exception ->
            if (exception is IOException) {
                Log.e("PreferencesRepo", "Error reading preferences.", exception)
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }
        .map { preferences ->
            preferences[AUTO_DARK] ?: true
        }

    val themeMode: Flow<Int> = dataStore.data
        .catch { exception ->
            if (exception is IOException) {
                Log.e("PreferencesRepo", "Error reading preferences.", exception)
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }
        .map { preferences ->
            preferences[THEME_MODE] ?: 0
        }

    val languageCode: Flow<String> = dataStore.data
        .catch { exception ->
            if (exception is IOException) {
                Log.e("PreferencesRepo", "Error reading preferences.", exception)
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }
        .map { preferences ->
            preferences[LANGUAGE_CODE] ?: "zh"
        }

    val enableDevMode: Flow<Boolean> = dataStore.data
        .catch { exception ->
            if (exception is IOException) {
                Log.e("PreferencesRepo", "Error reading preferences.", exception)
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }
        .map { preferences ->
            preferences[ENABLE_DEV_MODE] ?: false
        }

    val enableReceiving: Flow<Boolean> = dataStore.data
        .catch { exception ->
            if (exception is IOException) {
                Log.e("PreferencesRepo", "Error reading preferences.", exception)
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }
        .map { preferences ->
            preferences[ENABLE_RECEIVING] ?: false
        }

        val receivedMessageId: Flow<Int> = dataStore.data
            .catch { exception ->
                if (exception is IOException) {
                    Log.e("PreferencesRepo", "Error reading preferences.", exception)
                    emit(emptyPreferences())
                } else {
                    throw exception
                }
            }
            .map { preferences ->
                preferences[RECEIVED_MESSAGE_ID] ?: 0
            }

    suspend fun saveAutoDarkPreference(autoDark: Boolean) {
        dataStore.edit { preferences ->
            preferences[AUTO_DARK] = autoDark
        }
    }

    suspend fun saveThemeModePreference(themeMode: Int) {
        dataStore.edit { preferences ->
            preferences[THEME_MODE] = themeMode
        }
    }

    suspend fun saveLanguageCodePreference(languageCode: String) {
        dataStore.edit { preferences ->
            preferences[LANGUAGE_CODE] = languageCode
        }
    }

    suspend fun saveEnableDevModePreference(enableDevMode: Boolean) {
        dataStore.edit { preferences ->
            preferences[ENABLE_DEV_MODE] = enableDevMode
        }
    }

    suspend fun saveEnableReceivingPreference(enableReceiving: Boolean) {
        dataStore.edit { preferences ->
            preferences[ENABLE_RECEIVING] = enableReceiving
        }
    }

    suspend fun saveReceivedMessageIdPreference(receivedMessageId: Int) {
        dataStore.edit { preferences ->
            preferences[RECEIVED_MESSAGE_ID] = receivedMessageId
        }
    }
}
