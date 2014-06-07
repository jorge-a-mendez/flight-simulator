package serialcomm;
import processing.core.*;
public class AcceInterface extends PApplet{
	
	
	static final byte ACCEL_ANGLEXZ = 4;	
	static final byte ACCEL_ANGLEYZ = 5;
	SerialComm port;
	private Float angle[];
	private byte[] trama;
	private PFont font;
	
	public void setup(){
		size(300, 100);
		port = new SerialComm(this, "COM1", 57600);
		angle = new Float[2];
		angle[1] = (float) 0.0;
		angle[0] = (float) 0.0;
		byte[] start = {0,1};
		port.send_data(start);
		font = loadFont("ComicSansMS-16.vlw");
	}
	
	public void draw(){
		background(0);
		if (port.data_available()) {
			try {
				trama = port.get_next();
				set_angle(trama);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}	
		}	
		
		textFont(font, 16);
		fill(255);
		text("Angle YZ: " + angle[ACCEL_ANGLEYZ - 4].toString(), 50, 50);
		text("Angle XZ: " + angle[ACCEL_ANGLEXZ - 4].toString(), 50, 75);
	}
	
	//Evento que atiende la informacion que llega por serial.
	
	public void serialEvent(SerialComm port) {
		port.get_trama();
	}
	
	private void set_angle(byte[] trama) {
		Float a = (float)0;
		int correct = 0, b;
																				//< Calcula el angulo.
		if(trama.length != 8) return;
		correct = 0 | trama[6] & 0x1 | (trama[6] & 0x2) << 7 | (trama[6] & 0x4) << 14 | (trama[6] & 0x8) << 21;			//< Correccion
		b = (trama[2] << 24) | (trama[3] << 16) & 0x00FFFFFF | (trama[4] << 8) & 0x0000FFFF | trama[5] & 0x000000FF | correct;									//< Reconstruye el numero en punto flotante.	
		a = Float.intBitsToFloat(b);
		if (!Float.isNaN(a)) {
			println(a);
			a = (float) Math.atan(Math.sqrt(Math.abs(a)) * Math.signum(a));	
			angle[trama[1] - ACCEL_ANGLEXZ] = (float) Math.toDegrees(a);																				//< Guarda el valor.
		}
	}
}
