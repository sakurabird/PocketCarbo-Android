-keepattributes SourceFile,LineNumberTable,Exceptions
-keepnames class * extends java.lang.Throwable

# ----------------------------------------
# RxJava
# ----------------------------------------
-dontwarn rx.internal.util.unsafe.**
-keep class rx.schedulers.Schedulers {
    public static <methods>;
}
-keep class rx.schedulers.ImmediateScheduler {
    public <methods>;
}
-keep class rx.schedulers.TestScheduler {
    public <methods>;
}
-keep class rx.schedulers.Schedulers {
    public static ** test();
}


# ----------------------------------------
# Support Library
# ----------------------------------------
-dontwarn android.support.**
-keep class android.support.** { *; }

# http://stackoverflow.com/questions/40176244/how-to-disable-bottomnavigationview-shift-mode
-keepclassmembers class android.support.design.internal.BottomNavigationMenuView {
    boolean mShiftingMode;
}

# ----------------------------------------
# Retrofit and OkHttp
# ----------------------------------------
-dontwarn com.squareup.okhttp3.**
-dontwarn okio.**
-dontwarn retrofit2.**


# ----------------------------------------
# Picasso
# ----------------------------------------
-dontwarn com.squareup.picasso.**


# ----------------------------------------
# retrolambda
# ----------------------------------------
-dontwarn java.lang.invoke.*


# ----------------------------------------
# Icepick
# ----------------------------------------
-dontwarn icepick.**
-keep class icepick.** { *; }
-keep class **$$Icepick { *; }
-keepclasseswithmembernames class * {
    @icepick.* <fields>;
}
-keepnames class * { @icepick.State *;}


# ----------------------------------------
# Mauker1/MaterialSearchView
# ----------------------------------------
-keep class br.com.mauker.MsvAuthority
-keepclassmembers class br.com.mauker.** { *; }

# ----------------------------------------
# App
# ----------------------------------------
-keep class com.konifar.droidkaigi2017.** { *; }
-keepnames class ** { *; }


# ----------------------------------------
# Android and Java
# ----------------------------------------
-optimizationpasses 5
-dontusemixedcaseclassnames
-dontskipnonpubliclibraryclasses
-dontskipnonpubliclibraryclassmembers
-dontpreverify
-verbose
-dump class_files.txt
-printseeds seeds.txt
-printusage unused.txt
-printmapping mapping.txt
-optimizations !code/simplification/arithmetic,!field/*,!class/merging/*

-allowaccessmodification
-keepattributes *Annotation*
-renamesourcefileattribute SourceFile
-keepattributes SourceFile,LineNumberTable
-repackageclasses ''

-keep public class * extends android.app.Activity
-keep public class * extends android.app.Application
-keep public class * extends android.app.Service
-keep public class * extends android.content.BroadcastReceiver
-keep public class * extends android.content.ContentProvider
-keep public class * extends android.app.backup.BackupAgentHelper
-keep public class * extends android.preference.Preference
-keep public class com.android.vending.licensing.ILicensingService
-dontnote com.android.vending.licensing.ILicensingService

-keepclassmembers class * implements java.io.Serializable {
    static final long serialVersionUID;
    private static final java.io.ObjectStreamField[] serialPersistentFields;
    private void writeObject(java.io.ObjectOutputStream);
    private void readObject(java.io.ObjectInputStream);
    java.lang.Object writeReplace();
    java.lang.Object readResolve();
}

-keepclasseswithmembernames class * {
    native <methods>;
}

-keepclasseswithmembernames class * {
    public <init>(android.content.Context, android.util.AttributeSet);
}

-keepclasseswithmembernames class * {
    public <init>(android.content.Context, android.util.AttributeSet, int);
}

-keepclassmembers class **.R$* {
  public static <fields>;
}

-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

-keep class * implements android.os.Parcelable {
  public static final android.os.Parcelable$Creator *;
}

-dontwarn android.databinding.**
-dontwarn org.antlr.v4.**

# ----------------------------------------
# https://stackoverflow.com/a/41932540
# org.gradle.api.tasks.TaskExecutionException:
# Execution failed for task ':app:transformClassesAndResourcesWithProguardForRelease'.
# ----------------------------------------
-ignorewarnings
-keep class * {
    public private *;
}

# ----------------------------------------
# Clashlytics
# ----------------------------------------
-keepattributes *Annotation*
-keepattributes SourceFile,LineNumberTable
-keep public class * extends java.lang.Exception
-printmapping mapping.txt
-keep class com.crashlytics.** { *; }
-dontwarn com.crashlytics.**
