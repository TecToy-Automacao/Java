package br.com.itfast.example.v2propassagem

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import br.com.itfast.tectoy.Dispositivo
import br.com.itfast.tectoy.TecToy
import br.com.itfast.tectoy.TecToyScannerCallback
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*
import java.util.regex.Pattern


class MainActivity2 : AppCompatActivity() {

    lateinit var tectoy : TecToy
    var Origem: String = ""
    var Destino: String = ""
    var Data: String = ""
    var Horario: String = ""
    var NomeDigitado: String = ""
    var Lugar: String = ""

    var reinicImp = "" + 0x1B.toChar() + 0x40.toChar() //Reinicia impressora
    var cmdCanModoChines = "" + 0x1C.toChar() + 0x2E.toChar() //Desativa o modo chines
    var cfgCodePage = "" + 0x1B.toChar() + 0x52.toChar() + 0x0C.toChar()+"" + 0x1B.toChar() + 0x74.toChar() + 0x02.toChar()
    var centro = "" + 0x1B.toChar() + 0x61.toChar() + 0x31.toChar() //Ativa alinhamento centro
    var deslCentro = "" + 0x1B.toChar() + 0x61.toChar() + 0x30.toChar() //Desativa alinhamento centro
    var negrito = "" + 0x1B.toChar() + 0x45.toChar() + 0x31.toChar() //Ativa Negrito
    var deslNegrito = "" + 0x1B.toChar() + 0x45.toChar() + 0x30.toChar() //Desativa Negrito
    var Passagem: String = ""
    var strTela: String = " "
    var strMostraBilhete: String = ""
    var strTxtLido: String = " "
    lateinit var txtCodigoLido: EditText
    lateinit var plntxtNomePassageiro: EditText
    lateinit var txtInsiraNome: TextView
    lateinit var spnHorario: Spinner
    lateinit var spnOrigem: Spinner
    lateinit var spnDestino: Spinner
    lateinit var spnData: Spinner
    lateinit var btnImprimir: Button
    lateinit var btnVoltar: Button

    lateinit var imgOnibus: ImageView
    lateinit var btnLugar1: Button
    lateinit var btnLugar3: Button
    lateinit var btnLugar9: Button
    lateinit var btnLugar11: Button
    lateinit var btnLugar17: Button
    lateinit var btnLugar19: Button
    lateinit var btnLugar25: Button
    lateinit var btnLugar27: Button
    lateinit var btnConfirmar: Button
    lateinit var btnLerCodBarras: Button

    var callbackCodBarras = TecToyScannerCallback { strBuffer: String ->
        strTxtLido = strBuffer
        Log.i("TECTOY", strBuffer)
        mostrarBilhete()
    }

    fun ocultarOnibus(){
        imgOnibus.visibility = View.INVISIBLE
        btnLugar1.visibility = View.INVISIBLE
        btnLugar3.visibility = View.INVISIBLE
        btnLugar9.visibility = View.INVISIBLE
        btnLugar11.visibility = View.INVISIBLE
        btnLugar17.visibility = View.INVISIBLE
        btnLugar19.visibility = View.INVISIBLE
        btnLugar25.visibility = View.INVISIBLE
        btnLugar27.visibility = View.INVISIBLE
    }

    fun mostrarOnibus() {
        imgOnibus.visibility = View.VISIBLE
        btnLugar1.visibility = View.VISIBLE
        btnLugar3.visibility = View.VISIBLE
        btnLugar9.visibility = View.VISIBLE
        btnLugar11.visibility = View.VISIBLE
        btnLugar17.visibility = View.VISIBLE
        btnLugar19.visibility = View.VISIBLE
        btnLugar25.visibility = View.VISIBLE
        btnLugar27.visibility = View.VISIBLE
        btnLerCodBarras.visibility = View.INVISIBLE
        btnConfirmar.visibility = View.INVISIBLE
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main2)

        btnLerCodBarras = findViewById(R.id.btnIniciaScan)
        plntxtNomePassageiro = findViewById(R.id.plntxtNomePassageiro)
        txtInsiraNome = findViewById(R.id.txtInsiraNome)
        spnHorario = findViewById(R.id.spnHorario)
        spnOrigem = findViewById(R.id.spnOrigem)
        spnDestino = findViewById(R.id.spnDestino)
        spnData = findViewById(R.id.spnData)
        btnImprimir = findViewById(R.id.btnImprimir)
        btnVoltar = findViewById(R.id.btnVoltar)

