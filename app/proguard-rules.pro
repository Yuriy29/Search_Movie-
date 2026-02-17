# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# Uncomment this to preserve the line number information for
# debugging stack traces.
-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
-renamesourcefileattribute SourceFile

# ==================== Retrofit ====================
# Keep generic signature of Call, Response (R8 full mode strips signatures from non-kept items)
-keep,allowobfuscation,allowshrinking interface retrofit2.Call
-keep,allowobfuscation,allowshrinking class retrofit2.Response

# Keep generic signature of Call (R8 full mode strips signatures from non-kept items)
-keep,allowobfuscation,allowshrinking class * extends retrofit2.Call

# Keep inherited services
-if interface * { @retrofit2.http.* public *** *(...); }
-keep,allowobfuscation,allowshrinking class * extends <1>

# Keep class members used by Gson
-keepclassmembers,allowobfuscation class * {
  @com.google.gson.annotations.SerializedName <fields>;
}

# ==================== Gson ====================
# Gson uses generic type information stored in class files when working with fields
-keepattributes Signature

# For using Gson annotations
-keep class com.google.gson.** { *; }
-keep class com.google.gson.stream.** { *; }

# Keep class members used by Gson
-keepclassmembers class * {
    @com.google.gson.annotations.SerializedName <fields>;
}

# ==================== Room ====================
-keep class * extends androidx.room.RoomDatabase
-keep @androidx.room.Entity class *
-dontwarn androidx.room.paging.**

# ==================== Kotlin Coroutines ====================
-dontwarn kotlinx.coroutines.**
-keepclassmembers class kotlinx.coroutines.** { volatile <fields>; }

# ==================== OkHttp ====================
-dontwarn okhttp3.**
-dontwarn okio.**
-dontwarn javax.annotation.**

# ==================== Hilt ====================
-keep class dagger.hilt.** { *; }
-keep class * extends dagger.hilt.internal.GeneratedComponent

# ==================== Paging ====================
-dontwarn androidx.paging.**

# ==================== Coil ====================
-dontwarn coil.**

# ==================== Material Progress Indicator ====================
-keep class com.ehsanmsz.mszprogressindicator.** { *; }

# ==================== MovieExplorer Data Models ====================
# Keep data models used for API and database serialization
-keep class com.dopayurii.movie.data.model.** { *; }
-keep class com.dopayurii.movie.data.remote.** { *; }