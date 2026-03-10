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

class HorusDataf_1Plugin : FlutterPlugin, MethodCallHandler, ActivityAware, PluginRegistry.ActivityResultListener {
    
    private lateinit var channel: MethodChannel
    private var activity: Activity? = null
    private var pendingResult: Result? = null
    private val REQUEST_CODE_REDEBAN = 100 // Código para identificar la respuesta

    override fun onAttachedToEngine(flutterPluginBinding: FlutterPlugin.FlutterPluginBinding) {
        channel = MethodChannel(flutterPluginBinding.binaryMessenger, "horus_dataf_1")
        channel.setMethodCallHandler(this)
    }

    override fun onMethodCall(call: MethodCall, result: Result) {
        // Añadir mas tipos de transacción
        if (call.method == "ejecutarTransaccion") {
            val dataInput = call.argument<String>("data_input")
            val terminalMarca = call.argument<String>("marca") ?: "pax" // pax o urovo
            
            if (dataInput != null) {
                pendingResult = result
                lanzarRedeban(dataInput, terminalMarca)
            } else {
                result.error("ERROR_INPUT", "Falta el parámetro data_input", null)
            }
        } else {
            result.notImplemented()
        }
    }

    private fun lanzarRedeban(jsonInput: String, marca: String) {
        val intent = Intent(Intent.ACTION_SEND)
        
        // Configuración de paquete según el manual
        val packageName = if (marca.lowercase() == "urovo") 
            "rbm.urovo.wimobile.com.rbmappcomercioswm" 
        else 
            "rbm.pax.wimobile.com.rbmappcomercioswm"

        val activityName = if (marca.lowercase() == "urovo")
            "rbm.urovo.wimobile.com.rbmappcomercios.features.mainmenu.ui.MainMenuActivity"
        else
            "rbm.pax.wimobile.com.rbmappcomercios.features.mainmenu.ui.MainMenuActivity"

        val cn = ComponentName(packageName, activityName)
        intent.component = cn
        
        // Es obligatorio enviar el nombre de tu propio paquete
        intent.putExtra("package", activity?.packageName)
        // Se envía el JSON de la transacción
        intent.putExtra("data_input", jsonInput)

        activity?.startActivityForResult(intent, REQUEST_CODE_REDEBAN)
    }

    // Captura la respuesta JSON de Redeban 
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?): Boolean {
        if (requestCode == REQUEST_CODE_REDEBAN) {
            val output = data?.getStringExtra("Data_output")
            if (output != null) {
                pendingResult?.success(output)
            } else {
                pendingResult?.error("CANCELADO", "No se recibió respuesta", null)
            }
            pendingResult = null
            return true
        }
        return false
    }

    // Métodos obligatorios para manejar el ciclo de vida de la Activity
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