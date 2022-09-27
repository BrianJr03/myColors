package jr.brian.myapplication.data.model.remote.firebase

import android.content.Context
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import jr.brian.myapplication.util.makeToast

object Auth {
    private var firebaseAuth: FirebaseAuth = Firebase.auth
    private lateinit var firebaseUser: FirebaseUser

    init {
        if (firebaseAuth.currentUser != null && firebaseUser != firebaseAuth) {
            firebaseUser = firebaseAuth.currentUser as FirebaseUser
        }
    }

    fun signUp(context: Context, email: String, password: String) {
        firebaseAuth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    if (firebaseAuth.currentUser != null) {
                        firebaseUser = firebaseAuth.currentUser as FirebaseUser
                    }
                    sendEmailVerification(context)
                }
            }.addOnFailureListener {
                val msg = it.message.toString()
                makeToast(context, msg)
                Log.i("REGISTER_ERROR", msg)
            }
    }

    private fun sendEmailVerification(context: Context) {
        firebaseUser.let {
            firebaseUser.sendEmailVerification().addOnCompleteListener {
                if (it.isSuccessful) {
                    makeToast(context, "Verification email has been sent.")
                } else {
                    makeToast(context, "Failed to send email for verification")
                    Log.i("VERIFICATION_EMAIL_ERROR", it.result.toString())
                }
            }
        }
    }

    fun signIn(context: Context, email: String, password: String, onSignUp: () -> Unit) {
        firebaseAuth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener {
                if (it.isSuccessful) {

                    if (firebaseUser.isEmailVerified) {
                        onSignUp()
                    } else {
                        makeToast(context, "Please verify your email.")
                    }
                } else {
                    makeToast(context, it.result.toString())
                }
            }
    }

    fun signOut() {
        firebaseAuth.signOut()
    }
}