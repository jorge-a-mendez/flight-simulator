/**	##################################################################################
 * 		Level_Interface Class.
 * 			Esta clase implementa una interfaz de prueba para recepcion
 * 			de la data proveniente de un potenciometro. Corresponde a la aplicacion
 * 			PruebaSerial.c
 *  ###################################################################################
 */
package serialcomm;
import processing.core.*;
import processing.serial.Serial;
public class Level_interface extends PApplet{
	
	SerialPot port;
	FillingBar bar;
	Float t;
	public void setup(){
		size(320, 200, P2D);
		bar = new FillingBar(this, 10, 10);
		bar.set_tam(100);
		port = new SerialPot(this, "COM4", 9600, SerialPot.BIT12);
		thread("reading");
	}
	
	public void draw(){
		t = port.normalize();
		if(t != null){
			background(0);
			bar.fill((int)(t*100));
			bar.display();
		}
		//if(port.available() > 0) println(port.readBytes());		//< For debugging.
	}
	
	private class SerialPot extends SerialComm{
		
		static final byte POTENTIOMETER = 1;
		static final byte NO_CORRECTION = 0;
		static final byte CORRECTION_LASTBYTE = 2;
		static final byte BIT12 = 0;
		static final byte BIT10 = 1;
		static final byte BIT8 = 2;
		
		int mode;
		
		SerialPot(PApplet p, String port, int baudrate, int mode){
			super(p, port, baudrate);
			this.mode = mode;
		}
		
		Integer amplitude(){
			int a = 0;
			if(super.buffer[0] != POTENTIOMETER) return null;
			a = 0 | (super.buffer[1] << 8);
			a |= (0xFF) & super.buffer[2];
			if(super.buffer[3] == CORRECTION_LASTBYTE) a++;
			return a;
		}
		
		Float normalize(){
			if(super.buffer == null) return null;
			Integer t = this.amplitude();
			if(t == null) return null;
			switch(this.mode){
			case BIT8:
				return (float)t / (float) 255.0;
			case BIT10:
				return (float)t / (float) 1023.0;
			case BIT12:
				return (float)t / (float) 4095.0;
			default:
				return null;
			}
		}
	}
	
	private class FillingBar{
		PVector pos;
		float tam;
		PShape outer_rect;
		PShape inner_rect;
		//PApplet parent;
		
		FillingBar(PApplet p, int x, int y){
			pos = new PVector(x, y);
			p.noStroke();
			p.fill(150);
			outer_rect = createShape(RECT, 0, 0, 300, 100);
			//inner_rect = createShape(RECT, 0, 0, 300, 100);
			gradient_horizontal(color(255,0,0), color(0,255,0), 300, 100);
			tam = (float) 0.01;
			
		}
		
		void fill(int x){
			outer_rect = createShape(RECT, 300-3*x, 0, x*3, 100);
			inner_rect.setFill(150);
		}
		
		private void gradient_horizontal(int inicio, int fin, int width, int height){
			int c; 
			inner_rect = createShape();
			inner_rect.beginShape();
			inner_rect.strokeWeight(1);
			for (int i = 0; i <= width; i++){
				c = lerpColor(inicio, fin, map(i,0, width, 0, 1));
				inner_rect.stroke(c);
				inner_rect.vertex(i,0);
				inner_rect.vertex(i, height);
			}
			inner_rect.endShape();
		}	
		
		void display(){
			translate(pos.x, pos.y);
			scale(tam);
			shape(inner_rect);
			shape(outer_rect);
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
	
	//< Thread to poll for new data.
	public void reading(){
		for(;;)
			port.read_data();
	}
	
	
}
