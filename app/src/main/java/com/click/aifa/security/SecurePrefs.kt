package com.click.aifa.security
import android.content.Context
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey

object SecurePrefs {

    private const val FILE_NAME = "aifa_secure_prefs"
    private const val KEY_USERNAME = "username"
    private const val KEY_PASSWORD = "password"
    private const val KEY_BIOMETRIC = "biometric_enabled"

    private fun getPrefs(context: Context) =
        EncryptedSharedPreferences.create(
            context,
            FILE_NAME,
            MasterKey.Builder(context)
                .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
                .build(),
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )

    fun saveCredentials(context: Context, username: String, password: String) {
        getPrefs(context).edit()
            .putString(KEY_USERNAME, username)
            .putString(KEY_PASSWORD, password)
            .apply()
    }

    fun getUsername(context: Context): String? =
        getPrefs(context).getString(KEY_USERNAME, null)

    fun getPassword(context: Context): String? =
        getPrefs(context).getString(KEY_PASSWORD, null)

    fun enableBiometric(context: Context, enabled: Boolean) {
        getPrefs(context).edit()
            .putBoolean(KEY_BIOMETRIC, enabled)
            .apply()
    }

    fun isBiometricEnabled(context: Context): Boolean =
        getPrefs(context).getBoolean(KEY_BIOMETRIC, false)

    fun clear(context: Context) {
        getPrefs(context).edit().clear().apply()
    }
}
