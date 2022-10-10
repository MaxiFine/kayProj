package com.example.ctyassistant

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.text.TextUtils
import android.widget.LinearLayout
import android.widget.Toast
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputLayout
import timber.log.Timber

open class checkStuffs {

}

    /* function to check internet connection
   open fun isNetworkAvailable(mContext: Context): Boolean {

        var result = false
        val connectivityManager =
            mContext.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkCapabilities = connectivityManager.activeNetwork ?: return false
        val actNw =
            connectivityManager.getNetworkCapabilities(networkCapabilities) ?: return false
        result = when {
            actNw.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
            actNw.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
            actNw.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
            else -> false
        }

        return result

    }

     */

/*
    // check info inputted by user
    fun checkInfo(
        u: String?, e: String?, p: String, c: String, uname: TextInputEditText,
        ema: TextInputEditText, pasw: TextInputEditText, confp: TextInputEditText,
        su: LinearLayout?, tag: String?
    ): Boolean {
        if (u.isEmpty() && e.isEmpty() && p.isEmpty(p) && c.isEmpty()
        ) {
            Snackbar.make(
                su!!, "Fields are empty. Please provide details", Snackbar.LENGTH_SHORT
            ).show()
            return false
        }
        if (u.isEmpty()) {
            uname.error = " provide a username"
            return false
        }

        /*
        // checks if the email is valid
        //Regular expression to accept valid email id
        val regex = "^[a-zA-Z0-9+_.-]+@[a-zA-Z0-9.-]+$"

        //Creating a pattern object
        val pattern = Pattern.compile(regex)
        //Creating a Matcher object
        val matcher = e?.let { pattern.matcher(it) }

        //Verifying whether given phone number is valid
        if (matcher.matches()) {
            Timber.tag(tag).d("email is valid")
        } else {
            // System.out.println("Given email id is not valid");
            ema.error = "email is not valid"
            ema.requestFocus()
            return false
        }


        if (e.isEmpty()) {
            ema.error = " provide an email address"
            ema.requestFocus()
            return false
        }
        if (p.isEmpty()) {
            pasw.error = " provide a password"
            pasw.requestFocus()
            return false
        } else if (p.length < 8) {
            pasw.error = " passwords should be eight(8) or more characters "
            pasw.requestFocus()
            return false
        }
        if (c.isEmpty()) {
            confp.error = " Please confirm the password"
            confp.requestFocus()
            return false
        } else if (!c.equals(p)) {
            confp.error = "Passwords do not match. check the passwords"
            confp.requestFocus()
            return false
        }
        if (c.isEmpty() && p.isEmpty()) {
            Snackbar.make(
                su!!,
                "please provide a password and confirm it", Snackbar.LENGTH_SHORT
            ).show()
            return false
        }
        if (!u.isEmpty() && !e.isEmpty() && !c.isEmpty()
            && p.length >= 8 && p.equals(c)
        ) {
            Timber.tag(tag).d(" all inputs have been filled")
            return true
        }
        return false
    }

 */

/*
    fun checkInfoAddPlace(
        name: String?, desc: String?, latitude: Double, longitude: Double,
        nop: TextInputEditText, des: TextInputEditText, ltd: TextInputEditText,
        lgd: TextInputEditText, context: Context?, tag: String?
    ): Boolean {
        if (TextUtils.isEmpty(name) && TextUtils.isEmpty(desc) && latitude == 0.00 && longitude == 0.00) {
            Toast.makeText(
                context, " The fields are empty. Please provide details",
                Toast.LENGTH_SHORT
            ).show()
            return false
        }
        if (TextUtils.isEmpty(name)) {
            nop.error = "please provide name of the place"
            return false
        }
        if (desc.isEmpty(desc)) {
            des.error = "please provide the classification of the place"
            return false
        }
        if (latitude.toString().isEmpty()) {
            ltd.error = "please provide the latitude of the place"
            return false
        }
        if (longitude.toString().isEmpty()) {
            lgd.error = "please provide the longitude of the place"
            return false
        }

        if (latitude.toString().isEmpty() && longitude.toString().isEmpty()) {
            Toast.makeText(
                context, "provide both the latitude and longitude of the place",
                Toast.LENGTH_SHORT
            ).show()
            return false
        }
        if (!name.isEmpty() && !desc.isEmpty() && !latitude.toString().isEmpty()
            && !longitude.toString().isEmpty()
        ) {
            Timber.tag(tag).d("All inputs have been filled")
            return true
        }
        return false
    }


 */