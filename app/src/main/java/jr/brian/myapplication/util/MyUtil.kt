package jr.brian.myapplication.util

import android.content.Context
import android.widget.Toast
import androidx.annotation.ColorInt
import androidx.annotation.Size

fun makeToast(context: Context, msg: String) =
    Toast.makeText(context, msg, Toast.LENGTH_LONG).show()

@ColorInt
fun parseColor(@Size(min = 1) colorString: String): Int {
    val error = "Unknown Color"
    if (colorString[0] == '#') {
        var color = colorString.substring(1).toLong(16)
        if (colorString.length == 7) {
            color = color or -0x1000000
        } else require(colorString.length == 9) { error }
        return color.toInt()
    }
    throw IllegalArgumentException(error)
}