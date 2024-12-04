package com.mdev1008.nutriscanandroiddev.presentation.scan_page

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.OptIn
import androidx.camera.core.CameraSelector
import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.mlkit.vision.barcode.BarcodeScannerOptions
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.common.InputImage
import com.mdev1008.nutriscanandroiddev.R
import com.mdev1008.nutriscanandroiddev.databinding.FragmentScanPageBinding
import com.mdev1008.nutriscanandroiddev.domain.model.ScanItemForView
import com.mdev1008.nutriscanandroiddev.utils.Status
import com.mdev1008.nutriscanandroiddev.utils.errorLogger
import com.mdev1008.nutriscanandroiddev.utils.infoLogger
import com.mdev1008.nutriscanandroiddev.utils.showSnackBar
import kotlinx.coroutines.launch
import java.util.concurrent.Executors
import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.provider.Settings
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import com.mdev1008.nutriscanandroiddev.MainActivity

class ScanPage : Fragment() {
    private lateinit var viewBinding: FragmentScanPageBinding
    private val viewModel: ScanPageViewModel by activityViewModels<ScanPageViewModel> { ScanPageViewModel.Factory }
    private val CAMERA_REQUEST_PERMISSION_CODE = 101

    private val requestCameraPermissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()){ isGranted ->
        if (!isGranted){
            view?.let {

                val snackbar = Snackbar.make(it, "Camera Permission Required for Scanning Product", Snackbar.LENGTH_LONG)
                snackbar.setAction("Change Permission"){
                    if (isAdded){
                        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                            data = Uri.fromParts("package","com.mdev1008.nutriscanandroiddev", null)
                        }
                        startActivity(intent)
                        }
                    }
                snackbar.show()
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        viewBinding = FragmentScanPageBinding.inflate(inflater, container, false)
        return viewBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.onEvent(ScanPageEvent.ClearScanList)
        viewBinding.rvScanList.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = ScanListAdapter(emptyList()){ productId ->
                val bundle = Bundle().apply {
                    putString(getString(R.string.productId), productId)
                }
                findNavController().navigate(R.id.action_scan_page_to_product_details_page, bundle)
            }
        }
        requestCameraPermission()
        buildCameraPreview()
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED){
                viewModel.uiState.collect{ state ->
                    when(state.scanState){
                        Status.LOADING -> {
                            infoLogger("ScanPage: Loading Product Details")
                        }
                        Status.SUCCESS -> {
                            view.showSnackBar("Product found: ${state.scanList.last().productName}")
                            updateRecyclerView(state.scanList)
                        }
                        Status.FAILURE -> {
                            view.showSnackBar(state.errorMessage.toString())
                        }
                        Status.IDLE -> {
                        }
                    }
                }
            }
        }
    }

    private fun requestCameraPermission() {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED){
            requestCameraPermissionLauncher.launch(Manifest.permission.CAMERA)
       }
    }


    @OptIn(ExperimentalGetImage::class)
    private fun buildCameraPreview() {
        val options = BarcodeScannerOptions.Builder()
            .setBarcodeFormats(
                Barcode.FORMAT_UPC_A,
                Barcode.FORMAT_EAN_13
            )
            .build()
        val products = mutableMapOf<String, Int>()
        val framesCount = 30
        val barcodeScanner = BarcodeScanning.getClient(options)
        val cameraProviderFuture = ProcessCameraProvider.getInstance(requireContext())



        cameraProviderFuture.addListener({
          val cameraProvider = cameraProviderFuture.get()
            val preview = Preview.Builder().build().also {
                it.setSurfaceProvider(viewBinding.pvCameraPreview.surfaceProvider)
            }

            val imageAnalysis = ImageAnalysis.Builder()
                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                .build()

            imageAnalysis.setAnalyzer(Executors.newSingleThreadExecutor()){ imageProxy ->
                val rotationDegrees = imageProxy.imageInfo.rotationDegrees
                val inputImage = imageProxy.image?.let {
                    InputImage.fromMediaImage(it, rotationDegrees)
                }
                inputImage?.let {
                    barcodeScanner.process(it)
                        .addOnSuccessListener { barcodes ->
                            barcodes.getOrNull(0)?.rawValue?.let { code ->
                                if (products.containsKey(code)){
                                    products[code] = products.getValue(code) + 1
                                }else{
                                    products[code] = 1
                                }
                                infoLogger("$code : ${products.getValue(code)}")
                                if (products.getValue(code) == framesCount){
                                    viewModel.onEvent(ScanPageEvent.GetProductDetailsById(productId = code))
                                }
                            }
                        }
                        .addOnCompleteListener {
                            imageProxy.close()
                        }
                } ?: imageProxy.close()
            }
            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

            try {
                cameraProvider.unbindAll()
                cameraProvider.bindToLifecycle(
                    this, cameraSelector, preview, imageAnalysis
                )
            }catch (e: Exception){
                errorLogger("CameraX usecase Binding Failed")
            }
        }, ContextCompat.getMainExecutor(requireContext()))
    }


    private fun updateRecyclerView(newList: List<ScanItemForView>){
        val adapter = viewBinding.rvScanList.adapter as ScanListAdapter
        adapter.updateList(newList)
    }
}