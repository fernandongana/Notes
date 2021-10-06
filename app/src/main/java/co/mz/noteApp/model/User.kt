package co.mz.noteApp.model

import android.net.Uri
import android.os.Parcelable
import androidx.annotation.Keep
import kotlinx.parcelize.Parcelize

@Keep
@Parcelize
class User (
    var userId: String? = null,
    var email: String? = null,
    var photoUri : String? = null,
    var displayName: String? = null,
        ): Parcelable {
    constructor() : this(null, null, null, "")
}