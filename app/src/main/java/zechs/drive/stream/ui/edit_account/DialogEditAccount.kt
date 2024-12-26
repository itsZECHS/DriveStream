package zechs.drive.stream.ui.edit_account

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.util.Log
import android.view.Window
import android.widget.Toast
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputLayout
import com.google.android.material.textview.MaterialTextView
import zechs.drive.stream.R

class DialogEditAccount(
    context: Context,
    val name: String,
    val onUpdateClickListener: (String) -> Unit
) : Dialog(context) {

    companion object {
        const val TAG = "DialogEditAccount"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        // Can reuse this same layout from DialogAddAccount
        setContentView(R.layout.dialog_new_account)

        val title = findViewById<MaterialTextView>(R.id.tv_title)
        val etNickname = findViewById<TextInputLayout>(R.id.tf_nickname).editText!!
        val nextButton = findViewById<MaterialButton>(R.id.btn_next)

        etNickname.text = Editable.Factory.getInstance().newEditable(name)
        nextButton.text = context.getString(R.string.update)
        title.text = context.getString(R.string.edit_nickname)

        nextButton.setOnClickListener {
            if (etNickname.text.toString().isEmpty()) {
                Toast.makeText(
                    context,
                    context.getString(R.string.nickname_cannot_be_empty),
                    Toast.LENGTH_SHORT
                ).show()
            } else if (etNickname.text.toString() == name) {
                Log.d(TAG, "Ignoring update, nickname is the same")
            } else {
                onUpdateClickListener.invoke(etNickname.text.toString())
                dismiss()
            }
        }
    }

}