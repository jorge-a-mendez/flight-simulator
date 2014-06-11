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
	
	
	// Codes to interpret the data
	
	private static final int X = 0;
	private static final int Y = 1;
	private static final int Z = 2;
	private static final int NOSHOT = 0;
	private static final int SOFT = 1;
	private static final int MEDIUM = 2;
	private static final int HARD = 3;
	
	private PApplet parent;										//< Parent window. Need for uses of some methods. Others just allow static access to the method
	private PShape plane;										//< Plane object
	private PVector speed;										//< Maybe it is needed.
	private float size;											//< Size of the plane.
	private float[] angle;										//< Tilting angle (pitch and roll)
	private PVector posActual;									//< Plane position vector.
	private List<Bala> balas;			//< List of bullets
	private PositionProcessing pos_proc;						//< Processing of the position data given by the charging time of the plates.
	
	
	/* ########################################################################################
	 * 		Function: Plane. Constructor of the class.
	 * 		Parameters:
	 * 			PApplet p. Applet that owns the object.
	 * 		Return:
	 * 			New plane instance.
	 * ######################################################################################## */
	
	
	Plane(PApplet p){
		parent = p;
		plane = p.loadShape("Drone.obj"); 		//< Load the plane shape from the file.
		speed = new PVector(0, 0, 0);			//< Speed vector initialize
		pos_proc = new PositionProcessing();	//< Position processing instantiated
		size = 100;
		balas = new ArrayList<Bala>();
	}

	
	/* ########################################################################################
	 * 		Function: set_angles. Method for modifying plane's angles
	 * 		Parameters:
	 * 			float[] angle. Array of angles to update 'angle' field.
	 * 		Return:
	 * 			
	 * ######################################################################################## */
	
	void set_angles(float[] angle){
		this.angle = angle;
	}
	
	
	/* ########################################################################################
	 * 		Function: set_size. Method for modifying plane's size.
	 * 		Parameters:
	 * 			float s. Size to update 'size' field
	 * 		Return:
	 * 			
	 * ######################################################################################## */
	
	void set_size(float s){
		size = s;
	}
	
	
	/* ########################################################################################
	 * 		Function: update_pos. Method for modifying plane's position
	 * 		Parameters:
	 * 			float [] RC. Array of RC constants of the capacitive sensors
	 * 		Return:
	 * 			
	 * ######################################################################################## */
	
	void update_pos(float[] RC){
		//posActual = pos_proc.update_pos(RC);
		posActual = new PVector(500,300,100);
	}
	
	
	/* ########################################################################################
	 * 		Function: update_balas. Method for modifying bullets' positions
	 * 		Parameters:
	 * 			float ymax. Highest 'y' value of the background (lowest position possible)
	 * 			float zmax. Highest 'z' value of the background	(deepest position possible)
	 * 		Return:
	 * 
	 * ######################################################################################## */
	
	void update_balas(float ymax, float zmax){
		for(Bala b : balas){
			if(!(b.update_pos(ymax, zmax))){
				balas.remove(b);
			}
		}
	}
	
	
	/* ########################################################################################
	 * 		Function: display. Method for displaying plain and bullets on screen
	 * 		Parameters:
	 * 			
	 * 		Return:
	 * 			
	 * ######################################################################################## */
	
	void display(){
		parent.pushMatrix();
		parent.translate(posActual.x, posActual.y, posActual.z);
		
		parent.rotateY(PApplet.PI);
		parent.rotateZ(PApplet.PI + angle[1]);
		parent.rotateX(-angle[0]);
		
		parent.scale(size);
		parent.shape(plane);
		
		parent.popMatrix();
		
		for(Bala b : balas){
			b.display();
		}
		
	}
	
	
	/* ########################################################################################
	 * 		Function: shoot. Method for adding a new bullet
	 * 		Parameters:
	 * 			int intensity. Intensity of the shot to fire. Possible values:
	 * 				- NOSHOT
	 * 				- SOFT
	 * 				- MEDIUM
	 * 				- HARD
	 * 		Return:
	 * 			
	 * ######################################################################################## */
	
	void shoot(int intensity){
		if(intensity != NOSHOT){
			Bala bala = new Bala(intensity);
			balas.add(bala);
		}
	}
	
	
	private class Bala {
		private static final int SOFT_RAD = 2;		//< Bullet radiuses for different intensities
		private static final int MEDIUM_RAD = 4;
		private static final int HARD_RAD = 8;
		
		private PVector pos;			//< Bullet's position
		private float rad;				//< Bullet's radius
		private PVector speed;			//< Bullet's speed
		private PShape bala;			//< Bullet object
		
		
		// Create bullet whose radius depends on shot intensity
		
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
			pos = posActual;		//< Bullet's initial position is plane's position at the time.
			speed = new PVector(0,PApplet.sin(angle[X]),PApplet.cos(angle[X]));		//< Speed's magnitude 1, direction given by plane's angles.
			//speed.normalize();							//< Not needed.
			bala = parent.createShape(PApplet.SPHERE,rad);
			bala.setFill(parent.color(133,128,139));
			bala.setStroke(false);
		}
		
		
		// Updates bullet's position if possible, returns false it bullet falls off the space.
		
		boolean update_pos(float ymax, float zmax){
			pos.add(speed);
			if(pos.y > ymax || pos.z > zmax){
				return false;
			}
			return true;
		}
		
		
		// Display bullet on screen.
		
		void display(){
			parent.pushMatrix();
			
			parent.translate(pos.x, pos.y, pos.z);
			parent.scale(10);
			parent.shape(bala);
			
			parent.popMatrix();
		}
	}
	
	private static class PositionProcessing {
		private static final float ALPHA = (float)0;
		private static final float width = 200;
		private static final float height = 200;
		private static final float depth = 500;
		private float[] avg = new float[3];
		private float[] min = new float[3];
		private float[] max = new float[3];
		
		PositionProcessing(){
			int i;
			
			for(i = 0; i < 3; i++){
				
				avg[i] = 0;			//< Charging time average of each plate.
				
				// Adaptive boundaries of the data.
				
				min[i] = Float.POSITIVE_INFINITY;
				max[i] = Float.NEGATIVE_INFINITY;
			}
			
		}
		
		PVector update_pos(float[] RC){
			int i;
			PVector position = new PVector(0, 0, 0);
			
			
			for(i = 0; i < 3; i++){
				if(RC[i] == 0) continue;			//< Do nothing if value is zero.
				auto_cal(RC[i], i);					//< Update the boundaries of the data.
				RC[i] = linear(RC[i], i);			//< Normalize and linearize the data.
				update_avg(RC[i], i);				//< Update the average.
			}
			
			
			// Update the coordinates.

			position.x = PApplet.map(avg[0], 0, 1, 0, width);
			position.y = PApplet.map(1-avg[1], 0, 1, 0, height);
			position.z = PApplet.map(1-avg[2], 0, 1, 0, depth);
			
			return position;
		}
		
		
		// Autocalibrate the boundaries of the data.
		
		private void auto_cal(float pos, int plate){
			if(pos < min[plate])
				min[plate] = pos;
			if(pos > min[plate])
				max[plate] = pos;
		}
		
		
		// Linearize the data from the plates.
		
		private float linear(float pos, int plate){
			float normalized = normalize(pos, plate);
			if(normalized == 0)
				return 1;
			float linear = PApplet.sqrt(1 / normalized);
			linear = PApplet.map(linear, 1, (float) 4.5, 0, 1);
			return PApplet.constrain(linear, 0, 1);
		}
		
		
		// Normalize the values. Transform from the actual range to the range [0,1]
		
		private float normalize(float pos, int plate){
			if(min[plate] == max[plate] || min[plate] == Float.POSITIVE_INFINITY)
				return 0;
			float n = PApplet.map(pos, min[plate], max[plate], 0, 1);
			return PApplet.constrain(n,  0,  1);
		}
		
		
		// Calculate the average position given by the plates using a single pole filter.
		
		private void update_avg(float pos, int plate){
			if(pos == Float.POSITIVE_INFINITY)
				return;
			else{
				avg[plate] = avg[plate] * (1 - ALPHA) + (pos * ALPHA);
			}
		}
	}
}
	
