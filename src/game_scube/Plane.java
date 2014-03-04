/**	##################################################################################
 * 		Plane Class. 
 * 			Esta clase implementa los metodos y varibles de control necesarias
 * 			para el manejo del avion dentro del juego. (En desarrollo)
 *  ###################################################################################
 */

package game_scube;
import processing.core.*;

// This class needs to have the Drone.obj file inside the bin folder.

public class Plane {
	
	private static final int X = 0;
	private static final int Y = 1;
	private static final int Z = 2;
	
	private PApplet parent;
	private PShape plane;
	private PVector posActual;
	private PVector speed;		//< Maybe it is needed.
	private float size;
	private float[] angle;
	
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
	
	void update_pos(PVector speed){
		posActual.add(speed);
	}
	
	void display(){
		parent.pushMatrix();
		parent.translate(posActual.x, posActual.y, posActual.z);
		parent.rotateX(angle[X]);
		parent.rotateY(angle[Y]);
		parent.rotateZ(angle[Z]);
		parent.scale(size);
		parent.shape(plane);
	}	
	
	//Esta clase servira para el manejo de la animacion de las balas al disparar.
	//Status: En proceso creativo...
	
	private class Bala{
		
	}
}
