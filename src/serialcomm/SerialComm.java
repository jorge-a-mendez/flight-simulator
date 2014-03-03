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
	
	public SerialComm(PApplet p, String port, int baudrate){
		super(p, port, baudrate);
		this.bufferUntil(FIN);
	}
	
	void send_data(String data){
		this.write(INICIAR);		//< Byte de inicio.
		this.write(data);			//< Data a enviar. Esto debe incluir correction code, data code, data.
		this.write(FIN);			//< Byte de fin.
	}

	void read_data(){			//< Polling for data. Solo guarda la ultima trama valida.
		if(this.available() <= 0) return;
		byte[] a = this.readBytes();
		this.buffer = get_data(a);
	}
	
	public byte[] get_data(byte[] t) {
		int i = t.length - 1, j;
		
		if (t[i] != FIN) {
			for (--i; i >= 0 && t[i] != FIN; i--);	//< Busca por el caracter FIN en lo recibido hasta el momento.
		}
		if (i < 0) return null;						//< Si no lo consigue retorna null
		
		for (j = i ; j >= 1; j--)
			if(t[j] == INICIAR && t[j - 1] == FIN) break;			//< Busca el inicio del bloque de datos.
		if(j == 0)	return null;									//< Si j = 0 entonces no encontro el inicio de una trama valida.
		byte[] x = new byte[i - j - 1];								//< Nuevo array del tama;o de los datos importantes.
		j++;
		for(i = 0; i < x.length; i++)
			x[i] = t[j++];
		return x;													//< Retorna el array que sera el nuevo buffer.
	}
	
	// Rutina realizada cuando el buffer recibe el caracter de FIN.
	public void serialEvent(Serial port){
		try{
			byte[] t = port.readBytes();
			PApplet.println(t);
			buffer = get_data(t);
		}catch(Exception e){
			PApplet.println(e);
		}
	}
	
}
