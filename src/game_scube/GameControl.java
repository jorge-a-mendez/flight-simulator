package game_scube;

import processing.core.*;

public class GameControl extends PApplet {
	
	Background b;
	Plane p;
	
	public void setup(){
		size(1000, 640, P3D);
		b = new Background(this, 2000, new PVector(width/2, height/2-200, -2000));
		b.set_angle(-QUARTER_PI);
	}
	
	public void draw(){
		background(0);
		lights();
		b.display();
	}
	
	// Main to execute the Applet...
	public static void main(String args[]) {
		PApplet.main(new String[] { "--present", "game_scube.GameControl" });
	}

}
