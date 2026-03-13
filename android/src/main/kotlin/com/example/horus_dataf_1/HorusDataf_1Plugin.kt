package com.example.horus_dataf_1

import android.app.Activity
import android.content.ComponentName
import android.content.Intent
import io.flutter.embedding.engine.plugins.FlutterPlugin
import io.flutter.embedding.engine.plugins.activity.ActivityAware
import io.flutter.embedding.engine.plugins.activity.ActivityPluginBinding
import io.flutter.plugin.common.MethodCall
import io.flutter.plugin.common.MethodChannel
import io.flutter.plugin.common.MethodChannel.MethodCallHandler
import io.flutter.plugin.common.MethodChannel.Result
import io.flutter.plugin.common.PluginRegistry
import com.nexgo.oem.common.CloudPosSdk
import com.nexgo.smartpos.api.device.DeviceEngine

class HorusDataf_1Plugin : FlutterPlugin, MethodCallHandler, ActivityAware, PluginRegistry.ActivityResultListener {
    
    private lateinit var channel: MethodChannel
    private var activity: Activity? = null
    private var pendingResult: Result? = null
    private var deviceEngine: DeviceEngine? = null // Motor de Nexgo
    private val REQUEST_CODE_REDEBAN = 100

    override fun onAttachedToEngine(flutterPluginBinding: FlutterPlugin.FlutterPluginBinding) {
        channel = MethodChannel(flutterPluginBinding.binaryMessenger, "horus_dataf_1")
        channel.setMethodCallHandler(this)
        
        // Inicializar el SDK de Nexgo al arrancar el motor de Flutter
        try {
            deviceEngine = CloudPosSdk.getDeviceEngine(flutterPluginBinding.applicationContext)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onMethodCall(call: MethodCall, result: Result) {
        when (call.method) {
            "ejecutarTransaccion" -> {
                val dataInput = call.argument<String>("data_input")
                if (activity == null) {
                    result.error("NO_ACTIVITY", "La actividad es nula", null)
                    return
                }
                pendingResult = result
                lanzarRedeban(dataInput)
            }
            "imprimirTicket" -> {
                // Ejemplo de uso del hardware interno de Nexgo usando el .aar
                imprimirTexto(call.argument<String>("text") ?: "")
                result.success(true)
            }
            else -> result.notImplemented()
        }
    }

    private fun lanzarRedeban(jsonInput: String?) {
        val intent = Intent().apply {
            // Asegúrate de que este sea el nombre real del paquete de Redeban en el dispositivo
            component = ComponentName("com.redeban.smartpos", "com.redeban.smartpos.MainActivity")
            putExtra("package", activity?.packageName)
            putExtra("data_input", jsonInput)
        }

        try {
            activity?.startActivityForResult(intent, REQUEST_CODE_REDEBAN)
        } catch (e: Exception) {
            pendingResult?.error("APP_NOT_FOUND", "No se encontró la app de Redeban", e.message)
            pendingResult = null
        }
    }

    private fun imprimirTexto(texto: String) {
        val printer = deviceEngine?.printer
        printer?.initPrinter()
        printer?.appendLine(texto)
        printer?.startPrint(false, null)
    }

    // --- Gestión de ciclo de vida (ActivityAware) ---
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?): Boolean {
        if (requestCode == REQUEST_CODE_REDEBAN) {
            val output = data?.getStringExtra("Data_output")
            if (output != null) {
                pendingResult?.success(output)
            } else {
                pendingResult?.error("CANCELADO", "Operación cancelada o sin respuesta", null)
            }
            pendingResult = null
            return true
        }
        return false
    }

    override fun onAttachedToActivity(binding: ActivityPluginBinding) {
        activity = binding.activity
        binding.addActivityResultListener(this)
    }

    override fun onDetachedFromActivityForConfigChanges() { activity = null }
    override fun onReattachedToActivityForConfigChanges(binding: ActivityPluginBinding) {
        activity = binding.activity
        binding.addActivityResultListener(this)
    }
    override fun onDetachedFromActivity() { activity = null }
    override fun onDetachedFromEngine(binding: FlutterPlugin.FlutterPluginBinding) {
        channel.setMethodCallHandler(null)
    }
}