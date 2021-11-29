package com.optiscan.demo.barcode.views

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.os.CountDownTimer
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.microsoft.appcenter.AppCenter
import com.microsoft.appcenter.analytics.Analytics
import com.microsoft.appcenter.crashes.Crashes
import com.optiscan.OptiScanFactory
import com.optiscan.demo.barcode.R
import com.optiscan.scanner.CameraConfig
import com.optiscan.scanner.CameraScan
import com.optiscan.scanner.model.ScanBarcodeFormat
import com.optiscan.scanner.model.ScanResult
import com.optiscan.util.PermissionUtils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import android.R.attr.label
import android.content.*
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_optiscanner.*


class OptiScannerActivity : AppCompatActivity(), CameraScan.OnScanResultCallback,
    View.OnClickListener {
   // private lateinit var binding: ActivityOptiscannerBinding
    private var isContinuousScan = true
    private var mCameraScan: CameraScan? = null
    private var timer: CountDownTimer? = null
    private var confidence: Float = 0.5f
    private var isDebug: Boolean = false
    private var autoFlashlight: Boolean = false
    private var autoExposure: Boolean = false
    private var isQrBarcodeDetection: Boolean = true
    private var scanType: String? = null
    private lateinit var preference: SharedPreferences
    private lateinit var bottomSheetBehavior: BottomSheetBehavior<*>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //binding = ActivityOptiscannerBinding.inflate(layoutInflater)
        setContentView(R.layout.activity_optiscanner)

        initAppCenter()
        getIntentValues()
        setOnClickListeners()


        if (!isContinuousScan) btnImageCapture.isVisible = true

        mCameraScan = OptiScanFactory.createScanSession(this, previewView)

        mCameraScan?.setOnScanResultCallback(this)
            ?.setPlayBeep(true)
            ?.setBeepSoundResource(R.raw.beep)
            ?.setVibrate(false)
            ?.setCameraConfig(CameraConfig())
            ?.setAutoFlashlight(autoFlashlight)
            ?.setAutoExposure(autoExposure)
            ?.setDebugMode(isDebug)
            ?.setScanType(scanType)
            ?.setDarkLightLux(4f)
            ?.setBrightLightLux(100f)
            ?.setQrBarcodeDetection(isQrBarcodeDetection)
            ?.bindFlashlightView(ivFlash)
            ?.bindOverlayView(tracking_overlay)
            ?.updateConfidenceValue(confidence)
            ?.bindSliderView(sliderExposure)
            ?.setIsContinuousScan(isContinuousScan)

    }

    /**
     * This method is used get intent values and preference values.
     */
    private fun getIntentValues() {
        //supportActionBar?.setDisplayHomeAsUpEnabled(true)
        //supportActionBar?.title = intent.getStringExtra("scanTitle") ?: SCAN_TITLE
        tvSelectScannerByBarCode.setText(intent.getStringExtra("scanTitle") ?: SCAN_TITLE)
        scanType = intent.getStringExtra("scanType") ?: SCAN_TYPE

        preference = getSharedPreferences(getString(R.string.app_name), Context.MODE_PRIVATE)
        confidence = preference.getFloat("confidence", 0.5f)
        autoFlashlight = preference.getBoolean("autoFlashlight", false)
        autoExposure = preference.getBoolean("autoExposure", false)
    }
    /**
     * This method is used to set click listeners for views.
     */
    private fun setOnClickListeners() {
        bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet)
        ivUpArrow.setOnClickListener(this)
        ivDownArrow.setOnClickListener(this)
        ivFlash.setOnClickListener(this)
        settings.setOnClickListener(this)
        back.setOnClickListener(this)
        btnImageCapture.setOnClickListener(this)
        tvCopy.setOnClickListener(this)
    }

    /**
     * Called when a view has been clicked.
     *
     * @param v The view that was clicked.
     */
    override fun onClick(v: View?) {
        when (v) {
            ivFlash -> {
                toggleTorchState()
            }
            btnImageCapture -> {
                mCameraScan?.captureToScan()
            }
            ivUpArrow -> {
                bottomSheetBehavior.state = BottomSheetBehavior.STATE_HALF_EXPANDED
                ivUpArrow.isVisible = false
                ivDownArrow.isVisible = true
            }
            ivDownArrow -> {
                bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
                ivUpArrow.isVisible = true
                ivDownArrow.isVisible = false
            }
            tvCopy ->{
                val clipboard: ClipboardManager = getSystemService(CLIPBOARD_SERVICE) as ClipboardManager
                val clip = ClipData.newPlainText(label.toString(), tvData.text)
                clipboard.setPrimaryClip(clip)
                Toast.makeText(this,"COPIED",Toast.LENGTH_SHORT).show()
            }

            settings ->{
                val intent = Intent(this, SettingsActivity::class.java)
                intent.putExtra("confidence", confidence)
                intent.putExtra("autoFlashlight", autoFlashlight)
                /*intent.putExtra("isDebug",isDebug)
                intent.putExtra("isQrBarcodeDetection",isQrBarcodeDetection)*/
                startForResult.launch(intent)
            }

            back ->{
                finish()
            }
        }
    }

    /**
     * This method is used to toggle the state of torch light.
     */
    private fun toggleTorchState() {
        if (mCameraScan == null) return
        val isTorch = mCameraScan?.isTorchEnabled() ?: false
        mCameraScan?.enableTorch(!isTorch)
        ivFlash.isSelected = !isTorch
    }

    /**
     * This callback method is used to return successful output result to application for scanned QR/Bar code.
     *
     * @param result ScanResult contains data about decoded value
     */
    override fun onScanResultSuccess(result: ScanResult) {
        CoroutineScope(Dispatchers.Main).launch {
            if (timer != null) timer?.cancel()

            timer = object : CountDownTimer(5000, 1000) {
                @SuppressLint("SetTextI18n")
                override fun onTick(millisUntilFinished: Long) {
                    bottomSheet.isVisible = true
                    tvData.text =
                        "${result.barcodeFormat?.name?.let { ScanBarcodeFormat.valueOf(it) }} : ${result.text}"
                }

                override fun onFinish() {
                    bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
                    ivUpArrow.isVisible = true
                    ivDownArrow.isVisible = false
                    bottomSheet.isVisible = false
                    timer = null
                }
            }
            timer?.start()
        }
    }

    /**
     * This callback method is used to return failed output result to application for scanned QR/Bar code.
     *
     * @param error Detail message about error.
     */
    override fun onScanResultFailure(error: String) {}

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<String>, grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == CAMERA_PERMISSION_REQUEST_CODE) {
            if (PermissionUtils.requestPermissionsResult(
                    Manifest.permission.CAMERA,
                    permissions,
                    grantResults
                )
            ) {
                startCamera()
            } else {
                finish()
            }
        }
    }

    /**
     * This method is used to start the camera preview and check whether the camera permission enabled or not.
     * we are requesting runtime permission to enable if not enabled.
     */
    private fun startCamera() {
        if (mCameraScan == null) return

        if (PermissionUtils.checkPermission(this, Manifest.permission.CAMERA)) {
            mCameraScan?.startCamera()
        } else {
            PermissionUtils.requestPermissions(
                this, PERMISSIONS,
                CAMERA_PERMISSION_REQUEST_CODE
            )
        }
    }

    override fun onResume() {
        startCamera()
        val isTorch = mCameraScan?.isTorchEnabled() ?: false
        ivFlash.isSelected = isTorch
        super.onResume()
    }

    override fun onStop() {
        mCameraScan?.stopCamera()
        super.onStop()
    }

    override fun onDestroy() {
        releaseCamera()
        super.onDestroy()
    }

    /**
     * This method is used to release the camera object.
     */
    private fun releaseCamera() {
        mCameraScan?.release()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.menuSettings -> {
                val intent = Intent(this, SettingsActivity::class.java)
                intent.putExtra("confidence", confidence)
                intent.putExtra("autoFlashlight", autoFlashlight)
                /*intent.putExtra("isDebug",isDebug)
                intent.putExtra("isQrBarcodeDetection",isQrBarcodeDetection)*/
                startForResult.launch(intent)
                true
            }
            android.R.id.home -> {
                finish()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    /**
     * This method is used to initiate and configure the AppCenter to get analytics and crashes.
     */
    private fun initAppCenter() {
        AppCenter.start(
            application, "6747023a-4a96-4ce1-af34-1aad15e6afa8",
            Analytics::class.java, Crashes::class.java
        )
    }

    private val startForResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
            if (result.resultCode == Activity.RESULT_OK) {
                val intent = result.data
                if (intent != null) {
                    confidence = intent.getFloatExtra("confidence", 0.5f)
                    autoFlashlight = intent.getBooleanExtra("autoFlashlight", false)
                    autoExposure = intent.getBooleanExtra("autoExposure",false)
//                    isDebug = intent.getBooleanExtra("isDebug", false)
//                    isQrBarcodeDetection = intent.getBooleanExtra("isQrBarcodeDetection",false)
                }

                mCameraScan?.updateConfidenceValue(confidence)
                mCameraScan?.setAutoFlashlight(autoFlashlight)
                mCameraScan?.setAutoExposure(autoExposure)
//                mCameraScan?.setDebugMode(isDebug)
//                mCameraScan?.setQrBarcodeDetection(isQrBarcodeDetection)


                val editor = preference.edit()
                editor.putFloat("confidence", confidence)
                editor.putBoolean("autoFlashlight", autoFlashlight)
                editor.putBoolean("autoExposure",autoExposure)
//                editor.putBoolean("isDebug", isDebug)
//                editor.putBoolean("isQrBarcodeDetection", isQrBarcodeDetection)
                editor.commit()

            }
        }

    companion object {
        val PERMISSIONS =
            arrayOf(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE)
        private const val CAMERA_PERMISSION_REQUEST_CODE = 100
        private const val SCAN_TYPE = "any"
        private const val SCAN_TITLE = "QR/BAR CODE"
    }
}