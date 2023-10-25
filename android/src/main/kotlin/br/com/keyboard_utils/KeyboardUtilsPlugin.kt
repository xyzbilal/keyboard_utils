package br.com.keyboard_utils

import android.app.Activity
import br.com.keyboard_utils.keyboard.KeyboardHeightListener
import br.com.keyboard_utils.keyboard.KeyboardNewUtils;
import br.com.keyboard_utils.keyboard.KeyboardOptions;
import br.com.keyboard_utils.utils.KeyboardConstants.Companion.CHANNEL_IDENTIFIER
import io.flutter.embedding.engine.plugins.FlutterPlugin
import io.flutter.embedding.engine.plugins.activity.ActivityAware
import io.flutter.embedding.engine.plugins.activity.ActivityPluginBinding
import io.flutter.plugin.common.BinaryMessenger
import io.flutter.plugin.common.EventChannel
import io.flutter.plugin.common.PluginRegistry

import android.app.Dialog
import android.graphics.Point
import android.graphics.Rect
import android.os.Build
import android.util.Log
import android.view.Display
import android.view.View
import android.view.ViewTreeObserver
import android.view.Window
import android.src.main.kotlin.br.com.keyboard_utils.utils.Utils;

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
            println("显示 KeyboardUtilsPlugin 初始化")
            keyboardUtil?.unregisterKeyboardHeightListener()
            keyboardUtil = KeyboardNewUtils()
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
        println("显示 原生 onListen")
        activity?.apply {
            keyboardUtil?.registerKeyboardHeightListener(
                activity!!,
                object : KeyboardHeightListener {
                    override fun open(height: Float) {
                        println("显示 原生软键盘高度 height=$height")
                        val resultJSON = KeyboardOptions(isKeyboardOpen = true, height = height)
                        events?.success(resultJSON.toJson())
                    }

                    override fun hide() {
                        val resultJSON = KeyboardOptions(isKeyboardOpen = false, height = 0f)
                        events?.success(resultJSON.toJson())
                    }
                })
        }
    }

    override fun onCancel(arguments: Any?) {}
}
