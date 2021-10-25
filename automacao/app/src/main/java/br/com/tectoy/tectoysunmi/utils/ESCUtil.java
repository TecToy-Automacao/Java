package br.com.tectoy.tectoysunmi.utils;

import android.graphics.Bitmap;

import java.io.ByteArrayOutputStream;

//常用指令封装
public class ESCUtil {

	public static final byte ESC = 0x1B;// Escape
	public static final byte FS =  0x1C;// Text delimiter
	public static final byte GS =  0x1D;// Group separator
	public static final byte DLE = 0x10;// data link escape
	public static final byte EOT = 0x04;// End of transmission
	public static final byte ENQ = 0x05;// Enquiry character
	public static final byte SP =  0x20;// Spaces
	public static final byte HT =  0x09;// Horizontal list
	public static final byte LF =  0x0A;//Print and wrap (horizontal orientation)
	public static final byte CR =  0x0D;// Home key
	public static final byte FF =  0x0C;// Carriage control (print and return to the standard mode (in page mode))
	public static final byte CAN = 0x18;// Canceled (cancel print data in page mode)

	//Inicialize a impressora
	public static byte[] init_printer() {
		byte[] result = new byte[2];
		result[0] = ESC;
		result[1] = 0x40;
		return result;
	}

	//Comando de densidade de impressão
	public static byte[] setPrinterDarkness(int value){
		byte[] result = new byte[9];
		result[0] = GS;
		result[1] = 40;
		result[2] = 69;
		result[3] = 4;
		result[4] = 0;
		result[5] = 5;
		result[6] = 5;
		result[7] = (byte) (value >> 8);
		result[8] = (byte) value;
		return result;
	}

	/**
	 *
	 * Imprima um único código QR sunmiInstrução personalizada
	 * @param code:			二维码数据
	 * @param modulesize:	二维码块大小(单位:点, 取值 1 至 16 )
	 * @param errorlevel:	二维码纠错等级(0 至 3)
	 *                0 -- 纠错级别L ( 7%)
	 *                1 -- 纠错级别M (15%)
	 *                2 -- 纠错级别Q (25%)
	 *                3 -- 纠错级别H (30%)
	 */
	public static byte[] getPrintQRCode(String code, int modulesize, int errorlevel){
		ByteArrayOutputStream buffer = new ByteArrayOutputStream();
		try{
			buffer.write(setQRCodeSize(modulesize));
			buffer.write(setQRCodeErrorLevel(errorlevel));
			buffer.write(getQCodeBytes(code));
			buffer.write(getBytesForPrintQRCode(true));
		}catch(Exception e){
			e.printStackTrace();
		}
		return buffer.toByteArray();
	}

	/**
	 * Dois códigos QR horizontalmente sunmi Instrução personalizada
	 * @param code1:        Dados de código QR
	 * @param code2:		Dados de código QR
	 * @param modulesize:	Tamanho do bloco de código bidimensional (unidade: ponto, valor 1 a 16)
	 * @param errorlevel:   Nível de correção de erro de código QR (0 a 3)
	 *                0 -- Nível L de correção de erros ( 7%)
	 *                1 -- Nível M de correção de erros (15%)
	 *                2 -- Nível de correção de erro Q (25%)
	 *                3 -- Nível H de correção de erros (30%)
	 */
	public static byte[] getPrintDoubleQRCode(String code1, String code2, int modulesize, int errorlevel){
		ByteArrayOutputStream buffer = new ByteArrayOutputStream();
		try{
			buffer.write(setQRCodeSize(modulesize));
			buffer.write(setQRCodeErrorLevel(errorlevel));
			buffer.write(getQCodeBytes(code1));
			buffer.write(getBytesForPrintQRCode(false));
			buffer.write(getQCodeBytes(code2));

			//加入横向间隔
			buffer.write(new byte[]{0x1B, 0x5C, 0x18, 0x00});

			buffer.write(getBytesForPrintQRCode(true));
		}catch(Exception e){
			e.printStackTrace();
		}
		return buffer.toByteArray();
	}

	/**
	 *
	 * Impressão de código QR
	 */
	public static byte[] getPrintQRCode2(String data, int size){
		byte[] bytes1  = new byte[4];
		bytes1[0] = GS;
		bytes1[1] = 0x76;
		bytes1[2] = 0x30;
		bytes1[3] = 0x00;

		byte[] bytes2 = BytesUtil.getZXingQRCode(data, size);
		return BytesUtil.byteMerger(bytes1, bytes2);
	}

