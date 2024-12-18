package com.example.podwave.util

import android.Manifest
import android.app.Dialog
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.activity.result.ActivityResultLauncher
import androidx.core.content.ContextCompat
import com.example.podwave.R

object UIUtil {
    fun showDialog(
        context: Context,
        title: String,
        message: String,
        titleColor: String? = null,
        okButtonText: String = "",
        closeButtonText: String = "",
        showOkButton: Boolean = true,
        showCloseButton: Boolean = true,
        onOkClick: (() -> Unit)? = null,
        onCloseClick: (() -> Unit)? = null
    ) {
        val dialog = Dialog(context)
        dialog.setContentView(R.layout.layout_dialog)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        val titleView = dialog.findViewById<TextView>(R.id.tittle_layout)
        val messageView = dialog.findViewById<TextView>(R.id.message_layout)
        val okButton = dialog.findViewById<Button>(R.id.ok_button_layout)
        val closeButton = dialog.findViewById<Button>(R.id.close_button_layout)

        titleView.text = title
        messageView.text = message
        titleColor?.let {
            titleView.setTextColor(Color.parseColor(it))
        }

        okButton.text = okButtonText
        closeButton.text = closeButtonText

        okButton.visibility = if (showOkButton) View.VISIBLE else View.GONE
        closeButton.visibility = if (showCloseButton) View.VISIBLE else View.GONE

        okButton.setOnClickListener {
            onOkClick?.invoke()
            dialog.dismiss()
        }
        closeButton.setOnClickListener {
            onCloseClick?.invoke()
            dialog.dismiss()
        }
        dialog.show()
    }

    fun isNotificationPermissionGranted(context: Context): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
        } else {
            true
        }
    }

    fun requestNotificationPermission(permissionLauncher: ActivityResultLauncher<String>) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
        }
    }

}