package co.mz.noteApp.data

import android.net.Uri
import android.os.Parcelable
import androidx.annotation.Keep
import kotlinx.parcelize.Parcelize

@Keep
@Parcelize
class User (
    val userId: String? = null,
    val email: String? = null,
    var photoUri : Uri? = null,
    var displayName: String? = null,
    var password: String? = null
        ): Parcelable {
    constructor() : this(null, null, null, "", "")
}