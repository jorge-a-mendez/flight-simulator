package serialcomm;

import processing.serial.*;
import processing.core.*;

public class SerialComm extends Serial{
	
	static final byte INICIAR = 0;		//< Caracter de inicio de comunicacion.
	static final byte FIN = (byte) 0xFF;	//< Caracter de fin de comunicacion.
	
	// Data codes...
	static final byte PANELX = 1;
	static final byte PANELY = 2;
	static final byte PANELZ = 3;
	static final byte ACCEL_ANGLE = 4;	
	static final byte PIEZO = 5;
	
	//Correction codes...
	static final byte NO_CORRECTION = 1;
	
	//Atributos...
	
	byte[] buffer;
	
	public SerialComm(PApplet p, String port){
		super(p, port);
		bufferUntil(FIN);
	}
	
	void send_data(String data){
		this.write(INICIAR);		//< Byte de inicio.
		this.write(data);			//< Data a enviar. Esto debe incluir correction code, data code, data.
		this.write(FIN);			//< Byte de fin.
	}
	
	private byte[] get_data(byte[] t) {
		int i;
		
		for (i = t.length - 1; i >= 0; i--)
			if(t[i] == INICIAR) break;			//< Busca el inicio del bloque de datos.
		byte[] x = new byte[t.length - i - 1];	//< Nuevo array del tama;o de los datos importantes.
		i++;
		for(int j = 0; j < x.length; j++)
			x[j] = t[i++];
		return x;	//< Retorna el array que sera el nuevo buffer.
	}
	
	// Rutina realizada cuando el buffer recibe el caracter de FIN.
	void serialEvent(Serial port){
		byte[] t = port.readBytes();
		buffer = get_data(t);
	}
	
}
