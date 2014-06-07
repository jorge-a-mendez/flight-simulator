/**	#############################################################################################
 * 		GameData Class. 
 * 			Esta clase encapsula los datos necesarios para el manejo del juego. 
 * 			Inicia recepcion de datos a traves del puerto serial, interpreta los datos. 
 * 			Los datos aqui almacenados deben estar procesados para usar directamente
 * 			por las funciones de processing.
 *  #############################################################################################
 */


package game_scube;
import 	serialcomm.SerialComm; 
import processing.core.*;
public class GameData {
	
	public static final byte POSITION = 0;
	public static final byte PRESSURE = 1;
	public static final byte ANGLE = 2;
	
	// Data codes...
	static final byte PANELX = 1;
	static final byte PANELY = 2;
	static final byte PANELZ = 3;
	static final byte ACCEL_ANGLEXZ = 4;	
	static final byte ACCEL_ANGLEYZ = 5;
	static final byte PIEZO = 6;
	
	//Data variables.
	private final String name = "COM6";				//< Communication port name. Just for debugging
	SerialComm port;								//< Serial port instance.
	private int[] position;							//< Contains the RC times of each plate. 
	private float angle[];							//< Contains the tilting angle (pitch and roll)
	private int pressure_level;						//< Last pressure level received.
	private String[] keys;							//< Keys to sync
	GameDataThread thread;							//< Thread in charge of reading the data out of the serial port.
		
	public GameData(PApplet p){
		port = new SerialComm(p, name, 57600);		//< New port.
		// Initialize data holders.
		position = new int[3];						
		angle = new float[2];
		pressure_level = 0;
		
		// Keys to sync the access of the readers and writers to the buffer.
		
		keys = new String[3];
		keys[POSITION] = "position";
		keys[PRESSURE] = "pressure";
		keys[ANGLE] = "angle";
		
		// Initialize thread to always read the data out of the port.
		
		thread = new GameDataThread();
		thread.start();
	}
	
	// ###########################################################################################################################################
	
	//								PRIVATE SETTERS
	
	// ###########################################################################################################################################

	
	private void set_angle(byte[] trama) {
		float a = 0;
		int correct = 0, b;
																				//< Calcula el angulo.
		synchronized(keys[ANGLE]){
			if(trama.length != 8) return;
			correct = 0 | trama[6] & 0x1 | (trama[6] & 0x2) << 7 | (trama[6] & 0x4) << 14 | (trama[6] & 0x8) << 21;			//< Correccion
			b = (trama[2] << 24) | (trama[3] << 16) & 0x00FFFFFF | (trama[4] << 8) & 0x0000FFFF | trama[5] & 0x000000FF | correct;		//< Interpret the floating point number.	
			a = Float.intBitsToFloat(b);
			if (a != Float.NaN) {
				a = (float) Math.atan(Math.sqrt(Math.abs(a)) * Math.signum(a));	
				angle[trama[1] - ACCEL_ANGLEXZ] = a;																		//< Guarda el valor.
			}
		}
	}
	
	private void set_pressure(byte[] trama) {
		if(trama.length != 5) return;
		synchronized(keys[PRESSURE]){
			pressure_level = (int)trama[2];
		}
	}
	
	private void set_position(byte[] trama) {
		int correct = 0;
		synchronized(keys[POSITION]) {
			if (trama.length != 6) return;
			correct = 0 | trama[6] & 0x1 | (trama[6] & 0x2) << 7;			//< Correction
			position[trama[2] - 1] = (trama[3] << 8) & 0x0000FFFF | trama[4] & 0x000000FF | correct;		//< Reconstruct the integer out of the code 
		}
	}

	// ###########################################################################################################################################
	
	//								PUBLIC GETTERS.
	
	// ###########################################################################################################################################
	
	public int[] get_position(){
		int[] p = null;
		synchronized(keys[POSITION]){
			p = position;
		}
		return p;
	}
	
	public float[] get_angle(){
		float[] a;
		synchronized(keys[ANGLE]){
			a = angle;
		}
		return a;
	}
	
	public int get_pressure(){
		int p;
		synchronized(keys[PRESSURE]){
			p = pressure_level;
			pressure_level = 0;		//< When the data is read, it goes back to NOSHOT.
		}
		return p;
	}
	
	
	// Method to print out the data buffer.
	
	public String toString() {
		StringBuilder s = new StringBuilder();
		s.append("\n\n");
		s.append(keys[POSITION]);
		s.append(" -> ");
		synchronized(keys[POSITION]) {
			s.append(position.toString());
		}
		s.append("\n\n");
		s.append(keys[ANGLE]);
		s.append(" -> ");
		synchronized(keys[ANGLE]) {
			s.append("Angle XZ: " + Math.toDegrees(angle[0]));
			s.append("   Angle YZ: " + Math.toDegrees(angle[1]));
		}
		s.append("\n\n");
		s.append(keys[PRESSURE]);
		s.append(" -> ");
		synchronized(keys[PRESSURE]) {
			s.append(pressure_level);
		}
		
		return s.toString();
	}
	
	private class GameDataThread extends Thread {
		private boolean running;
		
		GameDataThread() {
			running = false;
		}
		
		public void start() {
			running = true;
			
			byte[] begin = {0,1};				//< Send the starting code to the MCU
			port.send_data(begin);
			
			super.start();						//< Start the thread.
		}
		
		public void run() {
			byte[] a = null;
			while(running){										//< Main loop of the thread.
				if(port.data_available()){		
					try {
						a = port.get_next();					//< Process next code in the queue
					} catch (Exception e) {
						PApplet.println(e);
					}
					switch(a[1]){								//< Choose the corresponding setter for the code received.
					case PANELX:
					case PANELY:
					case PANELZ:
						set_position(a);
						break;
					case ACCEL_ANGLEXZ:
					case ACCEL_ANGLEYZ:
						set_angle(a);
						break;
					case PIEZO:
						set_pressure(a);
						break;
					}
					
				}
			}
		}
		
		public void quit() {
			running = false;
			interrupt();						//< Si el hilo esta esperando.
		}
	}
}


