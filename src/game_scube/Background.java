package game_scube;

import processing.core.*;

public class Background {

	private PShape cube;
	private PVector pos;
	private PApplet parent;
	private float angleY;
	private float size;
	
	Background(PApplet p, float size, PVector pos){
		this.parent = p;
		this.size = size;
		this.pos = pos;
		this.angleY = 0;
		cube = p.createShape();
		cube.beginShape(p.QUADS);
		
		cube.fill(155);
		
		//Base...
		cube.vertex((float)-0.5, (float)0.5, (float)0.5);
		cube.vertex((float)0.5, (float)0.5, (float)0.5);
		cube.vertex((float)0.5, (float)0.5, (float)-0.5);
		cube.vertex((float)-0.5, (float)0.5, (float)-0.5);
		cube.vertex((float)-0.5, (float)0.5, (float)0.5);
		
		//Right vertical face...
		cube.vertex((float)-0.5, (float)0.5, (float)0.5);
		cube.vertex((float)-0.5, (float)-0.5, (float)0.5);
		cube.vertex((float)-0.5, (float)-0.5, (float)-0.5);
		cube.vertex((float)-0.5, (float)0.5, (float)-0.5);
		
		//Left vertical face...
		cube.vertex((float)-0.5, (float)-0.5, (float)-0.5);
		cube.vertex((float)0.5, (float)-0.5, (float)-0.5);
		cube.vertex((float)0.5, (float)0.5, (float)-0.5);
		
		cube.endShape();
	}
	
	void set_pos(int x, int y, int z){
		pos = new PVector(x, y, z);
	}
	
	void set_tam(float tam){
		size = tam;
	}
	
	void set_angle(float a){
		angleY = a;
	}
	
	void display(){
		parent.pushMatrix();
		parent.translate(pos.x, pos.y, pos.z);
		parent.scale(size);
		parent.rotateY(angleY);
		parent.shape(cube);
		parent.popMatrix();
	}
	
}