        imgOnibus = findViewById(R.id.imgOnibus)
        btnLugar1 = findViewById(R.id.btnLugar1)
        btnLugar3 = findViewById(R.id.btnLugar3)
        btnLugar9 = findViewById(R.id.btnLugar9)
        btnLugar11 = findViewById(R.id.btnLugar11)
        btnLugar17 = findViewById(R.id.btnLugar17)
        btnLugar19 = findViewById(R.id.btnLugar19)
        btnLugar25 = findViewById(R.id.btnLugar25)
        btnLugar27 = findViewById(R.id.btnLugar27)
        btnConfirmar = findViewById(R.id.btnConfirmar)
        txtCodigoLido = findViewById(R.id.txtCodigoLido)
      //  txtCodigoLido.movementMethod = ScrollingMovementMethod()

        val Horarios = arrayListOf<String>("6:00","9:00","12:00","15:00","23:00")
        val Origens = arrayListOf<String>("SP","SJC","COTIA")
        val Destinos = arrayListOf<String>("SP","SJC","COTIA","MANAUS")
        val Datas = arrayListOf<String>("01/08/2023","05/08/2023","10/08/2023","15/08/2023")
        val dateFormat: DateFormat
        val date: Date
        val  arrayAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, Horarios)
        spnHorario.adapter = arrayAdapter
        spnHorario.onItemSelectedListener = object :
        AdapterView.OnItemClickListener, AdapterView.OnItemSelectedListener {
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                if(spnHorario.selectedItem == "6:00"){
                    Horario = "6:00"
                }else if(spnHorario.selectedItem == "9:00"){
                    Horario = "9:00"
                }else if(spnHorario.selectedItem == "12:00"){
                    Horario = "12:00"
                }else if(spnHorario.selectedItem == "15:00"){
                    Horario = "15:00"
                }else if(spnHorario.selectedItem == "23:00"){
                    Horario = "23:00"
                }
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {
            }

            override fun onItemClick(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
            }

        }

        val  arrayAdapter2 = ArrayAdapter(this, android.R.layout.simple_spinner_item, Origens)
        spnOrigem.adapter = arrayAdapter2
        spnOrigem.onItemSelectedListener = object :
            AdapterView.OnItemClickListener, AdapterView.OnItemSelectedListener {

            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                if (spnOrigem.selectedItem == "SP"){
                    Origem = "SP"
                }else if (spnOrigem.selectedItem == "SJC"){
                    Origem = "SJC"
                }else if (spnOrigem.selectedItem == "COTIA"){
                    Origem = "COTIA"
                }
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {
            }

            override fun onItemClick(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
            }

        }
        val  arrayAdapter3 = ArrayAdapter(this, android.R.layout.simple_spinner_item, Destinos)
        spnDestino.adapter = arrayAdapter3
        spnDestino.onItemSelectedListener = object :
            AdapterView.OnItemClickListener, AdapterView.OnItemSelectedListener {

            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                if (spnDestino.selectedItem == "SP"){
                    Destino = "SP"
                }else if (spnDestino.selectedItem == "SJC"){
                    Destino = "SJC"
                }else if (spnDestino.selectedItem == "COTIA"){
                    Destino = "COTIA"
                }else if (spnDestino.selectedItem == "MANAUS"){
                    Destino = "MANAUS"
                }
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {
            }

            override fun onItemClick(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
            }

        }
        val  arrayAdapter4 = ArrayAdapter(this, android.R.layout.simple_spinner_item, Datas)
        spnData.adapter = arrayAdapter4
        spnData.onItemSelectedListener = object :
            AdapterView.OnItemClickListener, AdapterView.OnItemSelectedListener {

            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                if (spnData.selectedItem == "01/08/2023"){
                    Data = "01/08/2023"
                }else if (spnData.selectedItem == "05/08/2023"){
                    Data = "05/08/2023"
                }else if (spnData.selectedItem == "10/08/2023"){
                    Data = "10/08/2023"
                }else if (spnData.selectedItem == "15/08/2023"){
                    Data = "15/08/2023"
                }

            }

            override fun onNothingSelected(p0: AdapterView<*>?) {
            }

            override fun onItemClick(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
            }

        }

        tectoy = TecToy(Dispositivo.V2_PRO, this)

       btnLerCodBarras.setOnClickListener {
           tectoy.iniciarScanner(callbackCodBarras)
           txtCodigoLido.text.clear()
           txtCodigoLido.visibility = View.VISIBLE
           btnVoltar.visibility = View.VISIBLE
           btnLerCodBarras.visibility= View.INVISIBLE
           spnOrigem.visibility = View.INVISIBLE
           spnDestino.visibility = View.INVISIBLE
           spnData.visibility = View.INVISIBLE
           spnHorario.visibility = View.INVISIBLE
           btnImprimir.visibility =  View.INVISIBLE

       }

        btnVoltar.setOnClickListener {
            txtCodigoLido.visibility = View.INVISIBLE
            btnVoltar.visibility = View.INVISIBLE
            btnLerCodBarras.visibility= View.VISIBLE
            spnOrigem.visibility = View.VISIBLE
            spnDestino.visibility = View.VISIBLE
            spnData.visibility = View.VISIBLE
            spnHorario.visibility = View.VISIBLE
            txtCodigoLido.text.clear()
            btnConfirmar.visibility = View.VISIBLE
            strTxtLido = ""
            strMostraBilhete = ""
            strTela = ""
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
       }

        btnConfirmar.setOnClickListener {
            Passagem = reinicImp+ cmdCanModoChines+ cfgCodePage+ "TecToy Automacao  V2Pro\nSaiba mais em: \nwww.tectoyautomacao.com.br\n"+
            "--------------------------------\n       Demo Venda Passagem\n--------------------------------\n"
            mostrarOnibus()

        }

         btnLugar1.setOnClickListener {
                Lugar = "1"
                ocultarOnibus()

                btnImprimir.visibility = View.VISIBLE
                txtInsiraNome.visibility = View.VISIBLE
                plntxtNomePassageiro.visibility = View.VISIBLE

          }

         btnLugar3.setOnClickListener {

                Lugar = "3"
                ocultarOnibus()

                btnImprimir.visibility = View.VISIBLE
                txtInsiraNome.visibility = View.VISIBLE
                plntxtNomePassageiro.visibility = View.VISIBLE

         }

         btnLugar9.setOnClickListener {

                Lugar = "9"
                ocultarOnibus()

                btnImprimir.visibility = View.VISIBLE
                txtInsiraNome.visibility = View.VISIBLE
                plntxtNomePassageiro.visibility = View.VISIBLE

         }

         btnLugar11.setOnClickListener {

                Lugar = "11"
                ocultarOnibus()

                btnImprimir.visibility = View.VISIBLE
                txtInsiraNome.visibility = View.VISIBLE
                plntxtNomePassageiro.visibility = View.VISIBLE


         }

         btnLugar17.setOnClickListener {

                Lugar = "17"
               ocultarOnibus()

                btnImprimir.visibility = View.VISIBLE
                txtInsiraNome.visibility = View.VISIBLE
                plntxtNomePassageiro.visibility = View.VISIBLE


         }

         btnLugar19.setOnClickListener {

                Lugar = "19"
               ocultarOnibus()

                btnImprimir.visibility = View.VISIBLE
                txtInsiraNome.visibility = View.VISIBLE
                plntxtNomePassageiro.visibility = View.VISIBLE


         }

         btnLugar25.setOnClickListener {

                Lugar = "25"
              ocultarOnibus()

                btnImprimir.visibility = View.VISIBLE
                txtInsiraNome.visibility = View.VISIBLE
                plntxtNomePassageiro.visibility = View.VISIBLE

         }

         btnLugar27.setOnClickListener {

                Lugar = "27"
                ocultarOnibus()

                btnImprimir.visibility = View.VISIBLE
                txtInsiraNome.visibility = View.VISIBLE
                plntxtNomePassageiro.visibility = View.VISIBLE


                }

         btnImprimir.setOnClickListener {
            val dateFormat: DateFormat = SimpleDateFormat("dd/MM/yyyy HH:mm")
            val date = Date()
            if (plntxtNomePassageiro.text.toString().length != 0) {
                NomeDigitado = plntxtNomePassageiro.text.toString()
                txtInsiraNome.visibility = View.INVISIBLE
                plntxtNomePassageiro.visibility = View.INVISIBLE
                plntxtNomePassageiro.text.clear();
                var dataHora: String = dateFormat.format(date)

                strTela = Origem + "|" + Destino + "|" + Data + "|" + Horario + "|" +
                        Lugar + "|" + NomeDigitado + "|" + dataHora

                Passagem += centro+"Origem: " + Origem + "  " + "Destino: " + Destino+
                        "\nData: " + Data + " | " + Horario + "\n" +
                        negrito+"(Poltrona: " + Lugar + deslNegrito+ " Plataforma 1)\n" + deslCentro+
                        "--------------------------------\n" +
                        "Tarifa:                   130.00\n" +
                        "Valor a Pagar:            130.00\n" +
                        "\nFORMA DE PAGAMENTO VALOR PAGO R$\n" +
                        "Dinheiro                  150.00\n" +
                        "Troco                      20.00\n\n" +
                        centro + "Saiba mais sobre nossos produtos\n" +
                        negrito + "www.tectoyautomacao.com.br" + deslNegrito +
                        "\n--------------------------------\n"  +
                        negrito + "Passageiro: " + deslNegrito + "DOC 111222333x\n" + negrito+ NomeDigitado + deslNegrito+
                        "\nData/ Hora Impressao: " + dataHora

                tectoy.imprimir(Passagem +"\n\n")
                tectoy.imprimirQrCode(strTela, "Q", 4)
                tectoy.imprimir("\n\n\n\n")
                btnImprimir.visibility = View.INVISIBLE
                btnConfirmar.visibility = View.VISIBLE
                btnLerCodBarras.visibility = View.VISIBLE
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
            }else{
                Toast.makeText(this, "Preencha o nome do passageiro.", Toast.LENGTH_SHORT).show()
                btnConfirmar.visibility = View.INVISIBLE
                btnLerCodBarras.visibility = View.INVISIBLE
            }

        }


    }

    fun mostrarBilhete(){
        strMostraBilhete = "TecToy Automação  V2Pro\n" +
                "Saiba mais em: \n"+
                "www.tectoyautomacao.com.br\n"+
                "--------------------------------\n"+
                "       Demo  Venda Passagem\n"+
                "--------------------------------\n"
        if (strTxtLido.isNotEmpty() or (strTxtLido.indexOf("\\|")!=-1)){
            val arr = Pattern.compile("\\|").split(strTxtLido)
            // logs para conferencia em debug apenas
            Log.i("TECTOY", strTxtLido)
            Log.i("TECTOY", arr.contentToString())

            strMostraBilhete+=  "--------------------------------\n" +"Origem:" + arr[0].toString() + "  " + "Destino: " + arr[1].toString() +
                    "\nData: " + arr[2].toString()+ " |" + arr[3].toString() + "\n" +
                    "(Poltrona: " + arr[4].toString() + " Plataforma 1)\n" +
                    "--------------------------------\n" +
                    "Tarifa:                   130.00\n" +
                    "Valor a Pagar:            130.00\n" +
                    "\nFORMA DE PAGAMENTO VALOR PAGO R$\n" +
                    "Dinheiro                  150.00\n" +
                    "Troco                      20.00\n\n" +
                    "Saiba mais sobre nossos produtos\n" +
                    "   www.tectoyautomacao.com.br\n" +
                    "--------------------------------\n" +
                    "Passageiro: " + "DOC 11222333x\n" + arr[5].toString() +
                    "\nData/ Hora Impressao: \n         " + arr[6].toString()

            Log.i("TESTE",strTxtLido)
            Log.i("TESTE",strMostraBilhete)
            if (strMostraBilhete.isNotEmpty()){txtCodigoLido.setText(strMostraBilhete)}
            else{ Toast.makeText(this, "Erro ao ler conteudo", Toast.LENGTH_SHORT).show()}
        }else{ Toast.makeText(this, "Código inválido, tente ler o QrCode de um Bilhete válido.", Toast.LENGTH_SHORT).show()}


    }
}