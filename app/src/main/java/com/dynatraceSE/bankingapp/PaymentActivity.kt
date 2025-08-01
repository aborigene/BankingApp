// PaymentActivity.kt
package com.dynatraceSE.bankingapp

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import java.text.NumberFormat
import java.util.*

class PaymentActivity : AppCompatActivity() {

    private lateinit var recipientEditText: EditText
    private lateinit var amountEditText: EditText
    private lateinit var confirmPaymentButton: Button
    private lateinit var backButton: Button

    private var currentBalance: Double = 0.0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_payment)

        recipientEditText = findViewById(R.id.recipientEditText)
        amountEditText = findViewById(R.id.amountEditText)
        confirmPaymentButton = findViewById(R.id.confirmPaymentButton)
        backButton = findViewById(R.id.backButton)

        currentBalance = intent.getDoubleExtra(MainActivity.EXTRA_BALANCE, 0.0)

        confirmPaymentButton.setOnClickListener {
            handlePayment()
        }

        backButton.setOnClickListener {
            finish() // Volta para a MainActivity
        }
    }

    private fun handlePayment() {
        val recipient = recipientEditText.text.toString().trim()
        val amountString = amountEditText.text.toString().trim()

        if (recipient.isEmpty()) {
            showAlert("Erro", "Por favor, insira um beneficiário.")
            return
        }

        val amount = amountString.toDoubleOrNull()
        if (amount == null || amount <= 0) {
            showAlert("Erro", "Por favor, insira um valor válido para o pagamento.")
            return
        }

        if (amount > currentBalance) {
            showAlert("Erro", "Saldo insuficiente para realizar este pagamento.")
            return
        }

        // Confirmação do pagamento
        AlertDialog.Builder(this)
            .setTitle("Confirmar Pagamento")
            .setMessage("Você deseja pagar ${NumberFormat.getCurrencyInstance(Locale("pt", "BR")).format(amount)} para $recipient?")
            .setPositiveButton("Confirmar") { dialog, _ ->
                val newBalance = currentBalance - amount
                val newTransaction = Transaction(
                    id = UUID.randomUUID().toString(),
                    description = "Pagamento para $recipient",
                    amount = amount,
                    type = "payment",
                    date = Calendar.getInstance().time.toLocaleString()
                )

                val resultIntent = Intent().apply {
                    putExtra(MainActivity.RESULT_NEW_BALANCE, newBalance)
                    putExtra(MainActivity.RESULT_NEW_TRANSACTION, newTransaction)
                }
                setResult(Activity.RESULT_OK, resultIntent)
                finish() // Finaliza a PaymentActivity e retorna para a MainActivity
                showAlert("Sucesso!", "Pagamento de ${NumberFormat.getCurrencyInstance(Locale("pt", "BR")).format(amount)} para $recipient realizado com sucesso!")
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    private fun showAlert(title: String, message: String) {
        AlertDialog.Builder(this)
            .setTitle(title)
            .setMessage(message)
            .setPositiveButton("OK", null)
            .show()
    }
}