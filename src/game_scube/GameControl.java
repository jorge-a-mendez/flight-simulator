package game_scube;

import processing.core.*;
import serialcomm.SerialComm;

public class GameControl extends PApplet {
	
	Background b;
	Plane p;
	GameData data;
	
	public void setup(){
		size(1000, 640, P3D);
		data = new GameData(this);
		p = new Plane(this);
		p.shoot(2);
	}
	
	public void draw(){
		background(0);
		lights();
		PApplet.println(data);
		update_data();
		display_data();
	}
	
	public void serialEvent(SerialComm port) {
		port.get_trama();
	}
	
	private void update_data(){
		
		p.update_pos(data.get_position());
		//p.set_angles(data.get_angle());
		float[] angles = new float[2];
		angles[0] = PI/4;
		angles[1] = PI/6;
		p.set_angles(angles);
		//p.shoot(data.get_pressure());
		p.shoot(2);
		p.update_balas(500, 500);
	}
	
	private void display_data(){
		p.display();
	}
	
	// Main to execute the Applet...
	public static void main(String args[]) {
		PApplet.main(new String[] { "--present", "game_scube.GameControl" });
	}
	
	

}
