import UIKit
import Flutter
import OmiseSDK

@UIApplicationMain
@objc class AppDelegate: FlutterAppDelegate {
  override func application(
    _ application: UIApplication,
    didFinishLaunchingWithOptions launchOptions: [UIApplication.LaunchOptionsKey: Any]?
  ) -> Bool {
      
      // Flutter method channel           
      let controller : FlutterViewController = window?.rootViewController as! FlutterViewController
      let channel = FlutterMethodChannel(name: "io.mobileacademy/omise", binaryMessenger: controller.binaryMessenger)
      
      channel.setMethodCallHandler({
        [weak self] (call: FlutterMethodCall, result: @escaping FlutterResult) -> Void in
          
          guard call.method == "showCreditCardForm" else {
              result(FlutterMethodNotImplemented)
              return
         }
          guard let args = call.arguments as? [String:String] else {return}
          
          self?.getOmiseToken(flutterResult: result, args: args)
      })


    GeneratedPluginRegistrant.register(with: self)
    return super.application(application, didFinishLaunchingWithOptions: launchOptions)
  }
    
    func getOmiseToken(flutterResult: @escaping FlutterResult, args: [String:String])  {
        let client = OmiseSDK.Client.init(publicKey: "pkey_test_4xiihu5wbve7eff9s6l")
        
        
        let tokenParameters = Token.CreateParameter(
            name: args["name"]!,
            number: args["number"]!,
            expirationMonth: Int(args["expirationMonth"]!) ?? 0,
            expirationYear: Int(args["expirationYear"]!) ?? 0,
            securityCode: args["securityCode"]!
        )

        let request = Request<Token>(parameter: tokenParameters)
        client.send(request) { (result) in
            
            switch result {
                case .success(let token):
                    flutterResult(["id":token.id])
                    
                case .failure(let error):
                    flutterResult(FlutterError(code: "UNAVAILABLE", message: error.localizedDescription, details: nil))
                
            }
            
        }
        
    }
    
}
