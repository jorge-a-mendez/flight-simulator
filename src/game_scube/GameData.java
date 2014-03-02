/**	##################################################################################
 * 		GameData Class. 
 * 			Esta clase encapsula los datos necesarios para el manejo del juego. 
 * 			Inicia recepcion de datos a traves del puerto serial, interpreta los datos. 
 * 			Los datos aqui almacenados deben estar procesados para usar directamente
 * 			por las funciones de processing.
 *  ###################################################################################
 */


package game_scube;
import 	serialcomm.SerialComm; 
import processing.core.*;
public class GameData {
	
	private final String name = "COM1";
	SerialComm port;
	private PVector posicion;
	private float angle;
	private float pressure_level;
	
	public GameData(PApplet p){
		port = new SerialComm(p, name);
		posicion = new PVector(0,0,0);
		angle = 0;
		pressure_level = 0;
	}
	
	public PVector get_position(){
		return posicion;
	}
	
	public float get_angle(){
		return angle;
	}
	
	public float get_pressure(){
		return pressure_level;
	}
	
	public void set_position(){
		
	}
	
	public void set_angle(){
		
	}
	
	public void set_pressure() {
		
	}
	
}
