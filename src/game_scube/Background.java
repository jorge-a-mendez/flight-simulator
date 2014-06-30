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
		cube = p.createShape(PApplet.GROUP);
		
		
		PShape base = p.createShape();
		//Base...
		base.beginShape(PApplet.QUAD);
		base.fill(255);
		base.stroke(0);
		base.vertex((float)-0.5, (float)0.5, (float)0.5);
		base.vertex((float)0.5, (float)0.5, (float)0.5);
		base.vertex((float)0.5, (float)0.5, (float)-0.5);
		base.vertex((float)-0.5, (float)0.5, (float)-0.5);
		base.vertex((float)-0.5, (float)0.5, (float)0.5);
		
		base.endShape();
		
		//Left vertical face...
		PShape left = p.createShape();
		left.beginShape(PApplet.QUAD);
		left.fill(255);
		left.stroke(0);
		left.vertex((float)-0.5, (float)0.5, (float)0.5);
		left.vertex((float)-0.5, (float)-0.5, (float)0.5);
		left.vertex((float)-0.5, (float)-0.5, (float)-0.5);
		left.vertex((float)-0.5, (float)0.5, (float)-0.5);
		left.vertex((float)-0.5, (float)0.5, (float)0.5);
		left.endShape();
		
		//Front vertical face...
		
		PShape front = p.createShape();
		front.beginShape(PApplet.QUAD);
		front.fill(255);
		front.stroke(0);
		front.vertex((float)-0.5, (float)-0.5, (float)-0.5);
		front.vertex((float)0.5, (float)-0.5, (float)-0.5);
		front.vertex((float)0.5, (float)0.5, (float)-0.5);
		front.vertex((float)-0.5, (float)0.5, (float)-0.5);
		front.vertex((float)-0.5, (float)-0.5, (float)-0.5);
		front.endShape();
		
		cube.addChild(left);
		cube.addChild(front);
		cube.addChild(base);
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
