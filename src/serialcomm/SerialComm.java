package serialcomm;

import processing.serial.*;
import processing.core.*;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class SerialComm extends Serial{
	
	static final byte INICIAR = 0;			//< Caracter de inicio de comunicacion.
	static final byte FIN = (byte) 0xFF;	//< Caracter de fin de comunicacion.
	static final int[] LENGTHS = {5, 5, 5, 7, 7, 4};
	
	
	//Correction codes...
	static final byte NO_CORRECTION = 1;
	
	//Atributos...
	
	byte[] buffer;
	private BlockingQueue<byte[]> trama;
	PApplet parent;
	
	public SerialComm(PApplet p, String port, int baudrate){
		super(p, port, baudrate);
		parent = p;
		this.bufferUntil(FIN);
		trama = new LinkedBlockingQueue<byte[]>();
		//this.clear();
	}
	
	public void send_data(String data) {
		this.write(INICIAR);											//< Byte de inicio.
		this.write(data);												//< Data a enviar. Esto debe incluir correction code, data code, data.
		this.write(FIN);												//< Byte de fin.
	}
	
	public void send_data(byte[] data) {
		this.write(INICIAR);											//< Byte de inicio.
		this.write(data);												//< Data a enviar. Esto debe incluir correction code, data code, data.
		this.write(FIN);												//< Byte de fin.
	}
	
	public boolean read_lastdata() {										//< Polling for data. Solo guarda la ultima trama valida.
		if(this.available() <= 0) return false;
		byte[] a = this.readBytes();
		this.buffer = get_data(a);
		if (this.buffer != null) {
			return true;
		}
		return false;
	}
	
	public void read_alldata() {										//< Poll for new data. Crea lista con tramas validas recibidas.
		if(this.available() <= 0) return;
		byte[] b = this.readBytes();
		//PApplet.println(b);
		break_data(b);
		//split_data(b);
		//b = get_data(b);
		try {
			trama.put(b);											//< Se agrega nueva trama a la lista.
		} catch (Exception e) {
			PApplet.println(e);
		}
	}	
	
	private byte[] get_data(byte[] t) { 								//< Esta funcion solo retorna la ultima trama valida recibida.
		int i = t.length - 1, j;
		
		if (t[i] != FIN) {
			for (--i; i >= 0 && t[i] != FIN; i--);					//< Busca por el caracter FIN en lo recibido hasta el momento.
		}
		if (i < 0) return null;										//< Si no lo consigue retorna null
		
		for (j = i ; j >= 1; j--)
			if(t[j] == INICIAR && t[j - 1] == FIN) 
				break;												//< Busca el inicio del bloque de datos.
		//if(j == 0)	return null;									//< Si j = 0 entonces no encontro el inicio de una trama valida.
		byte[] x = new byte[i - j + 1];								//< Nuevo array del tama;o de los datos importantes.
		//j++;
		for(i = 0; i < x.length; i++)
			x[i] = t[j++];
		return x;													//< Retorna el array que sera el nuevo buffer.
	}
	
	private void split_data(byte[] t) {
		int i = 0, j;
		
		if(t == null) return;														//< Retorna nulo si t es nulo.
		if(t.length <= 4) return;													//< Si t no contiene el tama;o min de una trama retorna null.
		
		
		while(i < t.length){
			
			if(t[i] != INICIAR) {
				i++;
				while(i < t.length && t[i] != INICIAR)  i++; 						//< Busca el inicio de alguna trama.
			}
			j = i;
			if(i == t.length) break;
			
			while(j < t.length && t[j] != FIN) j++;									//< Busca el fin de la trama.
			if(j == t.length) break;
			
			// Al llegar aqui se tiene una trama valida de tamano j-i+1
			if (j-i+1 > 4){
				byte[] new_trama = new byte[j-i+1];
			 
				for(int k = i; k <= j; k++) new_trama[k-i] = t[k];					//< Se copia la trama en el nuevo arreglo.
				try {
					trama.put(new_trama);											//< Se agrega nueva trama a la lista.
				} catch (Exception e) {
					PApplet.println(e);
				}
			}
			i = j + 1;
		}
	}
	
	//More efficient search of valid tramas...
	private void break_data(byte[] t) {
		int i = 0, j;
		
		if(t == null) return;														//< Retorna nulo si t es nulo.
		if(t.length <= 4) return;													//< Si t no contiene el tama;o min de una trama retorna null.
		//PApplet.println(t);
		
		while(i < t.length){
			
			if(t[i] != INICIAR) {
				i++;
				while(i < t.length && t[i] != INICIAR)  i++; 						//< Busca el inicio de alguna trama.
			}
			j = i;
			if(i == t.length - 1) break;
			/*if(t[j+1] != 2) {
				i++;
				continue;
			}*/
			if(t[i + 1] - 1 < LENGTHS.length){
				j = i + LENGTHS[t[i + 1] - 1];									//< Busca el fin de la trama.
			
				if(j >= t.length) break;
				if(t[j] == -1){
					// Al llegar aqui se tiene una trama valida
	
					byte[] new_trama = new byte[j-i+1];
				 
					for(int k = i; k <= j; k++) new_trama[k-i] = t[k];					//< Se copia la trama en el nuevo arreglo.
					try {
						//PApplet.println(new_trama);
						trama.put(new_trama);											//< Se agrega nueva trama a la lista.
					} catch (Exception e) {
						PApplet.println(e);
					}
	
				}
				i = j + 1;
			}
			else
				i++;
		}
	}
	
	public Iterable<byte[]> data() {
		return trama;
	}
	
	public void get_trama() {
		read_alldata();
	}
	
	public boolean data_available() {
		return !trama.isEmpty();
	}
	
	public byte[] get_next() throws InterruptedException {
		return trama.take();
	}
}
