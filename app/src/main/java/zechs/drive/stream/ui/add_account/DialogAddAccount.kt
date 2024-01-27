package zechs.drive.stream.ui.add_account

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.Window
import android.widget.Toast
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputLayout
import zechs.drive.stream.R

class DialogAddAccount(
    context: Context,
    val onNextClickListener: (String) -> Unit
) : Dialog(context) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.dialog_new_account)

        val etNickname = findViewById<TextInputLayout>(R.id.tf_nickname).editText!!
        val nextButton = findViewById<MaterialButton>(R.id.btn_next)

        nextButton.setOnClickListener {
            if (etNickname.text.toString().isEmpty()) {
                Toast.makeText(
                    context,
                    context.getString(R.string.please_enter_a_nickname),
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                onNextClickListener.invoke(etNickname.text.toString())
                dismiss()
            }
        }
    }

}