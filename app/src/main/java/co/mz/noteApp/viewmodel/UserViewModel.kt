package co.mz.noteApp.viewmodel

import android.content.ContentValues
import android.content.ContentValues.TAG
import android.util.Log
import android.widget.Toast
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

    fun link(token: String){
        val credential = GoogleAuthProvider.getCredential(token, null)
        auth.currentUser!!.linkWithCredential(credential)
            .addOnCompleteListener{ task ->
                if (task.isSuccessful) {
                    Log.d(TAG, "linkWithCredential:success")
                    val user = task.result?.user
                    Log.d(TAG, user?.email.toString())
                } else {
                    Log.w(TAG, "linkWithCredential:failure", task.exception)

                }
            }
    }

    fun logOut(){
        auth.signOut()
        user.value = null
    }

    fun initiate(){
        user.value = null
    }

    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener() { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(ContentValues.TAG, "signInWithCredential:success")
                    user.value = task.result?.user
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w(ContentValues.TAG, "signInWithCredential:failure", task.exception)
                    user.value = null
                    _result.value = task.exception
                }
            }
    }


}