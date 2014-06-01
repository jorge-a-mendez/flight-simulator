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
		data.start();
	}
	
	public void draw(){
		background(0);
		lights();
		this.println(data);
	}
	
	public void serialEvent(SerialComm port) {
		port.get_trama();
	}
	
	// Main to execute the Applet...
	public static void main(String args[]) {
		PApplet.main(new String[] { "--present", "game_scube.GameControl" });
	}

}
