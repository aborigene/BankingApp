import SwiftUI


// MARK: - Home View
struct HomeView: View {
    // State variable to hold the account balance, shared between views
    @State private var balance: Double = 1250.75

    // State variable to control the presentation of the payment view
    @State private var isShowingPaymentView: Bool = false

    var body: some View {
        NavigationStack {
            VStack(spacing: 30) {
                // Main content of the home screen
                VStack {
                    Image(systemName: "creditcard.fill")
                        .font(.system(size: 60))
                        .foregroundColor(.blue)
                        .padding()
                        .background(Color.blue.opacity(0.1))
                        .clipShape(Circle())
                    
                    Text("Account Balance")
                        .font(.headline)
                        .foregroundColor(.secondary)
                    
                    Text(String(format: "$%.2f", balance))
                        .font(.largeTitle)
                        .fontWeight(.bold)
                        .foregroundColor(.primary)
                }
                .padding()
                .background(Color.white)
                .cornerRadius(20)
                .shadow(color: Color.black.opacity(0.1), radius: 10, x: 0, y: 5)
                
                // Button to navigate to the payment screen
                Button(action: {
                    isShowingPaymentView = true
                }) {
                    Text("Make a Payment")
                        .font(.headline)
                        .foregroundColor(.white)
                        .padding()
                        .frame(maxWidth: .infinity)
                        .background(Color.blue)
                        .cornerRadius(15)
                        .shadow(color: Color.blue.opacity(0.3), radius: 5, x: 0, y: 5)
                }
                .padding(.horizontal)
            }
            .padding()
            .frame(maxWidth: .infinity, maxHeight: .infinity)
            .background(Color(.systemGroupedBackground))
            .navigationTitle("Your Banking App")
            .sheet(isPresented: $isShowingPaymentView) {
                PaymentView(balance: $balance)
            }
        }
    }
}

// MARK: - Payment View
struct PaymentView: View {
    // Binding to the balance from the HomeView so changes are reflected
    @Binding var balance: Double

    // State variables for form inputs
    @State private var recipient: String = ""
    @State private var amount: String = ""
    @State private var message: String = ""
    @State private var isShowingMessage: Bool = false
    @State private var isError: Bool = false
    
    // Dismiss environment variable to close the sheet view
    @Environment(\.dismiss) var dismiss

    var body: some View {
        NavigationView {
            VStack(spacing: 20) {
                Form {
                    Section(header: Text("Payment Details").font(.headline)) {
                        TextField("Recipient Name", text: $recipient)
                            .textContentType(.name)
                            .padding(.vertical, 8)
                        
                        TextField("Amount", text: $amount)
                            .keyboardType(.decimalPad)
                            .padding(.vertical, 8)
                    }
                }
                .scrollDisabled(true)
                .background(Color(.systemGroupedBackground))
                .cornerRadius(15)
                .shadow(color: Color.black.opacity(0.1), radius: 5, x: 0, y: 3)
                
                if isShowingMessage {
                    Text(message)
                        .font(.subheadline)
                        .padding(10)
                        .frame(maxWidth: .infinity)
                        .background(isError ? Color.red.opacity(0.1) : Color.green.opacity(0.1))
                        .foregroundColor(isError ? .red : .green)
                        .cornerRadius(10)
                        .transition(.opacity)
                }
                
                Button(action: processPayment) {
                    Text("Pay")
                        .font(.headline)
                        .foregroundColor(.white)
                        .padding()
                        .frame(maxWidth: .infinity)
                        .background(Color.blue)
                        .cornerRadius(15)
                        .shadow(color: Color.blue.opacity(0.3), radius: 5, x: 0, y: 5)
                }
                .padding(.horizontal)
            }
            .padding()
            .navigationTitle("New Payment")
            .toolbar {
                ToolbarItem(placement: .cancellationAction) {
                    Button("Cancel") {
                        dismiss()
                    }
                }
            }
        }
    }
    
    // Function to handle the payment logic
    private func processPayment() {
        // Convert the amount string to a Double
        guard let paymentAmount = Double(amount), paymentAmount > 0 else {
            message = "Please enter a valid amount."
            isError = true
            isShowingMessage = true
            return
        }
        
        // Check for insufficient funds
        guard balance >= paymentAmount else {
            message = "Insufficient funds."
            isError = true
            isShowingMessage = true
            return
        }
        
        // Process the payment and update the balance
        balance -= paymentAmount
        message = "Payment of \(String(format: "$%.2f", paymentAmount)) to \(recipient) was successful!"
        isError = false
        isShowingMessage = true
        
        // Reset the form fields after a short delay
        DispatchQueue.main.asyncAfter(deadline: .now() + 2) {
            self.recipient = ""
            self.amount = ""
            self.isShowingMessage = false
            dismiss()
        }
    }
}
