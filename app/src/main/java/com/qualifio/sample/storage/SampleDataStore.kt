package com.qualifio.sample.storage

import android.app.Application
import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import javax.inject.Inject

val Context.dataStore by preferencesDataStore(name = "com.qualifio.sample.datastore")
private val KEY_IS_CONFIGURATION_FINISHED = booleanPreferencesKey("is_configuration_finished")
private val KEY_DOMAIN = stringPreferencesKey("configuration_domain")
private val KEY_USE_SECURE_TOKEN = booleanPreferencesKey("use_secure_token")
private val KEY_SHOW_CAMPAIGN_AT_LAUNCH = booleanPreferencesKey("show_campaign_at_launch")
private val KEY_SECURE_TOKEN_ALGORITHM = stringPreferencesKey("secure_token_algorithm")
private val KEY_SECURE_TOKEN_SECRET = stringPreferencesKey("secure_token_secret")
private val KEY_SECURE_TOKEN_FIELDS = stringPreferencesKey("secure_token_fields")

class SampleDataStore @Inject constructor(
    private val application: Application,
    private val moshi: Moshi,
) {
    val isConfigurationFinishedStream: Flow<Boolean> =
        application.dataStore.data.map { preferences ->
            preferences[KEY_IS_CONFIGURATION_FINISHED] ?: false
        }

    val domainStream: Flow<String?> =
        application.dataStore.data.map { preferences ->
            preferences[KEY_DOMAIN]
        }

    val useSecureTokenStream: Flow<Boolean> =
        application.dataStore.data.map { preferences ->
            preferences[KEY_USE_SECURE_TOKEN] ?: false
        }

    val showCampaignAtLaunchStream: Flow<Boolean> =
        application.dataStore.data.map { preferences ->
            preferences[KEY_SHOW_CAMPAIGN_AT_LAUNCH] ?: true
        }

    val secureTokenAlgorithmStream: Flow<String> =
        application.dataStore.data.map { preferences ->
            preferences[KEY_SECURE_TOKEN_ALGORITHM] ?: "HS256"
        }

    val secureTokenSecretStream: Flow<String> =
        application.dataStore.data.map { preferences ->
            preferences[KEY_SECURE_TOKEN_SECRET] ?: "8uJ5FVQb\$fzR!ypskjnzfqojzeoijfoai"
        }

    val secureTokenFieldsStream: Flow<Map<String, String>?> =
        application.dataStore.data.map { preferences ->
            val str = preferences[KEY_SECURE_TOKEN_FIELDS] ?: return@map null

            val type =
                Types.newParameterizedType(
                    Map::class.java,
                    String::class.java,
                    String::class.java,
                )

            val adapter = moshi.adapter<Map<String, String>>(type)
            return@map adapter.fromJson(str)
        }

    suspend fun updateIsConfigurationFinished(finished: Boolean): Unit =
        withContext(Dispatchers.IO) {
            application.dataStore.edit { preferences ->
                preferences[KEY_IS_CONFIGURATION_FINISHED] = finished
            }
        }

    suspend fun updateDomain(domain: String?): Unit =
        withContext(Dispatchers.IO) {
            application.dataStore.edit { preferences ->
                if (domain != null) {
                    preferences[KEY_DOMAIN] = domain
                } else {
                    preferences.remove(KEY_DOMAIN)
                }
            }
        }

    suspend fun updateUseSecureToken(enabled: Boolean) {
        application.dataStore.edit { preferences ->
            preferences[KEY_USE_SECURE_TOKEN] = enabled
        }
    }

    suspend fun updateShowCampaignAtLaunch(enabled: Boolean) {
        application.dataStore.edit { preferences ->
            preferences[KEY_SHOW_CAMPAIGN_AT_LAUNCH] = enabled
        }
    }

    suspend fun updateSecureTokenFields(fields: Map<String, String>?) {
        val str =
            moshi.adapter<Map<String, String>>(
                Types.newParameterizedType(
                    Map::class.java,
                    String::class.java,
                    String::class.java,
                ),
            ).toJson(fields)

        application.dataStore.edit { preferences ->
            if (str != null) {
                preferences[KEY_SECURE_TOKEN_FIELDS] = str
            } else {
                preferences.remove(KEY_SECURE_TOKEN_FIELDS)
            }
        }
    }
}
