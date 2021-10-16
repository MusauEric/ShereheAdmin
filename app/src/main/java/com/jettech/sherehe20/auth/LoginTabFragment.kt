package com.jettech.sherehe20.auth

import android.app.Dialog
import android.content.Intent
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
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.jettech.sherehe20.PermissionsActivity
import com.jettech.sherehe20.R
import es.dmoral.toasty.Toasty

class LoginTabFragment : Fragment() {

    private lateinit var auth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val root = inflater.inflate(R.layout.login_tab_fragment, container, false) as ViewGroup
        val button = root.findViewById(R.id.buttonLogin) as Button

        auth = Firebase.auth
        val user = Firebase.auth.currentUser
        if (user != null) {
            // User is signed in
            startActivity(Intent(requireContext(), PermissionsActivity::class.java))
            requireActivity().finish()
        }


        button.setOnClickListener {

            val loginEmail = root.findViewById<EditText>(R.id.email)
            val loginPassword = root.findViewById<EditText>(R.id.pass)
            if (loginEmail.text!!.isEmpty()) {
                Toasty.error(requireContext(), "Email field cannot be blank.", Toast.LENGTH_LONG, true)
                    .show()

                return@setOnClickListener
            }

            if (loginPassword.text!!.isEmpty()) {
                Toasty.error(requireContext(), "Password field cannot be blank.", Toast.LENGTH_LONG, true)
                    .show()

                return@setOnClickListener
            }
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


            auth.signInWithEmailAndPassword(loginEmail.text!!.trim().toString(), loginPassword.text!!.toString())
                .addOnCompleteListener() { task ->
                    if (task.isSuccessful) {
                        Log.d("hapo",task.toString())
                        // Sign in success, update UI with the signed-in user's information

                        startActivity(Intent(context, PermissionsActivity::class.java))
                        requireActivity().finish();
                        progressDialog.dismiss()

                    } else {
                        // If sign in fails, display a message to the user.

                        Log.e("LoginActivity", "signInWithEmail:failure", task.exception)
                        Toasty.error(
                            requireContext(), "Incorrect password or email: ",
                            Toast.LENGTH_LONG
                        ).show()
                        progressDialog.dismiss()

                    }


                }

        }



        return root
    }
}