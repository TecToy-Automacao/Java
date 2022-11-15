package br.com.tectoy.tectoysunmi.activity

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.nfc.NdefMessage
import android.nfc.NdefRecord
import android.nfc.NfcAdapter
import android.nfc.Tag
import android.os.Bundle
import android.os.Parcelable
import android.util.Log
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import br.com.tectoy.tectoysunmi.R
import br.com.tectoy.tectoysunmi.databinding.ActivityNfcBinding
import java.util.*
import kotlin.experimental.and

class NfcActivity: BaseActivity() {
    var ERROR_DETECTED: String = "No NFC tag detected!"
    var WRITE_SUCCESS: String = "Text written to the NFC tag successfully!"
    var WRITE_ERROR: String = "Error during writing, is the NFC tag close enough to your device?"
    lateinit var nfcAdapter: NfcAdapter
    lateinit var pendingIntent: PendingIntent
    lateinit var writeTagFilters: IntentFilter
    var writeMode: Boolean = false
    lateinit var myTag: Tag
    lateinit var context: Context

    lateinit var tvNFCContent: TextView
    lateinit var message: TextView
    lateinit var btnWrite: Button

    private lateinit var binding: ActivityNfcBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNfcBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setMyTitle("NFC")
        setBack()

        context = this

        tvNFCContent = findViewById<TextView>(R.id.txets)
        message = findViewById<TextView>(R.id.edit_message)
        btnWrite = findViewById<Button>(R.id.button)

        nfcAdapter = NfcAdapter.getDefaultAdapter(this)

        if (nfcAdapter ==  null){
            Toast.makeText(this, "NFC is not available", Toast.LENGTH_LONG).show()
            finish()
            return
        }
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        val action: String = intent?.action.toString()
        val tag: Tag? = intent?.getParcelableExtra<Tag>(NfcAdapter.EXTRA_TAG)

        var s: String = action + "n\n" + tag.toString()

        val data = arrayOf<Parcelable?>(intent?.getParcelableExtra<Parcelable>(NfcAdapter.EXTRA_NDEF_MESSAGES))

        if (data != null){
            try{
                for (i in 0.. data.toString().length){
                    val recs = (data[i] as NdefMessage).records
                    for (j in 0..recs.toString().length){
                        if (recs[j].tnf == NdefRecord.TNF_WELL_KNOWN
                            && Arrays.equals(recs[j].type,
                            NdefRecord.RTD_TEXT)){

                            val payload = recs[j].payload
                            val textEncoding = if (payload[0] and 128.toByte() == 0.toByte()) "UTF-8" else "UTF-16"
                            val langCodeLen = (payload[0] and 63.toByte()).toInt()


                            s +=("\n\nMensagemNFC  - "
                                    + j
                                    + "-:\n\""
                                    + String(payload, langCodeLen + 1,
                                    payload.toString().length - langCodeLen - 1) + "\"")
                        }
                    }
                }
            } catch (e: Exception){
                Log.e("TagDispatch", e.toString())
            }
        }
        tvNFCContent.text = s
    }

}