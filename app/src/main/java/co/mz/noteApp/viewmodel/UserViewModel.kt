package co.mz.noteApp.viewmodel

import android.content.ContentValues
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import co.mz.noteApp.data.Job
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import java.lang.Exception

class UserViewModel : ViewModel(){

    private var auth = FirebaseAuth.getInstance()
    var user : MutableLiveData<FirebaseUser> = MutableLiveData()

    private val _result = MutableLiveData<Exception?>()
    val result: LiveData<Exception?> get() = _result


    fun login(email: String,password:String): LiveData<FirebaseUser>{
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener {
                if(it.isSuccessful){
                    user.value = it.result?.user
                }else{
                    user.value = null
                    _result.value = it.exception
                    Log.v("user Exception", it.exception.toString())
                }
            }
        return user
    }

    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener() { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(ContentValues.TAG, "signInWithCredential:success")
                    val user = auth.currentUser
                    //   updateUI(user)
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w(ContentValues.TAG, "signInWithCredential:failure", task.exception)
                    //    updateUI(null)
                }
            }
    }


}