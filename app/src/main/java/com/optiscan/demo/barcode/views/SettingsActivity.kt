package com.optiscan.demo.barcode.views

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.optiscan.demo.barcode.BuildConfig
import com.optiscan.demo.barcode.R
import kotlinx.android.synthetic.main.activity_settings.*


class SettingsActivity : AppCompatActivity() {

    //private lateinit var binding: ActivitySettingsBinding
    private lateinit var preference: SharedPreferences
    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(R.layout.activity_settings)
        preference = getSharedPreferences(getString(R.string.app_name), Context.MODE_PRIVATE)
        val autoFlashlight = preference.getBoolean("autoFlashlight", false)
        val autoExposure = preference.getBoolean("autoExposure",false)


        etThreshold.setText(intent.getFloatExtra("confidence", 0.5f).toString())
        ch_autoflash.isChecked = autoFlashlight
        ch_autoexposure.isChecked = autoExposure
        ch_debug.isChecked = intent.getBooleanExtra("isDebug", false)
        ch_qr_barcode.isChecked = intent.getBooleanExtra("isQrBarcodeDetection", false)
        txt_version_name.text = "v" + BuildConfig.VERSION_NAME


        ch_debug.setOnCheckedChangeListener { buttonView, isChecked ->
            if (buttonView.isPressed && isChecked) {
                ch_qr_barcode.isChecked = true
            }
        }

        ch_autoexposure.setOnCheckedChangeListener { buttonView, isChecked ->
            if (buttonView.isPressed && isChecked) {
                ch_autoexposure.isChecked = true
            }
        }



        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Settings"

        btnSetThreshold.setOnClickListener {

            val threshold = etThreshold.text.toString()

            if (threshold.isEmpty()) {
                Toast.makeText(this, "Please Enter Valid Threshold Value", Toast.LENGTH_SHORT).show()
            } else if (threshold.toFloat() >= 1 || threshold.toFloat() < 0.1) {
                Toast.makeText(this, "Invalid Theshold Value", Toast.LENGTH_SHORT).show()
            } else {
                //Toast.makeText(this,"Threshold Value: $threshold",Toast.LENGTH_SHORT).show()

                val intent = Intent(this, OptiScannerActivity::class.java)
                val confidence = threshold.toFloat()
                intent.putExtra("confidence", confidence)
                intent.putExtra("autoFlashlight", ch_autoflash.isChecked)
                intent.putExtra("autoExposure",ch_autoexposure.isChecked)
                // intent.putExtra("isDebug",binding.ch_debug.isChecked)
                // intent.putExtra("isQrBarcodeDetection",binding.ch_qr_barcode.isChecked)
                setResult(Activity.RESULT_OK, intent)
                finish()
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        return when (item.itemId) {
            android.R.id.home -> {
                finish()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}