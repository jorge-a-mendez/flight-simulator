package game_scube;
import processing.core.*;

public class Bomb {
	static final float SPEED = 2;
	static final float RAD = 10;
	
	PApplet p;
	float xpos;
	float ypos;
	float zpos;
	PShape body;
	
	//w es el ancho total, d es la prfundidad total
	Bomb(PApplet p_, float w, float d){
		p = p_;
		xpos = p.random(w);
		ypos = 0;
		zpos = p.random(d);
		body = p.createShape(PApplet.SPHERE, RAD);
		body.setFill(0);
	}
	
	//w: ancho total, h: altura total, d: profundidad total
	public void moveBomb(float w, float h, float d){
		ypos += SPEED;
		if(ypos >= h){
			ypos = 0;
			xpos = p.random(w);
			zpos = p.random(d);
		}
	}
	
	public void drawBomb(){
		p.pushMatrix();
		
		p.translate(xpos, ypos, zpos);
		p.shape(body);
		
		p.popMatrix();
	}
	
	public static float get_bombRadius(){
		return RAD;		
	}
	
	public PVector get_bombPos(){
		return new PVector(xpos,ypos,zpos);
	}
}
