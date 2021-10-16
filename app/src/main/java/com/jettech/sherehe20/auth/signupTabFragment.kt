package com.jettech.sherehe20.auth

import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.IdpResponse
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.userProfileChangeRequest
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.FirebaseMessaging
import com.jettech.sherehe20.PermissionsActivity
import com.jettech.sherehe20.R
import com.jettech.sherehe20.utils.Constants
import es.dmoral.toasty.Toasty

class signupTabFragment : Fragment() {

    private lateinit var auth: FirebaseAuth


    private val RC_SIGN_IN = 1234
    var useremail: String = ""
    var name: String = ""
    var userpassword: String = ""
    lateinit var uView: Fragment

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val root = inflater.inflate(R.layout.signup_tab_fragment, container, false) as ViewGroup


        val fullname = root.findViewById<EditText>(R.id.userName)
        val email = root.findViewById<EditText>(R.id.regemail)
        val password = root.findViewById<EditText>(R.id.pass)
        val cnfPasword = root.findViewById<EditText>(R.id.cnfPass)
        val mobile = root.findViewById<EditText>(R.id.phone)
        val regUser = root.findViewById<Button>(R.id.buttonReg)

        uView = Fragment()

        regUser.setOnClickListener {
            if (fullname.text!!.isEmpty()) {
                Toasty.error(
                    requireContext(),
                    "fullname field cannot be blank.",
                    Toast.LENGTH_LONG,
                    true
                )
                    .show()

                name = fullname.text.toString()
                return@setOnClickListener
            }

            if (email.text!!.isEmpty()) {
                Toasty.error(
                    requireContext(),
                    "Email field cannot be blank.",
                    Toast.LENGTH_LONG,
                    true
                )
                    .show()
                useremail = email.text.toString()
                return@setOnClickListener
            }

            if (mobile.text!!.isEmpty()) {
                Toasty.error(
                    requireContext(),
                    "Phone number field cannot be blank.",
                    Toast.LENGTH_LONG,
                    true
                )
                    .show()

                return@setOnClickListener
            }

            if (password.text!!.isEmpty()) {
                Toasty.error(
                    requireContext(),
                    "Password field cannot be blank.",
                    Toast.LENGTH_LONG,
                    true
                )
                    .show()


                return@setOnClickListener
            }
            if (cnfPasword.text!!.isEmpty()) {
                Toasty.error(
                    requireContext(),
                    "Confirm password field cannot be blank.",
                    Toast.LENGTH_LONG,
                    true
                ).show()
                userpassword = cnfPasword.text.toString()
                return@setOnClickListener
            }

            if (password.text!!.trim().length < 6) {
                Toasty.error(
                    requireContext(),
                    "Password length must be 6 characters and above",
                    Toast.LENGTH_LONG,
                    true
                )
                    .show()
                return@setOnClickListener
            }
            if (password.text!!.toString() != cnfPasword.text!!.toString()) {
                Toasty.error(requireContext(), "Passwords don't match", Toast.LENGTH_LONG, true)
                    .show()
                return@setOnClickListener
            }

            var sharedPref: SharedPreferences = requireContext().getSharedPreferences(Constants.APP_SHARED_PREFERENCES, 0)

            val editor = sharedPref.edit()
            editor.putString(Constants.NAME, fullname.text.toString())
            editor.putString(Constants.EMAIL, email.text.toString())
            editor.putString(Constants.PASSWORD, password.text!!.toString())
            editor.apply()

            val providers = arrayListOf(
                AuthUI.IdpConfig.PhoneBuilder().build()
            )
            startActivityForResult(
                AuthUI.getInstance()
                    .createSignInIntentBuilder()
                    .setAvailableProviders(providers)
                    .setTosAndPrivacyPolicyUrls(
                        "https://gloib.com/terms.html",
                        "https://gloib.com/privacy.html"
                    )
                    .build(),


                RC_SIGN_IN
            )


        }

        return root
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        // The last parameter value of shouldHandleResult() is the value we pass to setRequestCode().
        // If we do not call setRequestCode(), we can ignore the last parameter.

