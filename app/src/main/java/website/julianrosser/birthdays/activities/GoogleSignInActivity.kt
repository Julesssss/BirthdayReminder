package website.julianrosser.birthdays.activities

import android.content.Intent
import android.support.design.widget.Snackbar
import android.util.Log
import com.google.android.gms.auth.api.Auth
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.auth.api.signin.GoogleSignInResult
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.SignInButton
import com.google.android.gms.common.api.GoogleApiClient
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import website.julianrosser.birthdays.AlarmsHelper
import website.julianrosser.birthdays.BirthdayReminder
import website.julianrosser.birthdays.BuildConfig
import website.julianrosser.birthdays.R
import website.julianrosser.birthdays.fragments.DialogFragments.SignOutDialog

abstract class GoogleSignInActivity : BaseActivity(), GoogleApiClient.OnConnectionFailedListener {

    companion object {
        const val GOOGLE_SIGN_IN = 6006
    }

    interface GoogleSignInListener {
        fun onLogin(firebaseUser: FirebaseUser)
        fun onGoogleFailure(message: String)
        fun onFirebaseFailure(message: String)
    }

    interface GoogleSignOutListener {
        fun onComplete()
    }

    private lateinit var signInButton: SignInButton
    private lateinit var mAuth: FirebaseAuth
    private lateinit var mAuthListener: FirebaseAuth.AuthStateListener
    private lateinit var mGoogleApiClient: GoogleApiClient
    private lateinit var listener: GoogleSignInListener

    fun setUpGoogleSignInButton(googleSignInButton: SignInButton, googleSignInListener: GoogleSignInListener) {
        signInButton = googleSignInButton
        listener = googleSignInListener

        val gso = setUpGoogleSignInOptions()
        signInButton.setSize(SignInButton.SIZE_WIDE)
        signInButton.setScopes(gso.scopeArray)
        signInButton.setOnClickListener {
            val signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient)
            startActivityForResult(signInIntent, GoogleSignInActivity.GOOGLE_SIGN_IN)
        }
        mAuth = FirebaseAuth.getInstance()
        mAuthListener = FirebaseAuth.AuthStateListener {
            it.currentUser?.let {
                listener.onLogin(it)
                return@AuthStateListener
            }
            listener.onFirebaseFailure("User returned null")
        }
    }

    override fun onStart() {
        super.onStart()
        mAuth.addAuthStateListener(mAuthListener)
    }

    override fun onStop() {
        super.onStop()
        mAuth.removeAuthStateListener(mAuthListener)
    }

    private fun setUpGoogleSignInOptions(): GoogleSignInOptions {
        // Configure sign-in to request the user's ID, email address, and basic
        // profile. ID and basic profile are included in DEFAULT_SIGN_IN.
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(BuildConfig.GOOGLE_SIGN_IN_KEY)
                .requestEmail()
                .build()

        // Build a GoogleApiClient with access to the Google Sign-In API and the
        // options specified by gso.
        mGoogleApiClient = GoogleApiClient.Builder(this)
                .enableAutoManage(this /* FragmentActivity */, this /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build()
        return gso
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == GOOGLE_SIGN_IN) run {
            val result = Auth.GoogleSignInApi.getSignInResultFromIntent(data)

            handleSignInResult(result)

        }
    }

    private fun handleSignInResult(result: GoogleSignInResult) {
        Log.d("SIGN IN", "handleSignInResult:" + result.isSuccess)
        if (result.isSuccess && result.signInAccount != null) {
            // Signed in successfully, now login with Firebase
            val account = result.signInAccount
            firebaseAuthWithGoogle(account!!)
            AlarmsHelper.setAllNotificationAlarms(this.applicationContext)
        } else {
            listener.onGoogleFailure("Google sign in failed")
            Snackbar.make(signInButton, getString(R.string.error_google_login), Snackbar.LENGTH_SHORT).show()
        }
    }

    private fun firebaseAuthWithGoogle(account: GoogleSignInAccount) {
        Log.d("Auth", "firebaseAuthWithGoogle: " + account.id!!)

        val credential = GoogleAuthProvider.getCredential(account.idToken, null)
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this) { task ->
                    Log.d("Auth", "signInWithCredential:onComplete:" + task.isSuccessful)

                    // If sign in fails, display a message to the user. If sign in succeeds
                    // the auth state listener will be notified and logic to handle the
                    // signed in user can be handled in the listener.
                    if (!task.isSuccessful) {
                        Log.w("Auth", "signInWithCredential", task.exception)
                        Snackbar.make(signInButton, "Authentication failed", Snackbar.LENGTH_SHORT).show()
                    }
                }
    }

    override fun onConnectionFailed(connectionResult: ConnectionResult) {
        Snackbar.make(signInButton, "Sign in failed", Snackbar.LENGTH_SHORT).show()
    }

    fun signOutGoogle(listener: GoogleSignOutListener) {
        SignOutDialog.newInstance {
            FirebaseAuth.getInstance().signOut()
            Auth.GoogleSignInApi.revokeAccess(mGoogleApiClient).setResultCallback {
                listener.onComplete()
                BirthdayReminder.getInstance().setUser(null)
                Snackbar.make(signInButton, R.string.message_signed_out, Snackbar.LENGTH_SHORT).show()
            }
        }.show(supportFragmentManager, SignOutDialog::class.java!!.simpleName)
    }
}