package game_scube;

import processing.core.*;
import serialcomm.SerialComm;

public class GameControl extends PApplet {
	
	Background b;
	Plane p;
	GameData data;
	PVector camera;
	float w = Plane.WIDTH/2;
	float h = w/2;
	
	public void setup(){
		size(800, 800, P3D);
		data = new GameData(this);
		p = new Plane(this);
		PVector pos = new PVector(Plane.WIDTH/2, Plane.HEIGHT/2, -Plane.DEPTH/2);
		p.update_pos(data.get_position());
		float[] angles = new float[2];
		angles[0] = PI/4;
		angles[1] = PI/6;
		p.set_angles(angles);
		p.shoot(1);
		background(0);
		b = new Background(this, Plane.WIDTH, pos);
		b.set_angle(0);

	}
	
	public void draw(){
		background(150);
		b.display();
		camera(
			(float)3*h,
			(float)-0.5*h,
			w * 4,
			h, h, h,
			0, 1, 0
		);

		update_data();	
		display_data();
	}
	
	public void serialEvent(SerialComm port) {
		port.get_trama();
	}
	
	private void update_data(){
		
		p.update_pos(data.get_position());
		p.set_angles(data.get_angle());
		p.shoot(data.get_pressure());
		p.update_balas();
	}
	
	private void display_data(){
		p.display();
	}
	
	public void mousePressed() {
		
		if (mouseButton == RIGHT) {
			p.reset();
			println("RESETTED");
		}
	}
	
	// Main to execute the Applet...
	public static void main(String args[]) {
		PApplet.main(new String[] { "--present", "game_scube.GameControl" });
	}
	
	

}
