package io.mobileacademy.omise

import android.content.Intent
import co.omise.android.api.Client
import co.omise.android.api.RequestListener
import co.omise.android.models.CardParam
import co.omise.android.models.Token
import co.omise.android.ui.CreditCardActivity
import co.omise.android.ui.OmiseActivity
import co.omise.android.ui.OmiseActivity.Companion.EXTRA_TOKEN_OBJECT
import io.flutter.embedding.android.FlutterActivity
import io.flutter.embedding.engine.FlutterEngine
import io.flutter.plugin.common.MethodChannel

class MainActivity: FlutterActivity() {
    private val channelName = "io.mobileacademy/omise"
    private  var token: Token? = null
    private val OMISE_PKEY: String = "pkey_test_4xiihu5wbve7eff9s6l"
    private val REQUEST_CC: Int = 100
    private val client = Client("pkey_test_4xiihu5wbve7eff9s6l")

    override fun configureFlutterEngine(flutterEngine: FlutterEngine) {
        super.configureFlutterEngine(flutterEngine)
        MethodChannel(flutterEngine.dartExecutor.binaryMessenger,channelName)
            .setMethodCallHandler { call, result ->
                if(call.method == "showCreditCardForm") {
                    val arg = call.arguments<Map<String,String>>() as Map<String, String>
                    // Sample builder for credit card
                    val cardParam = arg["expirationMonth"]?.let {expirationMonth->
                        arg["expirationYear"]?.let { expirationYear ->
                            CardParam(
                                name = arg["name"].toString(),
                                number = arg["number"].toString(),
                                expirationMonth =  expirationMonth.toInt(),
                                expirationYear = expirationYear.toInt(),
                                securityCode = arg["number"].toString(),
                            )
                        }
                    }
                    val request = Token.CreateTokenRequestBuilder(cardParam).build()
                    client.send(request, object : RequestListener<Token> {
                        override fun onRequestSucceed(model: Token) {
                            val data = hashMapOf<String, Any>()
                            data["id"] = model.id.toString()
                            data["used"] = model.used

                            result.success(data)

                        }

                        override fun onRequestFailed(throwable: Throwable) {
                            result.error(
                                "400",
                                throwable.localizedMessage?.toString(),
                                throwable.stackTrace
                            )
                        }

                    })
                }

        }

    }

    private fun showCreditCardForm() {
        val intent = Intent(this, CreditCardActivity::class.java)
        intent.putExtra(OmiseActivity.EXTRA_PKEY, OMISE_PKEY)
        startActivityForResult(intent, REQUEST_CC)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_CANCELED) {
            // handle the cancellation
            return
        }

        if (requestCode == REQUEST_CC) {
            token = data?.getParcelableExtra<Token>(EXTRA_TOKEN_OBJECT)
            // process your token here
        }
    }
}
