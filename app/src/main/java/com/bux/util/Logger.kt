package com.bux.util

import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import com.bux.BuildConfig
import java.util.*
import java.util.regex.Pattern

/**
 * Useful logging class that doesn't show any log messages on production builds.
 *
 * Please use the methods for the following reasons:
 *
 * i: start of method ONLY.
 * d: log variables (in this format: exampleVariable: exampleValue).
 * v: anything from states to readable messages.
 * w: warnings that indicate strange behavior that is less desirable.
 * e: errors that should never occur and that will indicate severe complications. Optionally supply a throwable.
 *
 */
object Logger {

    private const val LOG_TAG = "Logger"

    val mFilterSet = HashSet<String>()

    /**
     * Method to ignore single classes out that will no longer log messages. Please note that
     * warnings and errors will still be logged for obvious reasons.
     * @param tag The tag you want to exclude from logging.
     */
    @JvmStatic fun addIgnoreTag(tag: String) {
        if (!mFilterSet.contains(tag)) {
            w(tag, "Ignoring all [$tag] logs.")
            mFilterSet.add(tag)
        }
    }

    /**
     * log the contents of a bundle,
     * will log values in verbose, will log empty values in warning
     *
     * @param tag to identify
     * @param bundle to log
     */
    @JvmStatic fun bundle(tag: String, bundle: Bundle?) {
        i(tag, "bundle()")

        if (bundle != null) {
            for (key in bundle.keySet()) {
                val value = bundle.get(key)

                if (value != null) {

                    v(tag, "$key: $value (${value.javaClass.name})")
                } else {
                    w(tag, "Corresponding value for key [$key] is null")
                }
            }
        } else {
            w(tag, "bundle is null")
        }
    }

    /**
     * Logs each value in a collection.
     * @param tag   The tag to log it under/as.
     * @param var   The variable name for quick lookup.
     * @param items The items as a collection interface.
     */
    @JvmStatic fun each(tag: String, variable: String, items: Collection<*>) {
        d(tag, "collection: $variable [count:${items.size}]")

        for (item in items) {
            v(tag, item.toString())
        }
    }

    /**
     * Logs each value in an int array.
     * @param tag   The tag to log it under/as.
     * @param var   The variable name for quick lookup.
     * @param items The items as a int array.
     */
    @JvmStatic fun each(tag: String, variable: String, items: IntArray) {
        d(tag, "collection: $variable [count:${items.size}]")
        for (value in items) {
            v(tag, value.toString())
        }
    }

    /**
     * Logs each value in an String array.
     * @param tag   The tag to log it under/as.
     * @param var   The variable name for quick lookup.
     * @param items The items as a String array.
     */
    @JvmStatic fun each(tag: String, variable: String, items: Array<String>) {
        d(tag, "collection: $variable [count:${items.size}]")
        for (value in items) {
            v(tag, value)
        }
    }

    /**
     * Convenience method that shows the [.toString] of any object. NullSafe!
     * @param tag the tag to log it under/as.
     * @param name The variable name for quick lookup.
     * @param object The object to log
     */
    @JvmStatic fun toString(tag: String, name: String?, objectInstance: Any?) {
        if (objectInstance == null) {
            w(tag, name!! + " (null)")
        } else {
            if (objectInstance is List<*>) {
                v(
                    tag,
                    String.format(Locale.getDefault(), "(list) %1\$s: %2\$d items", name, objectInstance.size)
                )
            } else {
                if (TextUtils.isEmpty(name)) {
                    v(tag, String.format("%1\$s", objectInstance.toString()))
                } else {
                    v(
                        tag,
                        String.format("%1\$s: %2\$s", name, objectInstance.toString())
                    )
                }
            }
        }
    }

    @JvmStatic fun block(tag: String, message: String) {
        i(tag, "============================================================")
        i(tag, message.toUpperCase())
        i(tag, "============================================================")
    }

    @JvmStatic fun v(tag: String, message: String) {
        if (BuildConfig.DEBUG) {
            if (!mFilterSet.contains(tag)) {
                Log.v(tag, message)
            }
        }
    }

    @JvmStatic fun d(tag: String, message: String) {
        if (BuildConfig.DEBUG) {
            if (!mFilterSet.contains(tag)) {
                Log.d(tag, message)
            }
        }
    }

    @JvmStatic fun i(tag: String, methodName: String) {
        if (BuildConfig.DEBUG) {
            if (!mFilterSet.contains(tag)) {
                Log.i(tag, methodName)
            }
        }
    }

    @JvmStatic fun i(tag: String, methodName: String, param: Any) {
        if (BuildConfig.DEBUG) {
            if (!mFilterSet.contains(tag)) {
                var cleanMethodName = "???"

                try {
                    val pattern = Pattern.compile("[A-Za-z]+")
                    val matcher = pattern.matcher(methodName)
                    if (matcher.find()) {
                        cleanMethodName = matcher.group(0)
                    }
                } catch (ioobe: IndexOutOfBoundsException) {
                    w(
                        LOG_TAG,
                        "Cannot clean up methodname [$methodName]."
                    )
                }

                Log.i(tag, cleanMethodName + "()")
                toString(tag, null, param)
            }
        }
    }

    @JvmStatic fun w(tag: String, message: String) {
        if (BuildConfig.DEBUG) {
            if (!mFilterSet.contains(tag)) {
                Log.w(tag, message)
            }
        }
    }

    @JvmStatic fun w(tag: String, message: String, throwable: Throwable) {
        if (BuildConfig.DEBUG) {
            if (!mFilterSet.contains(tag)) {
                Log.w(tag, message, throwable)
            }
        }
    }

    @JvmStatic fun e(tag: String, message: String) {
        if (BuildConfig.DEBUG) {
            if (!mFilterSet.contains(tag)) {
                Log.e(tag, message)
            }
        }
    }

    @JvmStatic fun e(tag: String, message: String, throwable: Throwable) {
        if (BuildConfig.DEBUG) {
            if (!mFilterSet.contains(tag)) {
                Log.e(tag, message, throwable)
            }
        }
    }

    @JvmStatic fun wtf(tag: String, message: String) {
        if (BuildConfig.DEBUG) {
            if (!mFilterSet.contains(tag)) {
                Log.wtf(tag, message)
            }
        }
    }

    /**
     * Nanotimer is able to check and see how long certain methods take (in nanoseconds).
     * Create an instance and call [hit] several times.
     */
    class NanoTimer(private val tag: String) {

        private val start = System.nanoTime()
        private var last: Long = start

        private var count = 0

        init {
            i(tag, "Timer instance created...")
        }

        fun hit(with: String) {
            val elapsed = System.nanoTime() - last
            v(tag, String.format("[%1\$d] %2\$,d - %3\$s", count, elapsed, with))

            count++
            last = System.nanoTime()
        }

        fun separator(){
            v(tag, "-".repeat(30))
        }

        fun end() {
            val elapsed = last - start
            v(tag, String.format("[end] %1\$,d", elapsed))
        }

    }

}