package co.mz.noteApp.viewmodel

import android.content.ContentValues
import android.content.ContentValues.TAG
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.*
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
                   // linkToAnonymous(email, password)
                }else{
                    user.value = null
                    _result.value = it.exception
                    Log.v("user Exception", it.exception.toString())
                }
            }
        return user
    }

    fun post(value: FirebaseUser){
        user.postValue(value)
    }

    fun link(email: String, password: String): LiveData<FirebaseUser>{
        val credential = EmailAuthProvider.getCredential(email, password)
        auth.signInWithCredential(credential)
            .addOnCompleteListener{ task ->
                if (task.isSuccessful) {
                   // Log.d(TAG, "linkWithCredential:success")
                  //  val user = task.result?.user
                    user.value = task.result?.user

                  //  linkToAnonymous(email, password)
                } else {
                    user.value = null
                  //  Log.w(TAG, "linkWithCredential:failure", task.exception)
                    _result.value = task.exception
                }
            }
        return user
    }

    private fun linkToAnonymous(email: String, password: String){
        val credential = EmailAuthProvider.getCredential(email, password)
        auth.currentUser!!.linkWithCredential(credential)
            .addOnCompleteListener{ task ->
                if (task.isSuccessful) {
                    Log.d(TAG, "linkWithCredential:success")
                    //  val user = task.result?.user
                   // user.value = task.result?.user
                } else {
                    user.value = null
                    Log.w(TAG, "linkWithCredential:failure", task.exception)
                  //  _result.value = task.exception
                }
            }
    }

    fun changePassword(newPassword: String){
        auth.currentUser!!.updatePassword(newPassword)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Log.d(TAG, "User password updated.")
                }
            }
    }

    fun sendPasswordResetEmail(emailAddress: String){
        auth.sendPasswordResetEmail(emailAddress)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Log.d(TAG, "Email sent.")
                }
            }
    }

    fun createUser(email: String,password:String): LiveData<FirebaseUser>{
        auth.createUserWithEmailAndPassword(email, password)
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

    fun logOut(){
        auth.signOut()
        initiate()
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