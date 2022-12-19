package br.com.keyboard_utils

import android.app.Activity
import br.com.keyboard_utils.keyboard.DisplayUtil
import br.com.keyboard_utils.keyboard.KeyboardHeightListener
import br.com.keyboard_utils.keyboard.KeyboardUtils;
import br.com.keyboard_utils.keyboard.KeyboardOptions;
import br.com.keyboard_utils.utils.KeyboardConstants.Companion.CHANNEL_IDENTIFIER
import io.flutter.embedding.engine.plugins.FlutterPlugin
import io.flutter.embedding.engine.plugins.activity.ActivityAware
import io.flutter.embedding.engine.plugins.activity.ActivityPluginBinding
import io.flutter.plugin.common.BinaryMessenger
import io.flutter.plugin.common.EventChannel
import io.flutter.plugin.common.PluginRegistry

class KeyboardUtilsPlugin : FlutterPlugin, ActivityAware, EventChannel.StreamHandler {
    private var keyboardUtil: KeyboardUtils? = null
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
            keyboardUtil?.unregisterKeyboardHeightListener()
            keyboardUtil = KeyboardUtils()
        }
    }

    private fun tearDown() {
        eventChannel = null
        activityPluginBinding = null
        keyboardUtil?.unregisterKeyboardHeightListener()
        keyboardUtil = null
    }

    companion object {
        @JvmStatic
        fun registerWith(registrar: PluginRegistry.Registrar) {
            if (registrar.activity() == null) {
                return
            }

            val keyboardUtilsPlugin = KeyboardUtilsPlugin()
            keyboardUtilsPlugin.setup(registrar.activity(), registrar.messenger())
        }
    }

    override fun onAttachedToEngine(binding: FlutterPlugin.FlutterPluginBinding) {
        this.flutterPluginBinding = binding
        setup(null, binding.binaryMessenger)
    }

    override fun onDetachedFromEngine(binding: FlutterPlugin.FlutterPluginBinding) {
        flutterPluginBinding = null
    }

    override fun onAttachedToActivity(binding: ActivityPluginBinding) {
        activityPluginBinding = binding
        if (flutterPluginBinding != null) {
            setup(binding.activity, flutterPluginBinding!!.binaryMessenger)
        }
    }

    override fun onDetachedFromActivity() {
        tearDown()
    }

    override fun onReattachedToActivityForConfigChanges(binding: ActivityPluginBinding) {
        onAttachedToActivity(binding)
    }

    override fun onDetachedFromActivityForConfigChanges() {
        onDetachedFromActivity()
    }

    override fun onListen(arguments: Any?, events: EventChannel.EventSink?) {
        activity?.apply {
            keyboardUtil?.registerKeyboardHeightListener(activity, object : KeyboardHeightListener {
                override fun open(height: Int) {
                    val tempHeight = DisplayUtil.pxTodp(activity, height.toFloat())
                    val resultJSON = KeyboardOptions(isKeyboardOpen = true, height = tempHeight)
                    events?.success(resultJSON.toJson())
                }

                override fun hide() {
                    val resultJSON = KeyboardOptions(isKeyboardOpen = false, height = 0)
                    events?.success(resultJSON.toJson())
                }
            })
        }
    }

    override fun onCancel(arguments: Any?) {}
}
