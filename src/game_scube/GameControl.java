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
		size(640, 640, P3D);
		data = new GameData(this);
		p = new Plane(this);
		PVector pos = new PVector(250, 250, 250);
		p.update_pos(data.get_position());
		//p.update_pos(pos);
		float[] angles = new float[2];
		angles[0] = PI/4;
		angles[1] = PI/6;
		p.set_angles(angles);
		p.shoot(2);
		background(0);
		b = new Background(this, Plane.WIDTH, pos);
		b.set_angle(0);
		
		//b.set_pos(250, 250, 250);
		//b.set_tam(50);
	}
	
	public void draw(){
		background(255);
		//b.display();
		camera = p.get_cam();
		camera.x /= Plane.WIDTH;
		camera.y /= Plane.HEIGHT;
		camera.z /= Plane.DEPTH;
		camera(
			h + (camera.x - camera.z) * h,
			h + (camera.y - 1) * height,
			w * 2,
			h, h, h,
			0, 1, 0
		);
		
		lights();
		//PApplet.println(data);
		update_data();	
		display_data();
	}
	
	public void serialEvent(SerialComm port) {
		port.get_trama();
	}
	
	private void update_data(){
		
		p.update_pos(data.get_position());
		p.set_angles(data.get_angle());
		//float[] angles = new float[2];
		//angles[0] = PI/2;
		//angles[1] = PI/6;		p.set_angles(data.get_angle());
		p.shoot(data.get_pressure());
		//p.shoot(2);
		p.update_balas();
	}
	
	private void display_data(){
		p.display();
	}
	
	public void mousePressed() {
		
		if (mouseButton == RIGHT) {
			p.reset();
		}
	}
	
	// Main to execute the Applet...
	public static void main(String args[]) {
		PApplet.main(new String[] { "--present", "game_scube.GameControl" });
	}
	
	

}
