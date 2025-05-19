package com.hearthappy.vma.model

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.doublePreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.first

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user_preferences")

//Preferences 写入
suspend fun DataStore<Preferences>.saveToDataStore(key: String, value: String) {
    this.edit { userinfo ->
        userinfo[stringPreferencesKey(key)] = value
    }
}

//Preferences 读取
suspend fun DataStore<Preferences>.read(key: String): String {
    return this.data.first()[stringPreferencesKey(key)] ?: "The value of key was not found"
}

suspend inline fun <reified R : Any> DataStore<Preferences>.readMultiple(vararg keys: R, block: (Array<Any?>) -> Unit) {
    val preferences = this.data.first()
    val array = keys.map { key ->
        when (key) {
            is String    -> preferences[stringPreferencesKey(key.toString())]
            is Int       -> preferences[intPreferencesKey(key.toString())]
            is Long      -> preferences[longPreferencesKey(key.toString())]
            is Boolean   -> preferences[booleanPreferencesKey(key.toString())]
            is Float     -> preferences[floatPreferencesKey(key.toString())]
            is Double    -> preferences[doublePreferencesKey(key.toString())]
//            is ByteArray -> preferences[byteArrayPreferencesKey(key.toString())]
            else         -> Unit
        }
    }.toTypedArray()
    block(array)
}
