// MainActivity.kt
package com.dynatraceSE.bankingapp

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.io.Serializable
import java.text.NumberFormat
import java.util.*
import kotlin.collections.ArrayList

// Data class para representar uma transação
data class Transaction(
    val id: String,
    val description: String,
    val amount: Double,
    val type: String, // "payment" ou "income"
    val date: String
) : Serializable // Necessário para passar entre Activities

// Adaptador para exibir a lista de transações
class TransactionAdapter(private val transactions: List<Transaction>) :
    RecyclerView.Adapter<TransactionAdapter.TransactionViewHolder>() {

    class TransactionViewHolder(itemView: android.view.View) : RecyclerView.ViewHolder(itemView) {
        val descriptionTextView: TextView = itemView.findViewById(R.id.transactionDescription)
        val amountTextView: TextView = itemView.findViewById(R.id.transactionAmount)
    }

    override fun onCreateViewHolder(parent: android.view.ViewGroup, viewType: Int): TransactionViewHolder {
        val view = android.view.LayoutInflater.from(parent.context)
            .inflate(R.layout.item_transaction, parent, false)
        return TransactionViewHolder(view)
    }

    override fun onBindViewHolder(holder: TransactionViewHolder, position: Int) {
        val transaction = transactions[position]
        holder.descriptionTextView.text = transaction.description
        val formattedAmount = NumberFormat.getCurrencyInstance(Locale("pt", "BR")).format(transaction.amount)
        holder.amountTextView.text = if (transaction.type == "payment") "- $formattedAmount" else "+ $formattedAmount"
        holder.amountTextView.setTextColor(
            if (transaction.type == "payment") android.graphics.Color.RED else android.graphics.Color.GREEN
        )
    }

    override fun getItemCount(): Int = transactions.size
}

class MainActivity : AppCompatActivity() {

    private lateinit var balanceTextView: TextView
    private lateinit var payButton: Button
    private lateinit var transactionsRecyclerView: RecyclerView
    private lateinit var transactionAdapter: TransactionAdapter

    private var currentBalance: Double = 1500.00
    private val transactionList: MutableList<Transaction> = ArrayList()

    companion object {
        const val REQUEST_CODE_PAYMENT = 1
        const val EXTRA_BALANCE = "extra_balance"
        const val EXTRA_TRANSACTIONS = "extra_transactions"
        const val RESULT_NEW_BALANCE = "result_new_balance"
        const val RESULT_NEW_TRANSACTION = "result_new_transaction"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        balanceTextView = findViewById(R.id.balanceTextView)
        payButton = findViewById(R.id.payButton)
        transactionsRecyclerView = findViewById(R.id.transactionsRecyclerView)

        updateBalanceDisplay()

        payButton.setOnClickListener {
            val intent = Intent(this, PaymentActivity::class.java)
            intent.putExtra(EXTRA_BALANCE, currentBalance)
            startActivityForResult(intent, REQUEST_CODE_PAYMENT)
        }

        transactionsRecyclerView.layoutManager = LinearLayoutManager(this)
        transactionAdapter = TransactionAdapter(transactionList)
        transactionsRecyclerView.adapter = transactionAdapter
    }

    // Atualiza a exibição do saldo
    private fun updateBalanceDisplay() {
        val formattedBalance = NumberFormat.getCurrencyInstance(Locale("pt", "BR")).format(currentBalance)
        balanceTextView.text = formattedBalance
    }

    // Lida com o resultado da PaymentActivity
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE_PAYMENT && resultCode == RESULT_OK) {
            data?.let {
                val newBalance = it.getDoubleExtra(RESULT_NEW_BALANCE, currentBalance)
                val newTransaction = it.getSerializableExtra(RESULT_NEW_TRANSACTION) as? Transaction

                currentBalance = newBalance
                updateBalanceDisplay()

                newTransaction?.let { transaction ->
                    transactionList.add(0, transaction) // Adiciona no início da lista
                    transactionAdapter.notifyItemInserted(0)
                }
            }
        }
    }
}