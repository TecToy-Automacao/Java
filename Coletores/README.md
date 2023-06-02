#Coletores
*Exemplo para Coletores da TECTOY L2s e L2ks, utilizando a biblioteca IT4R.aar*

- Para utilizar esse recurso o objeto TecToy deve ser inicializado com
 o dispositivo L2s ou L2Ks, conforme exemplo abaixo
 tecToy = new TecToy(Dispositivo.L2Ks, context);

- Para acionar o leitor e realizar a leitura chamar o método
 iniciarScanner passando como parâmetro o callback de recebimento da
 leitura. OBS.: O método deve ser chamado sempre dentro de uma thread.

 //implementação do callback, onde após receber o retorno para a  leitura do scanner e retorna o código lido em um text view
    private TecToyScannerCallback scannerCallback = new TecToyScannerCallback() {
         @Override
         public void retornarCodigo(String strCodigo) {
             tecToy.pararScanner();
             runOnUiThread(new Runnable() {
                 @Override
                 public void run() {
                     txtCodLido.setText(strCodigo);
                 }
             });
         }
     };


 //Runnable chamando o método de iniciar o scanner
  private Runnable iniciarScanner = new Runnable() {
         @Override
         public void run() {
             try {
                 Looper.prepare();
                 try {
                     tecToy.iniciarScanner(scannerCallback);
                 } catch (Exception e) {
                     Log.e("ERRO", e.getMessage() != null ?
             e.getMessage() : e.toString());
                 }

             } catch (DarumaException de) {
                 throw de;
             }
         }
     };

- Para trabalhar com leitura de NFC devem ser feitas as implementações e configuração abaixo
  - No arquivo manifest da aplicação adicionar permissão para NFC (<uses-permission android:name="android.permission.NFC" />)
  - Adicionar action NDEF_DISCOVERED
  
  
  Exemplo de arquivo AndroidManifest.xml com as alterações citadas acima
  <?xml version="1.0" encoding="utf-8"?>
<manifest xmlns:android="http://schemas.android.com/apk/res/android" xmlns:tools="http://schemas.android.com/tools">
    <uses-permission android:name="android.permission.NFC" />
    <application
        android:allowBackup="true"
        android:dataExtractionRules="@xml/data_extraction_rules"
        android:fullBackupContent="@xml/backup_rules"
        android:icon="@mipmap/ic_launcher"
        android:label="@string/app_name"
        android:roundIcon="@mipmap/ic_launcher_round"
        android:supportsRtl="true"
        android:theme="@style/Theme.Coletores"
        tools:targetApi="31">
        <activity
            android:name=".MainActivity"
            android:exported="true">
            <intent-filter>
                <action android:name="android.intent.action.MAIN" />
                <action android:name="android.nfc.action.NDEF_DISCOVERED" />
                <category android:name="android.intent.category.DEFAULT" />
                <category android:name="android.intent.category.LAUNCHER" />
            </intent-filter>
        </activity>
    </application>
</manifest>

- Implementar callback TecToyNfcCallback para receber a resposta da leitura NFC, exemplo abaixo

TecToyNfcCallback callbackNFC = new TecToyNfcCallback() {
        @Override
        public void retornarValor(String strValor) {

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    txtRetornoNFC.setText(strValor);
                }
            });
        }
    };

- no "onCreate" da activity criar o objeto pendingIntent e iniciar a leitura de NFC através do métod iniciarNFC(Intent intent, TecToyNfcCallback), exemplo abaixo
pendingIntent = PendingIntent.getActivity(this, 0, new Intent(this, getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);
tecToy.iniciarNFC(getIntent(), callbackNFC);

- Em "onNewIntent" da Activity adicionar chamada ao método onNewIntentNFC(Intent intent), exemplo abaixo
  tecToy.onNewIntentNFC(intent);

- Em "onPause" da Activity adicionar chamada ao método onPauseNFC(Activity activity), exemplo abaixo
  tecToy.onPauseNFC(this);

- Em "onResumeNFC" da Activity adicionar chamada ao método onNewIntentNFC(Activity activity, PendingIntent pendingIntent), exemplo abaixo
  tecToy.onResumeNFC(this, pendingIntent);

- Para fazer a escrita no NFC utilizar o método escreverNFC(String strValor), exemplo abaixo
tecToy.escreverNFC(txtGravarNFC.getText().toString());
