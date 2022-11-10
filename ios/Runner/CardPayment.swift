//
//  CardPayment.swift
//  Runner
//
//  Created by Richard on 9/11/2565 BE.
//

import Foundation
import OmiseSDK



class CardPayment   {
    let client = OmiseSDK.Client.init(publicKey: "pkey_test_4xiihu5wbve7eff9s6l")
    var returnData:[String:String] = [:]
    
    func payment() async -> [String:String] {
        
        let tokenParameters = Token.CreateParameter(
            name: "JOHN DOE",
            number: "4242424242424242",
            expirationMonth: 11,
            expirationYear: 2022,
            securityCode: "123"
        )

        let request = Request<Token>(parameter: tokenParameters)
        
        client.send(request) { [weak self] (result) in
          guard let s = self else { return }

            switch result {
                case .success(let token):
                    print(token)
                    
                case .failure(let error):
                    print(error)
                
            }
        }
        
        return returnData
    }   
    
        
    
}