	/**
	 *
	 * Imprimir código de barras 1D
	 */
	public static byte[] getPrintBarCode(String data, int symbology, int height, int width, int textposition){
		if(symbology < 0 || symbology > 10){
			return new byte[]{LF};
		}

		if(width < 2 || width > 6){
			width = 2;
		}

		if(textposition <0 || textposition > 3){
			textposition = 0;
		}

		if(height < 1 || height>255){
			height = 162;
		}

		ByteArrayOutputStream buffer = new ByteArrayOutputStream();
		try{
			buffer.write(new byte[]{0x1D,0x66,0x01,0x1D,0x48,(byte)textposition,
					0x1D,0x77,(byte)width,0x1D,0x68,(byte)height,0x0A});

			byte[] barcode;
			if(symbology == 10){
				barcode = BytesUtil.getBytesFromDecString(data);
			}else{
				barcode = data.getBytes("GB18030");
			}


			if(symbology > 7){
				buffer.write(new byte[]{0x1D,0x6B,0x49,(byte)(barcode.length+2),0x7B, (byte) (0x41+symbology-8)});
			}else{
				buffer.write(new byte[]{0x1D,0x6B,(byte)(symbology + 0x41),(byte)barcode.length});
			}
			buffer.write(barcode);
		}catch(Exception e){
			e.printStackTrace();
		}
		return buffer.toByteArray();
	}

	//
	//Impressão de bitmap
	public static byte[] printBitmap(Bitmap bitmap){
		byte[] bytes1  = new byte[4];
		bytes1[0] = GS;
		bytes1[1] = 0x76;
		bytes1[2] = 0x30;
		bytes1[3] = 0x00;

		byte[] bytes2 = BytesUtil.getBytesFromBitMap(bitmap);
		return BytesUtil.byteMerger(bytes1, bytes2);
	}

	//Configurações de impressão de bitmap mode
	public static byte[] printBitmap(Bitmap bitmap, int mode){
		byte[] bytes1  = new byte[4];
		bytes1[0] = GS;
		bytes1[1] = 0x76;
		bytes1[2] = 0x30;
		bytes1[3] = (byte) mode;

		byte[] bytes2 = BytesUtil.getBytesFromBitMap(bitmap);
		return BytesUtil.byteMerger(bytes1, bytes2);
	}

	//
	//Impressão de bitmap
	public static byte[] printBitmap(byte[] bytes){
		byte[] bytes1  = new byte[4];
		bytes1[0] = GS;
		bytes1[1] = 0x76;
		bytes1[2] = 0x30;
		bytes1[3] = 0x00;

		return BytesUtil.byteMerger(bytes1, bytes);
	}

	/*
	*	Selecione o comando de bitmap configuração mode
	*	Precisa definir 1B 33 00 Defina o espaçamento de linha para 0
	 */
	public static byte[] selectBitmap(Bitmap bitmap, int mode){
		return BytesUtil.byteMerger(BytesUtil.byteMerger(new byte[]{0x1B, 0x33, 0x00}, BytesUtil.getBytesFromBitMap(bitmap, mode)), new byte[]{0x0A, 0x1B, 0x32});
	}

	/**
	 *
	 * Pular o número especificado de linhas
	 */
    public static byte[] nextLine(int lineNum) {
        byte[] result = new byte[lineNum];
        for (int i = 0; i < lineNum; i++) {
            result[i] = LF;
        }

        return result;
    }

    // ------------------------style set-----------------------------
	//
	//Definir espaçamento de linha padrão
	public static byte[] setDefaultLineSpace(){
		return new byte[]{0x1B, 0x32};
	}

	//
	//Definir espaçamento de linha
	public static byte[] setLineSpace(int height){
    	return new byte[]{0x1B, 0x33, (byte) height};
	}

	// ------------------------underline-----------------------------
	//
	//Definir sublinhado 1 ponto
	public static byte[] underlineWithOneDotWidthOn() {
		byte[] result = new byte[3];
		result[0] = ESC;
		result[1] = 45;
		result[2] = 1;
		return result;
	}

	//
	// Definir sublinhado 2 pontos
	public static byte[] underlineWithTwoDotWidthOn() {
		byte[] result = new byte[3];
		result[0] = ESC;
		result[1] = 45;
		result[2] = 2;
		return result;
	}

	// Sublinhado Desligado
	public static byte[] underlineOff() {
		byte[] result = new byte[3];
		result[0] = ESC;
		result[1] = 45;
		result[2] = 0;
		return result;
	}

	// ------------------------bold-----------------------------
	/**
	 * Negrito Ligado
	 */
	public static byte[] boldOn() {
		byte[] result = new byte[3];
		result[0] = ESC;
		result[1] = 69;
		result[2] = 0xF;
		return result;
	}

	/**
	 *
	 * Negrito desligado
	 */
	public static byte[] boldOff() {
		byte[] result = new byte[3];
		result[0] = ESC;
		result[1] = 69;
		result[2] = 0;
		return result;
	}

