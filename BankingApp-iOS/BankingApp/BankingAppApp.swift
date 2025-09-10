//
//  BankingAppApp.swift
//  BankingApp
//
//  Created by Simoes, Igor on 09/09/25.
//

import SwiftUI

//// MARK: - App Entry Point
//@main
//struct BankingApp: App {
//    var body: some Scene {
//        WindowGroup {
//            HomeView()
//        }
//    }
//}

import PaymentLibrary
import SimpleMath



// The main entry point for your application
@main
struct BankingApp: App {
    
    // One-time initialization of the PaymentClient
    // This is called when the application launches
    init() {
        let SM = SimpleAlgorithms()
        let fibonacci: Int = SM.factorial(n: 10)
        print("factorial of 10 is : \(fibonacci)")
        // You should replace this with your actual API base URL
        // or keep "TEST_ONLY" for a simulated payment environment.
        //let PaymentClientInstance = PaymentClient(baseUrl: "TEST_ONLY")
        //_ = PaymentClient.getInstance(baseUrl: "TEST_ONLY")
        //let myPC = PaymentClient.shared
        //let myPC = PaymentLibrary.PaymentClient.shared
        _ = PaymentClient.getInstance(baseUrl: "TEST_ONLY")
        let pc = PaymentClient.shared
    }
    
    var body: some Scene {
        WindowGroup {
            HomeView()
        }
    }
}
