package co.mz.noteApp.data

import android.os.Parcelable
import androidx.annotation.Keep
import kotlinx.parcelize.Parcelize

@Keep
@Parcelize
class Category (
    var name: String? = null,
        ): Parcelable {
}