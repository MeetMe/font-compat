# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in /Users/jhansche/Development/adt-bundle-mac-x86_64-20130219/sdk/tools/proguard/proguard-android.txt
# You can edit the include path and order by changing the proguardFiles
# directive in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# Add any project specific keep options here:

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

-keep class android.support.annotation.Keep

-keepclassmembers @android.support.annotation.Keep class * {
    <fields>;
}

-keepclassmembers class * {
    @android.support.annotation.Keep *;
}

-keep class com.meetme.support.fonts.FontManager
-keep class com.meetme.support.fonts.FontManager$FontManagerImpl

-keepclassmembers class com.meetme.support.fonts.FontManager {
    public boolean install(android.content.Context, int);
}

-keep class * implements com.meetme.support.fonts.FontManager$FontManagerImpl {
    public boolean init(android.content.Context, int);
}