package com.click.aifa.ui.addTransaction

import android.content.Context
import com.click.aifa.data.Category
import com.google.firebase.crashlytics.buildtools.reloc.com.google.common.reflect.TypeToken
import com.google.gson.Gson

object CategoryPreference {

    private const val PREF_NAME = "category_pref"
    private const val KEY_CATEGORIES = "categories"
    private const val KEY_SELECTED = "selected_category"

    private fun prefs(context: Context) =
        context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)

    fun saveCategories(context: Context, categories: List<Category>) {
        val json = Gson().toJson(categories)
        prefs(context).edit().putString(KEY_CATEGORIES, json).apply()
    }

    fun getCategories(context: Context): MutableList<Category> {
        val json = prefs(context).getString(KEY_CATEGORIES, null) ?: return mutableListOf()
        val type = object : TypeToken<MutableList<Category>>() {}.type
        return Gson().fromJson(json, type)
    }

    fun saveSelected(context: Context, categoryName: String) {
        prefs(context).edit().putString(KEY_SELECTED, categoryName).apply()
    }

    fun getSelected(context: Context): String? {
        return prefs(context).getString(KEY_SELECTED, null)
    }
}
