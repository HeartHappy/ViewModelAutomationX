package com.hearthappy.vma.model

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.byteArrayPreferencesKey
import androidx.datastore.preferences.core.doublePreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext

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
            is String    -> preferences[stringPreferencesKey(key)]
            is Int       -> preferences[intPreferencesKey(key.toString())]
            is Long      -> preferences[longPreferencesKey(key.toString())]
            is Boolean   -> preferences[booleanPreferencesKey(key.toString())]
            is Float     -> preferences[floatPreferencesKey(key.toString())]
            is Double    -> preferences[doublePreferencesKey(key.toString())]
            is ByteArray -> preferences[byteArrayPreferencesKey(key.toString())]
            else         -> Unit
        }

    }.toTypedArray()
    block(array)
}

suspend inline fun DataStore<Preferences>.readMultipleString(vararg keys: String, block: (Array<String?>) -> Unit) {
    val preferences = this.data.first()
    val array = keys.map {
        preferences[stringPreferencesKey(it)]
    }.toTypedArray()
    block(array)
}

suspend inline fun DataStore<Preferences>.readMultipleInt(vararg keys: String, block: (Array<Int?>) -> Unit) {
    val preferences = this.data.first()
    val array = keys.map {
        preferences[intPreferencesKey(it)]
    }.toTypedArray()
    block(array)
}

suspend inline fun DataStore<Preferences>.readMultipleLong(vararg keys: String, block: (Array<Long?>) -> Unit) {
    val preferences = this.data.first()
    val array = keys.map {
        preferences[longPreferencesKey(it)]
    }.toTypedArray()
    block(array)
}

suspend inline fun DataStore<Preferences>.readMultipleBoolean(vararg keys: String, block: (Array<Boolean?>) -> Unit) {
    val preferences = this.data.first()
    val array = keys.map {
        preferences[booleanPreferencesKey(it)]
    }.toTypedArray()
    block(array)
}

suspend inline fun DataStore<Preferences>.readMultipleFloat(vararg keys: String, block: (Array<Float?>) -> Unit) {
    val preferences = this.data.first()
    val array = keys.map {
        preferences[floatPreferencesKey(it)]
    }.toTypedArray()
    block(array)
}

suspend inline fun DataStore<Preferences>.readMultipleDouble(vararg keys: String, block: (Array<Double?>) -> Unit) {
    val preferences = this.data.first()
    val array = keys.map {
        preferences[doublePreferencesKey(it)]
    }.toTypedArray()
    block(array)
}

suspend inline fun DataStore<Preferences>.readMultipleByteArray(vararg keys: String, block: (Array<ByteArray?>) -> Unit) {
    val preferences = this.data.first()
    val array = keys.map {
        preferences[byteArrayPreferencesKey(it)]
    }.toTypedArray()
    block(array)
}