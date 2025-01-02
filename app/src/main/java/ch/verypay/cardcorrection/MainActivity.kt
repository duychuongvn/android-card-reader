package ch.verypay.cardcorrection

import android.app.PendingIntent
import android.content.Intent
import android.nfc.NfcAdapter
import android.nfc.Tag
import android.nfc.tech.IsoDep
import android.os.Bundle
import android.widget.TextView
import androidx.activity.ComponentActivity
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import ch.verypay.cardcorrection.ui.theme.CardcorrectionTheme
import java.io.IOException
import java.net.URISyntaxException
import java.util.function.Consumer

class MainActivity : ComponentActivity() {
    private var nfcAdapter: NfcAdapter? = null
    private lateinit var textView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        textView = findViewById(R.id.textView)
        nfcAdapter = NfcAdapter.getDefaultAdapter(this)

        if (nfcAdapter == null) {
            textView.text = "NFC is not available on this device."
        }
    }

    override fun onResume() {
        super.onResume()
        val intent = Intent(this, javaClass).apply {
            addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)
        }
        val pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_MUTABLE)
        nfcAdapter?.enableForegroundDispatch(this, pendingIntent, null, arrayOf(arrayOf("android.nfc.tech.IsoDep")))
    }

    override fun onPause() {
        super.onPause()
        nfcAdapter?.disableForegroundDispatch(this)
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        if (intent.action == NfcAdapter.ACTION_TECH_DISCOVERED) {
            val tag = intent.getParcelableExtra<Tag>(NfcAdapter.EXTRA_TAG)
            connectToCard(tag)
        }
    }



    private fun connectToCard(tag: Tag?) {
        val isoDep = IsoDep.get(tag)
        if (isoDep != null) {
            try {
                isoDep.connect()
                textView.text = "Start..."

                val msgConsumer: Consumer<String?> =
                    Consumer<String?> { msg ->
                        runOnUiThread { textView.append("\n$msg") }
                     }
                val client = MyWebSocketClient(
                    isoDep,
                    msgConsumer
                )

                var cuid = ByteUtils.bytesToHexString(tag!!.id);
                var command = "CK" + cuid
                textView.append("\n Start sending $command")
                client.send("CK" + ByteUtils.bytesToHexString(tag!!.id)) // Send response from card to server
            } catch (e: IOException) {
                e.printStackTrace()
            } catch (e: URISyntaxException) {
                e.printStackTrace()
            }
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    CardcorrectionTheme {
        Greeting("Android")
    }
}