	// ------------------------character-----------------------------
	/*
	*Modo de byte único ativado
	 */
	public static byte[] singleByte(){
		byte[] result = new byte[2];
		result[0] = FS;
		result[1] = 0x2E;
		return result;
	}

	/*
	* Modo de byte único desativado
 	*/
	public static byte[] singleByteOff(){
		byte[] result = new byte[2];
		result[0] = FS;
		result[1] = 0x26;
		return result;
	}

	/**
	 * Definir conjunto de caracteres de byte único
	 */
	public static byte[] setCodeSystemSingle(byte charset){
		byte[] result = new byte[3];
		result[0] = ESC;
		result[1] = 0x74;
		result[2] = charset;
		return result;
	}

	/**
	 * Definir conjunto de caracteres multibyte
	 */
	public static byte[] setCodeSystem(byte charset){
		byte[] result = new byte[3];
		result[0] = FS;
		result[1] = 0x43;
		result[2] = charset;
		return result;
	}

	// ------------------------Align-----------------------------

	/**
	 * Alinhamento a esquerda
	 */
	public static byte[] alignLeft() {
		byte[] result = new byte[3];
		result[0] = ESC;
		result[1] = 97;
		result[2] = 0;
		return result;
	}

	/**
	 * Alinhamento no centro
	 */
	public static byte[] alignCenter() {
		byte[] result = new byte[3];
		result[0] = ESC;
		result[1] = 97;
		result[2] = 1;
		return result;
	}

	/**
	 * Alinhamento a direita
	 */
	public static byte[] alignRight() {
		byte[] result = new byte[3];
		result[0] = ESC;
		result[1] = 97;
		result[2] = 2;
		return result;
	}

	// Cortar Papel
	public static byte[] cutter() {
		byte[] data = new byte[]{0x1d, 0x56, 0x01};
		return data;
	}

	//Papel para marca preta
	public static byte[] gogogo(){
		byte[] data = new byte[]{0x1C, 0x28, 0x4C, 0x02, 0x00, 0x42, 0x31};
		return data;
	}


	////////////////////////////////////////////////////////////////////////////////////
	//////////////////////////          private                /////////////////////////
	////////////////////////////////////////////////////////////////////////////////////
	private static byte[] setQRCodeSize(int modulesize){
		//二维码块大小设置指令
		byte[] dtmp = new byte[8];
		dtmp[0] = GS;
		dtmp[1] = 0x28;
		dtmp[2] = 0x6B;
		dtmp[3] = 0x03;
		dtmp[4] = 0x00;
		dtmp[5] = 0x31;
		dtmp[6] = 0x43;
		dtmp[7] = (byte)modulesize;
		return dtmp;
	}

	private static byte[] setQRCodeErrorLevel(int errorlevel){
		//二维码纠错等级设置指令
		byte[] dtmp = new byte[8];
		dtmp[0] = GS;
		dtmp[1] = 0x28;
		dtmp[2] = 0x6B;
		dtmp[3] = 0x03;
		dtmp[4] = 0x00;
		dtmp[5] = 0x31;
		dtmp[6] = 0x45;
		dtmp[7] = (byte)(48+errorlevel);
		return dtmp;
	}


	private static byte[] getBytesForPrintQRCode(boolean single){
		//打印已存入数据的二维码
		byte[] dtmp;
		if(single){		//同一行只打印一个QRCode， 后面加换行
			dtmp = new byte[9];
			dtmp[8] = 0x0A;
		}else{
			dtmp = new byte[8];
		}
		dtmp[0] = 0x1D;
		dtmp[1] = 0x28;
		dtmp[2] = 0x6B;
		dtmp[3] = 0x03;
		dtmp[4] = 0x00;
		dtmp[5] = 0x31;
		dtmp[6] = 0x51;
		dtmp[7] = 0x30;
		return dtmp;
	}

	private static byte[] getQCodeBytes(String code) {
		//二维码存入指令
		ByteArrayOutputStream buffer = new ByteArrayOutputStream();
		try {
			byte[] d = code.getBytes("GB18030");
			int len = d.length + 3;
			if (len > 7092) len = 7092;
			buffer.write((byte) 0x1D);
			buffer.write((byte) 0x28);
			buffer.write((byte) 0x6B);
			buffer.write((byte) len);
			buffer.write((byte) (len >> 8));
			buffer.write((byte) 0x31);
			buffer.write((byte) 0x50);
			buffer.write((byte) 0x30);
			for (int i = 0; i < d.length && i < len; i++) {
				buffer.write(d[i]);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return buffer.toByteArray();
	}
}