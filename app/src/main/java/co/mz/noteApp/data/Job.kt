package co.mz.noteApp.data

import android.os.Parcelable
import androidx.annotation.Keep
import com.google.firebase.firestore.ServerTimestamp
import java.util.Date
import kotlinx.parcelize.Parcelize

@Keep
@Parcelize
data class Job (
    @ServerTimestamp
    var createdAt: Date? = null,
    @ServerTimestamp
    var expiry: Date? = null,
    var name: String? = null,
    var company: String? = null,
    var companyWeb: String? = null,
    var location: String? = null,
    var requeriments: String? = null,
    var description: List<String>? = null,
        ): Parcelable {
    constructor() : this(null, null, "", "", "","", "", emptyList())




}