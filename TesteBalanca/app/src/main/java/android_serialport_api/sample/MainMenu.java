/*
 * Copyright 2009 Cedric Priscal
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License. 
 */

package android_serialport_api.sample;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;

import android_serialport_api.SerialPort;

public class MainMenu extends Activity {
	private OutputStream mOutputStream;
	private InputStream mInputStream;

	private String getValue(byte[] resp) {
		byte[] retArray = new byte[10];
		int iPos = 0;
		for(int i = 0; i< resp.length; i++) {
			if(resp[i] != 0x02 && resp[i] != 0x03) {
				if((byte)resp[i] == (byte)0x03) {
					break;
				}
				retArray[iPos] = resp[i];
				iPos++;

			}
		}
		return new String(retArray);
	}

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

		try {
			//Configura a porta e velocidade de comunicação com a balança.
			// A porta ttyS3 que está no exemplo é para o dispositivo D2s, para outros dispositivos consultar o link abaixo da Sunmi
			//https://developer.sunmi.com/docs/en-US/xeghjk491/cideghjk524
			SerialPort serialPort = new SerialPort(new File("/dev/ttyS3"), 9600, 0);//your serial port dev
			mOutputStream = serialPort.getOutputStream();
			mInputStream = serialPort.getInputStream();
		} catch (IOException e) {
			e.printStackTrace();
		}
		final TextView txtValue = (TextView) findViewById(R.id.txtValue);
		final Button buttonWrite = (Button)findViewById(R.id.ButtonWrite);
		buttonWrite.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				byte[] mBufferW = new byte[1];
				Arrays.fill(mBufferW, (byte) 0x05); //comando que deve ser enviado para que a balança retorne o peso.
				byte[] mBuffer = new byte[10];
				Arrays.fill(mBuffer, (byte) 0x00);
				try {
					mOutputStream.write(mBufferW); //escrita do comando
					mInputStream.read(mBuffer); //leitura da resposta da balança
					txtValue.setText(getValue(mBuffer));
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		});
    }
}
