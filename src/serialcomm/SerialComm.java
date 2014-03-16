package serialcomm;

import processing.serial.*;
import processing.core.*;

import java.util.*;

public class SerialComm extends Serial{
	
	static final byte INICIAR = 0;			//< Caracter de inicio de comunicacion.
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
	ArrayList<byte[]> trama;
	PApplet parent;
	
	public SerialComm(PApplet p, String port, int baudrate){
		super(p, port, baudrate);
		parent = p;
	}
	
	public void send_data(String data){
		this.write(INICIAR);		//< Byte de inicio.
		this.write(data);			//< Data a enviar. Esto debe incluir correction code, data code, data.
		this.write(FIN);			//< Byte de fin.
	}

	public boolean read_lastdata(){										//< Polling for data. Solo guarda la ultima trama valida.
		if(this.available() <= 0) return false;
		byte[] a = this.readBytes();
		this.buffer = get_data(a);
		if (this.buffer != null) {
			PApplet.println(buffer);
			return true;
		}
		return false;
	}
	
	public boolean read_alldata(){										//< Poll for new data. Crea lista con tramas validas recibidas.
		if(this.available() <= 0) return false;
		byte[] b = this.readBytes();
		this.trama = split_data(b);
		if(this.trama != null) return true;
		return false;
	}
	
	
	private byte[] get_data(byte[] t) { 								//< Esta funcion solo retorna la ultima trama valida recibida.
		int i = t.length - 1, j;
		
		if (t[i] != FIN) {
			for (--i; i >= 0 && t[i] != FIN; i--);					//< Busca por el caracter FIN en lo recibido hasta el momento.
		}
		if (i < 0) return null;										//< Si no lo consigue retorna null
		
		for (j = i ; j >= 1; j--)
			if(t[j] == INICIAR && t[j - 1] == FIN) break;			//< Busca el inicio del bloque de datos.
		if(j == 0)	return null;									//< Si j = 0 entonces no encontro el inicio de una trama valida.
		byte[] x = new byte[i - j - 1];								//< Nuevo array del tama;o de los datos importantes.
		j++;
		for(i = 0; i < x.length; i++)
			x[i] = t[j++];
		return x;													//< Retorna el array que sera el nuevo buffer.
	}
	
	private ArrayList<byte[]> split_data(byte[] t){
		int i = 0, j;
		
		if(t == null) return null;												//< Retorna nulo si t es nulo.
		if(t.length == 4) return null;											//< Si t no contiene el tama;o min de una trama retorna null.
		
		ArrayList<byte[]> trama = new ArrayList<byte[]>();
		
		while(i < t.length){
			
			if(t[i] != INICIAR) {
				i++;
				while(i < t.length && t[i] != INICIAR)  i++; 					//< Busca el inicio de alguna trama.
			}
			j = i;
			if(i == t.length) break;
			
			while(j < t.length && t[j] != FIN) j++;								//< Busca el fin de la trama.
			if(j == t.length) break;
			
			// Al llegar aqui se tiene una trama valida de tamano j-i+1
			
			byte[] new_trama = new byte[j-i+1];
			for(int k = i; k < j-i+1; k++) new_trama[k-i] = t[k];				//< Se copia la trama en el nuevo arreglo.
			trama.add(new_trama);												//< Se agrega nueva trama a la lista.
			
			i = j + 1;
		}
		if(trama.isEmpty()) trama = null;										//< Si no se agrego ninguna trama valida a la lista, se retorna null
		return trama;
	}	
}
