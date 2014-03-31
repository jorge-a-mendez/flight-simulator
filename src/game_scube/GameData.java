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
import  java.lang.Math.*;
public class GameData extends Thread {
	
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
	
	
	private final String name = "COM5";
	SerialComm port;
	private PVector position;
	private float angle[];
	private float pressure_level;
	private String[] keys;						//< Keys to sync
	private int wait;							//< Time to wait
	private boolean running;
	
	public GameData(PApplet p){
		port = new SerialComm(p, name, 115200);
		position = new PVector(0,0,0);
		angle = new float[2];
		pressure_level = 0;
		keys = new String[3];
		keys[POSITION] = "position";
		keys[PRESSURE] = "pressure";
		keys[ANGLE] = "angle";
	}
	
	public void start() {
		wait = 3;
		running = true;
		byte[] begin = {0,1};
		
		port.send_data(begin);
		
		try{
			this.wait(3);
		}catch (Exception e) {
			
		}
		super.start();
	}
	
	public void run() {
		while(running){										//< Main loop of the thread.
			
			if(port.read_alldata()){
				for(byte[] a : port.data()){				//< Procesa cada trama en la lista.
					switch(a[1]){
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
			try{
				Thread.sleep(wait);
			}catch(Exception e){
				PApplet.println(e);
			}
		}
	}
	
	public void quit() {
		running = false;
		interrupt();						//< Si el hilo esta esperando.
	}
	
	// Private setters.
	
	private void set_angle(byte[] trama) {
		float a = 0;
		int correct = 0;
		if(trama.length != 8) return;
		correct = 0 | trama[6] & 0x1 | (trama[6] & 0x2) << 7 | (trama[6] & 0x4) << 14 | (trama[6] & 0x8) << 21;			//< Correccion
		a = 0 | (trama[5] << 24 | trama[4] << 16 | trama[3] << 8 | trama[2]) | correct;									//< Reconstruye el numero en punto flotante.	
		a = (float) Math.atan(Math.sqrt(a));																			//< Calcula el angulo.
		synchronized(keys[ANGLE]){
			angle[ACCEL_ANGLEXZ - 4] = a;																				//< Guarda el valor.
		}
	}
	
	private void set_pressure(byte[] trama) {
		synchronized(keys[PRESSURE]){
			
		}
	}
	
	private void set_position(byte[] trama) {
		synchronized(keys[POSITION]){
			
		}
	}
	
	
	//Public getters...
	
	public PVector get_position(){
		PVector p = null;
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
	
	public float get_pressure(){
		float p;
		synchronized(keys[PRESSURE]){
			p = pressure_level;
		}
		return p;
	}
	
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
			s.append("Angle XZ: " + angle[0]);
			s.append("   Angle YZ: " + angle[1]);
		}
		s.append("\n\n");
		s.append(keys[PRESSURE]);
		s.append(" -> ");
		synchronized(keys[PRESSURE]) {
			s.append(pressure_level);
		}
		
		return s.toString();
	}
	
}
