package co.mz.noteApp.data

import android.os.Parcelable
import androidx.annotation.Keep
import kotlinx.parcelize.Parcelize

@Keep
@Parcelize
class User (
    val userId: String,
    val email: String,
    val displayName: String? = null,
    val password: String? = null
        ): Parcelable {
}