package zechs.drive.stream.ui.add_client

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.Window
import android.widget.EditText
import androidx.appcompat.widget.AppCompatImageButton
import androidx.core.view.isGone
import androidx.core.view.isVisible
import com.google.android.material.button.MaterialButton
import com.google.android.material.chip.Chip
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textfield.TextInputLayout
import com.google.android.material.textview.MaterialTextView
import zechs.drive.stream.R
import zechs.drive.stream.data.model.Client
import zechs.drive.stream.utils.util.GoogleClientValidator
import zechs.drive.stream.utils.util.UrlValidator

class DialogAddClient(
    context: Context,
    val client: Client? = null,
    val onSubmitClickListener: (Client) -> Unit
) : Dialog(context, R.style.ThemeOverlay_Fade_MaterialAlertDialog) {

    private lateinit var title: MaterialTextView
    private lateinit var tfClientId: TextInputLayout
    private lateinit var etClientId: EditText
    private lateinit var tfClientSecret: TextInputLayout
    private lateinit var etClientSecret: EditText
    private lateinit var tfRedirectUri: TextInputLayout
    private lateinit var etRedirectUri: EditText
    private lateinit var btnClientInfo: AppCompatImageButton
    private lateinit var submitButton: MaterialButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.dialog_client)

        title = findViewById(R.id.tv_title)
        tfClientId = findViewById(R.id.tf_client_id)
        etClientId = tfClientId.editText!!
        tfClientSecret = findViewById(R.id.tf_client_secret)
        etClientSecret = tfClientSecret.editText!!
        tfRedirectUri = findViewById(R.id.tf_redirect_uri)
        etRedirectUri = tfRedirectUri.editText!!
        btnClientInfo = findViewById(R.id.btnClientInfo)
        val chipScope = findViewById<Chip>(R.id.chipScope)
        submitButton = findViewById(R.id.btn_submit)

        submitButton.setOnClickListener {
            if (!areInputsValid()) {
                return@setOnClickListener
            }
            onSubmitClickListener.invoke(
                Client(
                    id = etClientId.text.trim().toString(),
                    secret = etClientSecret.text.trim().toString(),
                    redirectUri = etRedirectUri.text.trim().toString()
                )
            )
            dismiss()
        }

        chipScope.setOnCloseIconClickListener {
            MaterialAlertDialogBuilder(context)
                .setTitle(context.getString(R.string.important_notice))
                .setMessage(context.getString(R.string.important_notice_scope_warning))
                .setPositiveButton(R.string.ok) { dialog, _ ->
                    dialog.dismiss()
                }.show()
        }

        setupDialog()
    }

    private fun areInputsValid(): Boolean {
        val clientId = etClientId.text.trim().toString()
        val clientSecret = etClientSecret.text.trim().toString()
        val redirectUri = etRedirectUri.text.trim().toString()

        var isValid = true

        // Validate Client ID
        if (client == null) {
            if (!GoogleClientValidator.isValidClientId(clientId)) {
                tfClientId.isErrorEnabled = true
                tfClientId.error = "Invalid Client ID. Use the one provided by Google."
                isValid = false
            } else {
                tfClientId.isErrorEnabled = false
                tfClientId.error = null
            }
        }

        // Validate Client Secret
        if (clientSecret.isBlank()) {
            tfClientSecret.isErrorEnabled = true
            tfClientSecret.error = "Client Secret is required."
            isValid = false
        } else {
            tfClientSecret.isErrorEnabled = false
            tfClientSecret.error = null
        }

        // Validate Redirect URI
        if (!UrlValidator.startsWithHttpOrHttps(redirectUri)) {
            tfRedirectUri.isErrorEnabled = true
            tfRedirectUri.error = "Invalid URI. It must start with 'http' or 'https'."
            isValid = false
        } else {
            tfRedirectUri.isErrorEnabled = false
            tfRedirectUri.error = null
        }

        return isValid
    }

    private fun setupDialog() {
        if (client != null) {
            title.text = context.getString(R.string.edit_client)
            etClientId.setText(client.id)
            etClientId.isEnabled = false
            btnClientInfo.isVisible = true
            btnClientInfo.setOnClickListener {
                MaterialAlertDialogBuilder(context)
                    .setTitle(context.getString(R.string.important_notice))
                    .setMessage(context.getString(R.string.important_notice_client_id_warning))
                    .setPositiveButton(R.string.ok) { dialog, _ ->
                        dialog.dismiss()
                    }.show()
            }
            etClientSecret.setText(client.secret)
            etRedirectUri.setText(client.redirectUri)
            submitButton.text = context.getString(R.string.done)
        } else {
            btnClientInfo.isGone = true
            title.text = context.getString(R.string.add_new_client)
            submitButton.text = context.getString(R.string.submit)
        }
    }

}