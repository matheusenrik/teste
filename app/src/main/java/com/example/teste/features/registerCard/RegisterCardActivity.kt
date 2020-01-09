package com.example.teste.features.registerCard

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import com.example.teste.R
import com.example.teste.data.model.User
import com.example.teste.databinding.ActivityRegisterCardBinding
import com.example.teste.features.paymentFeature.PaymentActivity
import com.example.teste.modules.Utils
import com.example.teste.modules.changeText
import com.example.teste.modules.verifyFieldHasVoids
import kotlinx.android.synthetic.main.activity_register_card.*
import org.koin.androidx.viewmodel.ext.android.viewModel


class RegisterCardActivity : AppCompatActivity() {
    private val registerViewModel: RegisterCardViewModel by viewModel()
    private lateinit var binding: ActivityRegisterCardBinding
    private var maskDtControl = true
    private var maskNumberControl = true
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_register_card)
        binding.viewModel = this.registerViewModel
        binding.lifecycleOwner = this
        recoveryUser()
        setToolbar()
        controlVisibilityButton()
        observableFields()
        clickRegisterCard()

    }

    private fun recoveryUser() {
        val user = intent.getSerializableExtra(resources.getString(R.string.UserPayment)) as User
        registerViewModel.userRecovery(user)
    }

    private fun observableFields() {
        holderNameEd.changeText {
            controlVisibilityButton()
        }
        numberCardEd.changeText {
            controlVisibilityButton()
            maskNumberCardControl()

        }
        cvvCardEd.changeText {
            controlVisibilityButton()
        }
        expirationDateEd.changeText {
            controlVisibilityButton()
            maskDataControl()
        }

    }

    private fun controlVisibilityButton() {
        paymentButton.isVisible = !EditText(this).verifyFieldHasVoids(
            holderNameEd,
            numberCardEd,
            cvvCardEd,
            expirationDateEd
        )
    }


    private fun verifyCredCardValide(): Boolean {
        val approvedCvv =
            registerViewModel.verifyNumeberCharacterCvv(registerViewModel.card.value?.cvvCard ?: "")
        val approvedName = registerViewModel.verifyNameContainsNumber(
            registerViewModel.card.value?.holderName ?: ""
        )
        val approvedDate =
            registerViewModel.verifyValidateDate(registerViewModel.card.value?.expirationDate ?: "")

        val removeMaskNumberCard = registerViewModel.card.value?.numberCard?.replace(" ", "")
        val approvedNumber = registerViewModel.verifyNumeberCharacterNumberCard(
            removeMaskNumberCard ?: ""
        )
        if (!approvedCvv) {
            binding.cvvCardTl.error = "cvv invalido"
            binding.cvvCardTl.isErrorEnabled = true
        } else {
            binding.cvvCardTl.isErrorEnabled = false
        }
        if (!approvedName) {
            binding.holderNameTl.isErrorEnabled = true
            binding.holderNameTl.error = "name errado"
        } else {
            binding.holderNameTl.isErrorEnabled = false
        }
        if (!approvedNumber) {
            binding.numberCardTl.isErrorEnabled = true
            binding.numberCardTl.error = "numero errado"
        } else {
            binding.numberCardTl.isErrorEnabled = false
        }
        if (!approvedDate) {
            binding.expirationDateTl.isErrorEnabled = true
            binding.expirationDateTl.error = "data errada"
        } else {
            binding.expirationDateTl.isErrorEnabled = false
        }

        return (approvedCvv && approvedName
                && approvedNumber && approvedDate)
    }

    private fun maskDataControl() {
        if (maskDtControl) {
            maskDtControl = false
            val dataWithMask = Utils.maskDate(registerViewModel.card.value?.expirationDate)
            binding.expirationDateEd.setText(dataWithMask)
            binding.expirationDateEd.setSelection(
                registerViewModel.card.value?.expirationDate?.length ?: 0
            )
        } else {
            maskDtControl = true
        }
    }

    private fun maskNumberCardControl() {
       if (maskNumberControl) {
            maskNumberControl = false
            val maskNumberCard = Utils.maskNumberCard(registerViewModel.card.value?.numberCard)
            binding.numberCardEd.setText(maskNumberCard)
            binding.numberCardEd.setSelection(registerViewModel.card.value?.numberCard?.length ?: 0)
        } else {
            maskNumberControl = true
        }
    }

    private fun setToolbar() {
        setSupportActionBar(toolbarRegister)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeButtonEnabled(true)
        supportActionBar?.title = ""
        supportActionBar?.setHomeAsUpIndicator(R.drawable.back_left_48dp)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            android.R.id.home
            -> {
                onBackPressed()
            }
            else -> return super.onOptionsItemSelected(item)
        }
        return true
    }

    private fun clickRegisterCard() {
        paymentButton.setOnClickListener {
             if (verifyCredCardValide()) {
                if (registerViewModel.saveCard()) {
                    val intent = Intent(this, PaymentActivity::class.java)
                    intent.putExtra(
                        resources.getString(R.string.UserPayment),
                        registerViewModel.user
                    )

                    startActivity(intent)
                }
            }
        }
    }
}



