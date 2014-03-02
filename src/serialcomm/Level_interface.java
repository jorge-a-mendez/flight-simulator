/**	##################################################################################
 * 		Level_Interface Class.
 * 			Esta clase implementa una interfaz de prueba para recepcion
 * 			de la data proveniente de un potenciometro. Corresponde a la aplicacion
 * 			PruebaSerial.c
 *  ###################################################################################
 */
package serialcomm;
import processing.core.*;
public class Level_interface extends PApplet{
	
	static final byte POTENTIOMETER = 1;
	static final byte CORRECTION_LASTBYTE = 2;
	
	SerialComm port;
	FillingBar bar;
	
	public void setup(){
		//port = new SerialComm(this, "COM1");
		size(320, 150, P2D);
		bar = new FillingBar(this, 10, 10);
		bar.set_tam(100);
	}
	
	public void draw(){
		background(0);
		bar.fill(mouseX*100/width);
		bar.display();
	}
	
	
	// Main to execute the Applet...
	public static void main(String args[]) {
		PApplet.main(new String[] { "--present", "serialcomm.Level_interface" });
	}

	private class FillingBar{
		PVector pos;
		float tam;
		PShape outer_rect;
		PShape inner_rect;
		//PApplet parent;
		
		FillingBar(PApplet p, int x, int y){
			pos = new PVector(x, y);
			p.stroke(0);
			p.fill(150);
			outer_rect = createShape(RECT, 0, 0, 300, 100);
			inner_rect = createShape(RECT, 0, 0, 300, 100);
			tam = (float) 0.01;
			//parent = p;
		}
		
		void fill(int x){
			inner_rect = createShape(RECT, 0, 0, x*3, 100);
			inner_rect.setFill(color(155,0,0));
		}
		
		void display(){
			translate(pos.x, pos.y);
			scale(tam);
			shape(outer_rect);
			shape(inner_rect);
		}
		
		void set_tam(float x){
			tam = (float) (0.01*x);
		}
		
		int get_width(){
			return (int)(300 * tam);
		}
		
		int get_height(){
			return (int)(100 * tam);
		}
	}
}
