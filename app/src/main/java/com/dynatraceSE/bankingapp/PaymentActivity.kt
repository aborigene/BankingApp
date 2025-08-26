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
import com.dynatracese.paymentlibrary.PaymentCallback
import com.dynatracese.paymentlibrary.PaymentClient
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class PaymentActivity : AppCompatActivity() {

    private lateinit var recipientEditText: EditText
    private lateinit var amountEditText: EditText
    private lateinit var confirmPaymentButtonComCrash: Button
    private lateinit var confirmPaymentButtonSemCrash: Button
    private lateinit var backButton: Button

    private var currentBalance: Double = 0.0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_payment)

        recipientEditText = findViewById(R.id.recipientEditText)
        amountEditText = findViewById(R.id.amountEditText)
        confirmPaymentButtonComCrash = findViewById(R.id.confirmPaymentButtonComCrash)
        confirmPaymentButtonSemCrash = findViewById(R.id.confirmPaymentButtonSemCrash)
        backButton = findViewById(R.id.backButton)

        currentBalance = intent.getDoubleExtra(MainActivity.EXTRA_BALANCE, 0.0)

        confirmPaymentButtonComCrash.setOnClickListener {
            handlePayment(true)
        }

        confirmPaymentButtonSemCrash.setOnClickListener {
            handlePayment(false)
        }

        backButton.setOnClickListener {
            finish() // Volta para a MainActivity
        }
    }

    private fun handlePayment(crashStatus: Boolean) {
        val creditCard = recipientEditText.text.toString().trim()
        val amountString = amountEditText.text.toString().trim()
        val paymentClient = PaymentClient("TEST_ONLY", this, crashStatus)


        if (creditCard.isEmpty()) {
            showAlert("Erro", "Por favor, insira o numero do Cartao de Credito.")
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
            .setMessage("Você deseja pagar ${NumberFormat.getCurrencyInstance(Locale("pt", "BR")).format(amount)} de $creditCard?")
            .setPositiveButton("Confirmar") { dialog, _ ->
                CoroutineScope(Dispatchers.Main).launch {
                    paymentClient.receivePayment(
                        amount = amount,
                        creditCardNumber = creditCard, // Exemplo de dados
                        vendorName = "Loja de Exemplo",
                        vendorId = "vendor_02",
                        callback = object : PaymentCallback {
                            override fun onPaymentSuccess(transactionId: String) {
                                // O pagamento foi bem-sucedido.
                                // Aqui você pode atualizar o saldo, mostrar uma mensagem de sucesso
                                // ou navegar de volta para a tela inicial.
                                //Toast.makeText(this@PaymentActivity, "Pagamento realizado! ID: $transactionId", Toast.LENGTH_LONG).show()
                            }

                            override fun onPaymentFailure(error: String) {
                                // Ocorreu um erro no pagamento.
                                // Exiba uma mensagem de erro para o usuário.
                                //Toast.makeText(this@PaymentActivity, "Falha no pagamento: $error", Toast.LENGTH_LONG).show()
                            }
                        }
                    )
                }
                val newBalance = currentBalance - amount
                val newTransaction = Transaction(
                    id = UUID.randomUUID().toString(),
                    description = "Pagamento de $creditCard",
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
                showAlert("Sucesso!", "Pagamento de ${NumberFormat.getCurrencyInstance(Locale("pt", "BR")).format(amount)} de $creditCard realizado com sucesso!")
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