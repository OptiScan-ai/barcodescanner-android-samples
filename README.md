# OptiScan Demo APP

OptiScan SDK Integration Steps 

 

Import Optiscan sdk as library module into project 

To use your Android library's code in another app module, proceed as follows: 

1.Create new project in android studio. 

2.Go to File > New > Import Module... 

3.Select the source directory of the Module (OptiScan library) to import and click Finish. Ex: D:\Project\OptiScan Library\optiscan (Here optiscan is a root folder for our optiscan library. So, we need to pick optiscan folder to import library into our project) 

4.Open Project Structure Dialog (You can open the PSD by selecting File > Project Structure) and from the left panel click on Dependencies. 

5.Select the module from the Module (Middle) section in which you want to add module dependency. 

6.Click the (+) icon from the Declared Dependencies section and click Module Dependency. 

7.Select the module and click Ok. 

8.Open your build.gradle file and check that the module is now listed under dependencies if not then add this as dependency. 
```gradle
 dependencies { 
              implementation project(':optiscan) 
      } 
      
```

9.Make sure the library is listed at the top of your settings.gradle file, as shown here for a library named "optiscan". 
```gradle 
include ':optiscan' ':app' 
```
 

10.Click Sync Project with Gradle Files. 

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
