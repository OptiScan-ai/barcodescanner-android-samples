package com.optiscan.demo.barcode.views

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.android.play.core.install.model.AppUpdateType
import com.google.android.play.core.install.model.InstallStatus
import com.google.android.play.core.install.model.UpdateAvailability
import com.optiscan.demo.barcode.R
import com.optiscan.demo.barcode.adapters.ScanningTypesAdapter
//import com.obs.optiscan.demo.databinding.ActivityHomeBinding
import com.optiscan.demo.barcode.model.ScanTypes
import kotlinx.android.synthetic.main.activity_home.*
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.android.material.snackbar.Snackbar




class HomeActivity : AppCompatActivity() {

   // private lateinit var binding: ActivityHomeBinding

    var scanTypes: MutableList<ScanTypes> = ArrayList()
    private var mFirebaseAnalytics: FirebaseAnalytics? = null
    private val UPDATE_REQUEST_CODE = 123
    private val appUpdateManager by lazy { AppUpdateManagerFactory.create(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
       // binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(R.layout.activity_home)

        appUpdateManager.registerListener {
            if (it.installStatus() == InstallStatus.DOWNLOADED) {
                showUpdateDownloadedSnackbar()
            }
        }

        appUpdateManager.appUpdateInfo.addOnSuccessListener {
            if (it.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE && it.isUpdateTypeAllowed(AppUpdateType.FLEXIBLE)) {
                appUpdateManager.startUpdateFlowForResult(it, AppUpdateType.FLEXIBLE, this, UPDATE_REQUEST_CODE)
            }
        }.addOnFailureListener {
            Log.e("FlexibleUpdateActivity", "Failed to check for update: $it")
        }

        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this)
        addScanningTypes()

        setUpRecyclerView()

        //supportActionBar?.displayOptions = ActionBar.DISPLAY_SHOW_CUSTOM
        //supportActionBar?.setCustomView(R.layout.custom_title)

    }

    private fun setUpRecyclerView() {
        rv_scanningTypes.layoutManager = GridLayoutManager(this, 2)
        rv_scanningTypes.adapter = ScanningTypesAdapter(scanTypes, this
        ) { model ->
            val intent = Intent(this, OptiScannerActivity::class.java)
            intent.putExtra("scanType", model.scanType)
            intent.putExtra("scanTitle", model.type)
            startActivity(intent)
        }
    }

    private fun addScanningTypes() {
        scanTypes.add(ScanTypes("QR CODE", R.drawable.ic_qr_code, "qr_code"))
        scanTypes.add(ScanTypes("BAR CODE", R.drawable.ic_barcode, "bar_code"))
        // scanTypes.add(ScanTypes("OCR", R.drawable.finalocrupdated))
        scanTypes.add(ScanTypes("QR/BAR CODE", R.drawable.ic_qr_bar_code, "any"))
    }

    override fun onResume() {
        super.onResume()
        appUpdateManager.appUpdateInfo.addOnSuccessListener {
            if (it.installStatus() == InstallStatus.DOWNLOADED) {
                showUpdateDownloadedSnackbar()
            }
        }
    }

    private fun showUpdateDownloadedSnackbar() {
        Snackbar.make(homeLayout, "Update downloaded!", Snackbar.LENGTH_INDEFINITE)
            .setAction("Install") { appUpdateManager.completeUpdate() }
            .show()

    }

}