        if (requestCode == RC_SIGN_IN) {
            val response = IdpResponse.fromResultIntent(data)

            if (resultCode == Activity.RESULT_OK) {

                creatUser()


            } else if (resultCode == Activity.RESULT_CANCELED) {
                Toast.makeText(requireContext(), "Please Try Again", Toast.LENGTH_LONG).show()
                val intent = Intent(requireContext(), LoginActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                startActivity(intent)

            } else {
                // Sign in failed. If response is null the user canceled the
                // sign-in flow using the back button. Otherwise check
                // response.getError().getErrorCode() and handle the error.
                // ...
                Toast.makeText(requireContext(), response!!.error!!.errorCode, Toast.LENGTH_LONG)
                    .show()
            }


            super.onActivityResult(
                requestCode,
                resultCode,
                data
            )   // This line is REQUIRED in fragment mode


        } else {
            // Sign in failed. If response is null the user canceled the
            // sign-in flow using the back button. Otherwise check
            // response.getError().getErrorCode() and handle the error.
            // ...
            Toast.makeText(requireContext(), "Try Again", Toast.LENGTH_LONG).show()
        }

    }

    private fun creatUser() {

        val progressDialog = Dialog(requireContext())
        progressDialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        progressDialog.setContentView(R.layout.custom_dialog_progress)

/* Custom setting to change TextView text,Color and Text Size according to your Preference*/

        val progressTv = progressDialog.findViewById(R.id.progress_tv) as TextView
        progressTv.text = resources.getString(R.string.loading)
        progressTv.setTextColor(ContextCompat.getColor(requireContext(), R.color.pink))
        progressTv.textSize = 15F

        progressDialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        progressDialog.setCancelable(false)
        progressDialog.show()

        val sharedPref: SharedPreferences =
            requireContext().getSharedPreferences(Constants.APP_SHARED_PREFERENCES, 0)
        val username: String = sharedPref.getString(Constants.NAME, "").toString().trim()
        val useremail: String = sharedPref.getString(Constants.EMAIL, "").toString().trim()
        val userpass: String = sharedPref.getString(Constants.PASSWORD, "").toString().trim()

        // Successfully signed in

        val currentUser = FirebaseAuth.getInstance().currentUser
        val credential = EmailAuthProvider.getCredential(
            useremail.toString().trim(),
            userpass.toString().trim()
        )
        Log.d("checkemail",useremail)
        currentUser!!.linkWithCredential(credential)
            .addOnCompleteListener() { task ->
                if (task.isSuccessful) {
                    Log.d("RegisterActivity", "linkWithCredential:success")
                    val user = task.result?.user
                    Log.e("RegisterActivity", user!!.phoneNumber!!)

                    val db = Firebase.firestore

                    val userData = hashMapOf(

                        "fullname" to username.toString(),
                        "email" to useremail.toString(),
                        "userType" to "1",
                        "phone" to user.phoneNumber,
                        "id" to user.uid,

                        )

                    db.collection("storeowner").document(user.uid)
                        .set(userData)
                        .addOnSuccessListener {
                            Log.e(
                                "RegisterActivity",
                                "DocumentSnapshot successfully written!"
                            )
                            val profileUpdates = userProfileChangeRequest {
                                displayName =
                                    username.toString()

                            }

                            user.updateProfile(profileUpdates)
                                .addOnCompleteListener { task ->
                                    if (task.isSuccessful) {

                                        Log.d(
                                            "RegisterActivity",
                                            "User profile updated."
                                        )
                                        FirebaseMessaging.getInstance().token.addOnCompleteListener(
                                            OnCompleteListener { task ->
                                                if (!task.isSuccessful) {
                                                    Log.w(
                                                        "MainActivity",
                                                        "Fetching FCM registration token failed",
                                                        task.exception
                                                    )
                                                    return@OnCompleteListener
                                                }

                                                // Get new FCM registration token
                                                val token = task.result

                                                db.collection("storeowner")
                                                    .document(user.uid)
                                                    .update("token", token)
                                                    .addOnSuccessListener {
                                                        startActivity(
                                                            Intent(
                                                                requireContext(),
                                                                PermissionsActivity::class.java
                                                            )
                                                        )
                                                        var sharedPref: SharedPreferences = requireContext().getSharedPreferences(Constants.APP_SHARED_PREFERENCES, 0)
                                                        val editor = sharedPref.edit()
                                                        editor.clear()
                                                        editor.commit()

                                                        requireActivity().finish();
                                                        progressDialog.dismiss()

                                                    }
                                            })
                                    }
                                }


                        }
                        .addOnFailureListener { e ->
                            progressDialog.dismiss()
                            Log.e("RegisterActivity", "Error writing document", e)
                        }


                } else {
                    Log.w("RegisterActivity", "linkWithCredential:failure", task.exception)

                    Toasty.error(
                        requireContext(), "Authentication failed: " + task.exception,
                        Toast.LENGTH_LONG
                    ).show()

                }


            }

    }

}