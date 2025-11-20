package br.com.keyboard_utils

import android.app.Activity
import br.com.keyboard_utils.keyboard.KeyboardHeightListener
import br.com.keyboard_utils.keyboard.KeyboardNewUtils
import br.com.keyboard_utils.keyboard.KeyboardOptions
import br.com.keyboard_utils.utils.KeyboardConstants.Companion.CHANNEL_IDENTIFIER
import io.flutter.embedding.engine.plugins.FlutterPlugin
import io.flutter.embedding.engine.plugins.activity.ActivityAware
import io.flutter.embedding.engine.plugins.activity.ActivityPluginBinding
import io.flutter.plugin.common.BinaryMessenger
import io.flutter.plugin.common.EventChannel

class KeyboardUtilsPlugin : FlutterPlugin, ActivityAware, EventChannel.StreamHandler {

    private var keyboardUtil: KeyboardNewUtils? = null
    private var flutterPluginBinding: FlutterPlugin.FlutterPluginBinding? = null
    private var activityPluginBinding: ActivityPluginBinding? = null
    private var activity: Activity? = null
    private var eventChannel: EventChannel? = null

    private fun setup(activity: Activity?, messenger: BinaryMessenger) {
        if (eventChannel == null) {
            eventChannel = EventChannel(messenger, CHANNEL_IDENTIFIER)
            eventChannel?.setStreamHandler(this)
        }

        this.activity = activity

        if (this.activity != null) {
            println("KeyboardUtilsPlugin init")
            keyboardUtil?.unregisterKeyboardHeightListener()
            keyboardUtil = KeyboardNewUtils()
        }
    }

    private fun tearDown() {
        eventChannel = null
        activityPluginBinding = null
        keyboardUtil?.unregisterKeyboardHeightListener()
        keyboardUtil = null
        activity = null
    }

    // --- FlutterPlugin ---

    override fun onAttachedToEngine(binding: FlutterPlugin.FlutterPluginBinding) {
        flutterPluginBinding = binding
        // Engine bağlandığında henüz activity yok, sadece channel kuruluyor
        setup(null, binding.binaryMessenger)
    }

    override fun onDetachedFromEngine(binding: FlutterPlugin.FlutterPluginBinding) {
        tearDown()
        flutterPluginBinding = null
    }

    // --- ActivityAware ---

    override fun onAttachedToActivity(binding: ActivityPluginBinding) {
        activityPluginBinding = binding
        flutterPluginBinding?.let {
            setup(binding.activity, it.binaryMessenger)
        }
    }

    override fun onDetachedFromActivity() {
        tearDown()
        activityPluginBinding = null
    }

    override fun onReattachedToActivityForConfigChanges(binding: ActivityPluginBinding) {
        onAttachedToActivity(binding)
    }

    override fun onDetachedFromActivityForConfigChanges() {
        onDetachedFromActivity()
    }

    // --- EventChannel.StreamHandler ---

    override fun onListen(arguments: Any?, events: EventChannel.EventSink?) {
        println("KeyboardUtilsPlugin onListen")
        activity?.let { act ->
            keyboardUtil?.registerKeyboardHeightListener(
                act,
                object : KeyboardHeightListener {
                    override fun open(height: Float) {
                        println("Keyboard height = $height")
                        val resultJSON = KeyboardOptions(isKeyboardOpen = true, height = height)
                        events?.success(resultJSON.toJson())
                    }

                    override fun hide() {
                        val resultJSON = KeyboardOptions(isKeyboardOpen = false, height = 0f)
                        events?.success(resultJSON.toJson())
                    }
                }
            )
        }
    }

    override fun onCancel(arguments: Any?) {
        // Gerekirse burada listener temizliği yapabilirsin
    }
}
