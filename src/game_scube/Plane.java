/**	##################################################################################
 * 		Plane Class. 
 * 			Esta clase implementa los metodos y varibles de control necesarias
 * 			para el manejo del avion dentro del juego. (En desarrollo)
 *  ###################################################################################
 */

package game_scube;
import java.util.ArrayList;
import java.util.List;

import processing.core.*;

// This class needs to have the Drone.obj file inside the bin folder.

public class Plane {
	
	private static final int X = 0;
	private static final int Y = 1;
	private static final int Z = 2;
	private static final int NOSHOT = 0;
	private static final int SOFT = 1;
	private static final int MEDIUM = 2;
	private static final int HARD = 3;
	
	private PApplet parent;
	private PShape plane;
	private PVector posActual;
	private PVector speed;		//< Maybe it is needed.
	private float size;
	private float[] angle;
	private List<Bala> balas = new ArrayList<Bala>();
	
	Plane(PApplet p, int x, int y, int z){
		parent = p;
		plane = p.loadShape("Drone.obj");
		angle = new float[3];
		angle[X] = angle[Y] = angle[Z] = 0;
		posActual = new PVector(x, y, z);
		speed = new PVector(0, 0, 0);
		size = 1;
	}
	
	void set_angles(Float x, Float y, Float z){
		if(x != null) angle[X] = x;
		if(y != null) angle[Y] = y;
		if(x != null) angle[Z] = z;
	}
	void set_size(float s){
		size = s;
	}
	
	void update_pos(PVector speed){
		posActual.add(speed);
	}
	
	void update_pos2(PVector new_pos){
		posActual = new_pos;		
	}
	
	void update_balas(float ymax, float zmax){
		for(Bala b : balas){
			if(!(b.update_pos(ymax, zmax))){
				balas.remove(b);
			}
		}
	}
	
	void display(){
		parent.pushMatrix();
		parent.translate(posActual.x, posActual.y, posActual.z);
		parent.rotateX(angle[X]);
		parent.rotateY(angle[Y]);
		parent.rotateZ(angle[Z]);
		parent.scale(size);
		parent.shape(plane);
		for(Bala b : balas){
			b.display();
		}
		parent.popMatrix();
	}
	
	void shoot(int intensity){
		if(intensity != NOSHOT){
			Bala bala = new Bala(intensity);
			balas.add(bala);
		}
	}
	
	//Esta clase servira para el manejo de la animacion de las balas al disparar.
	//Status: En proceso creativo...
	
	private class Bala{
		private static final int SOFT_RAD = 2;
		private static final int MEDIUM_RAD = 4;
		private static final int HARD_RAD = 8;
		
		private PVector pos;
		private float rad;
		private PVector speed;
		private PShape bala;
		
		Bala(int intensity){
			switch(intensity){
				case SOFT:
					rad = SOFT_RAD;
					break;
				case MEDIUM:
					rad = MEDIUM_RAD;
					break;
				case HARD:
					rad = HARD_RAD;
					break;
			}	
			pos = posActual;
			speed = new PVector(0,PApplet.sin(angle[X]),PApplet.cos(angle[X]));
			//speed.normalize();							//< Not needed.
			bala = parent.createShape(PApplet.SPHERE,rad);
			bala.setFill(parent.color(133,128,139));
			bala.setStroke(false);
		}
		
		//false si la bala salio del espacio predeterminado
		boolean update_pos(float ymax, float zmax){
			pos.add(speed);
			if(pos.y > ymax || pos.z > zmax){
				return false;
			}
			return true;
		}
		
		void display(){
			parent.pushMatrix();
			
			parent.translate(pos.x, pos.y, pos.z);
			parent.shape(bala);
			
			parent.popMatrix();
		}
	}
}
