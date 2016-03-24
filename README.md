# Fonts Support Library

To date (as of Anrdoid N), Android does not provide an easy way to set a global
custom font typeface that will get applied across an application by default.
There is only one method, [`Typeface.createFromAssets()`](http://developer.android.com/reference/android/graphics/Typeface.html#createFromAsset(android.content.res.AssetManager, java.lang.String))
that enables loading a custom `.ttf` font file for use in an app, and then the
only way to apply that custom Typeface to text is by calling something like
[`TextView.setTypeface()`](http://developer.android.com/reference/android/widget/TextView.html#setTypeface(android.graphics.Typeface)).

Some workarounds have been floating around:  one is [using Reflection](http://stackoverflow.com/questions/2711858/is-it-possible-to-set-font-for-entire-application/16883281#16883281)
to swap the built-in Typefaces so that they get used automatically; another
option is an open-source library called [Calligraphy](https://github.com/chrisjenx/Calligraphy)
which lets you specify the path within `assets` from which to load custom fonts.

Both workarounds cause confusion and have some pitfalls, and there are still
some cases where the custom font does not get used, even when you think it
should.

For example, while _Calligraphy_ can apply a `fontPath="custom.ttf"` override
at the theme level, it is unable to handle the `android:textStyle` attribute
to alter which font is used for a particular style. And there is no way to
even create a `Typeface` that is composed of a family of fonts with varying
text styles -- even though that is exactly how the Android platform creates
its font families of typefaces.

To make matters worse, the font handling changed in Android 5.0 (Lollipop) with
the introduction of `libminikin` for Typeface handling. Along with minikin,
the `Typeface` class also now includes a series of caching that occurs, making
it easier to coerce Android to use your custom fonts, when it would have
previously tried to reload the default font.

In practice, it's also common to see frequent native crashes when using the
Reflection method to replace the default fonts, due to the way that some
platforms reuse `Typeface` class instances even across different processes.
When that happens, the Typeface instance becomes weakly reachable, so it is a
candidate for garbage collection. After the Typeface instance is collected,
its native pointer reference is freed, leading to possible native crashes.

## How it works

The approach we take has to be different based on the platform version, due
to the changes mentioned above with respect to `libminikin` and name-to-Typeface
caching.

### Lollipop (5.0 and newer)

First, we make use of a custom `fonts.xml` file that is structured the same way
as [Android's built-in configuration](https://github.com/android/platform_frameworks_base/blob/master/data/fonts/fonts.xml).
This allows us to specify the font family definitions the same way that Android
does, for consistency. We then use Reflection to turn that 
[FontFamily](https://github.com/android/platform_frameworks_base/blob/master/graphics/java/android/graphics/FontFamily.java)
of font files [_into a single `Typeface`_ instance](https://github.com/android/platform_frameworks_base/blob/master/graphics/java/android/graphics/Typeface.java#L220-L232).

That means that we can name the font family using the 16+ `android:fontFamily`
attribute, and Android will _automagically_ handle applying the correct style
from the font-family-based Typeface based on the `android:textStyle`, just like
it does with the built-in system fonts.

As for naming the font family, there are two choices, depending on your goal:

1. `<family name="sans-serif">` — will effectively **replace** the built-in sans-serif
font family that the platform provides (usually Roboto). Use this if you want
to completely replace the Roboto font everywhere in your app.
But be warned: this does not currently support font _aliases_, such as
`android:fontFamily="sans-serif-medium"` to get a Medium, Thin, or Black
variant of a font. Theoretically you could still achieve that simply by
creating a _new_ `<family name="sans-serif-medium" ...>` family that points
to a whole new set of <font> files. This is a `TODO` item, at least worth
investigating.
2. `<family name="my-app-font">` — will simply create a brand new Typeface by
font family name, allowing you to specify the font override using the
`android:fontFamily="my-app-font"` attribute in your layouts, custom
`TextAppearance` styles, widget styles, or even globally in your Theme.

### KitKat (4.4)

**Limitations**

KitKat can only replace the `Typeface.DEFAULT` (et al) constants, and has an
`sDefaults` array that lets us replace the default Typeface based on the text
style, which is used by `Typeface.defaultFromStyle(int)` and
`TextView.setTypeface((Typeface) null, int)`.

But understanding how the TextView class applies the Typeface, we are able to
coerce the system into loading our custom font in one of two ways:

1. `android:fontFamily="@null" android:typeface="sans" android:textStyle="normal"`
This causes the system to use our custom font in the **normal** style.
2. `android:fontFamily="@null" android:typeface="normal" android:textStyle="bold"`
This causes the system to use our custom font in the **non-normal** (bold,
italic, or bold+italic) styles.

This is portrayed in the [values/styles.xml](sample/src/main/res/values/styles.xml#L42)
file, where the "normal" style has to also set `typeface="sans"` in order to
get the override.

### Jelly Bean (4.1 – 4.3)

This has been tested using the same implementation as on KitKat, albeit not
thoroughly.

### Older

Likewise, theoretically the KitKat implementation should work on older
platforms as well, since the `sDefaults` cache is still present, as far back
as Cupcake (1.5).

But because these versions have not been tested **at all**, I cannot guarantee
that it will work. For older versions, we would recommend using something
else, like [Calligraphy](https://github.com/chrisjenx/Calligraphy).