# OptiScan Demo APP

OptiScan SDK Integration Steps 

To use our Android library's code in another app module, proceed as follows: 

1.Open your build.gradle file and check that the module is now listed under dependencies if not then add this as dependency. 
```gradle
 dependencies { 
           implementation 'ai.optiscan:optiscan:x.x.x' //1.0.1 is latest version
           } 
      
```
 
2.Click Sync Project with Gradle Files. 

## Runtime Permission 

Add the below camera permission to your AndroidManifest.xml file 
```gradle 
<uses-permission android:name="android.permission.CAMERA" /> 
```
 

## Add UI dependency in application 

Add the below UI view components in application layout file. 
```gradle
<androidx.camera.view.PreviewView 
    android:id="@+id/previewView" 
    android:layout_width="match_parent" 
    android:layout_height="match_parent" /> 
 
<com.obs.optiscan.scanner.ViewFinderView 
    android:id="@+id/viewfinderView" 
    android:layout_width="match_parent" 
    android:layout_height="match_parent" /> 
 
<com.obs.optiscan.scanner.tensorflow.view.OverlayView 
    android:id="@+id/tracking_overlay" 
    android:layout_width="match_parent" 
    android:layout_height="match_parent" /> 
```
 

These three view components are mandatory. Camera preview used to display the preview for camera and ViewFinderView and OverlayView are used to show bounding box once we got success result by decode process. 

 

## Simple Usage access from Activity 

In Activity we need to initialize the OptiscanFactory to create a session for scanning process. It will return CameraScan object. 

 
```gradle
Val mCameraScan = OptiScanFactory.createScanSession(this, binding.previewView) 

//We can config other possible properties as we required. 

mCameraScan?.setOnScanResultCallback(this) 
    ?.setPlayBeep(true) 
    ?.setVibrate(false) 
    ?.setCameraConfig(CameraConfig()) 
    ?.setAutoFlashlight(autoFlashlight) 
    ?.setDebugMode(isDebug) 
    ?.setScanType(scanType) 
    ?.setDarkLightLux(4f) 
    ?.setBrightLightLux(100f) 
    ?.setQrBarcodeDetection(isQrBarcodeDetection) 
    ?.bindFlashlightView(binding.ivFlash) 
    ?.bindOverlayView(binding.trackingOverlay) 
    ?.updateConfidenceValue(confidence) 
    ?.setIsContinuousScan(isContinuousScan) 

    /**
     * Once Camera Permission Allowed by user we can start Optiscan using below command
     */
     
    if (PermissionUtils.checkPermission(this, Manifest.permission.CAMERA)) {
            mCameraScan?.startCamera()
        }

    /**
     * This method is used to Stop the camera object.
     */

    override fun onStop() {
        mCameraScan?.stopCamera()
        super.onStop()
    }
   
    /**
     * This method is used to release the camera object.
     */
    override fun onDestroy() {
         mCameraScan?.release()
        super.onDestroy()
    }
```
 

We can get decode value result in below callback methods. It will be triggered once input frame processed. 

 
```gradle
override fun onScanResultSuccess(result: ScanResult) { 
	  CoroutineScope(Dispatchers.Main).launch { 
   	    if (timer != null) timer?.cancel() 
 
   timer = object : CountDownTimer(5000, 1000) { 
            	  

  override fun onTick(millisUntilFinished: Long) { 
       	binding.tvData.text = ${result.text}" 
      } 
 
  override fun onFinish() { 
          timer = null 
  }  
} 
   timer?.start() 
} 

} 
 
override fun onScanResultFailure(error: String) {}
```

## Screenshots
![device-2021-11-11-162104](https://user-images.githubusercontent.com/93595331/141309376-46ca133d-6977-4a24-8adb-1a423853e1be.png) ![device-2021-11-11-193112](https://user-images.githubusercontent.com/93595331/141311026-b03dc378-ae63-4fb9-a39b-bf0b9613839e.png)

https://user-images.githubusercontent.com/93595331/141416713-2eb0bef8-b88a-4a54-a0e9-31763c2d25b9.mp4



