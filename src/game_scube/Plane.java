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
	
	public static final float WIDTH = 500;
	public static final float HEIGHT = 500;
	public static final float DEPTH = 500;
	
	// Codes to interpret the data
	
	private static final int X = 0;
	private static final int Y = 1;
	private static final int Z = 2;
	private static final int NOSHOT = 0;
	private static final int SOFT = 1;
	private static final int MEDIUM = 2;
	private static final int HARD = 3;
	
	
	private static final float[] PLANE_ALPHA = {(float) 0.2, (float) 0.15, (float) 0.15};
	private static final float[] CAM_ALPHA = {(float) 0.01, (float) 0.01, (float) 0.01};
	private PApplet parent;										//< Parent window. Need for uses of some methods. Others just allow static access to the method
	private PShape plane;										//< Plane object
	private PVector speed;										//< Maybe it is needed.
	private float size;											//< Size of the plane.
	private float[] angle;										//< Tilting angle (pitch and roll)
	private PVector posActual;									//< Plane position vector.
	private PVector camPos;
	private List<Bala> balas;			//< List of bullets
	private PositionProcessing pos_proc;						//< Processing of the position data given by the charging time of the plates.
	private PositionProcessing camera;
	
	
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
		pos_proc = new PositionProcessing(PLANE_ALPHA);	//< Position processing instantiated
		camera = new PositionProcessing(CAM_ALPHA);
		size = 40;
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
		this.angle[0] = -this.angle[0];
		this.angle[1] = PApplet.PI - this.angle[1];
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
		PVector pos = pos_proc.update_pos(RC);
		if(pos != null) posActual = pos;
		//posActual = new PVector(300,300,100);
		PApplet.println("PosActual : " + posActual);
		pos = camera.update_pos(RC);
		if(pos != null) camPos = pos;
	}
	
	PVector get_cam(){
		return camPos;
	}
	
	
	/* ########################################################################################
	 * 		Function: update_balas. Method for modifying bullets' positions
	 * 		Parameters:
	 * 			float ymax. Highest 'y' value of the background (lowest position possible)
	 * 			float zmax. Highest 'z' value of the background	(deepest position possible)
	 * 		Return:
	 * 
	 * ######################################################################################## */
	
	void update_balas(){
		for(int last = balas.size() - 1; last >= 0; last--){
			if(!(balas.get(last).update_pos())){
				balas.remove(last);
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
		parent.rotateZ(angle[1]);
		parent.rotateX(angle[0]);
		
		parent.scale(size);
		parent.shape(plane);
		
		parent.popMatrix();
		
		for(Bala b : balas){
			b.display();
		}
		
	}
	
	
	public void reset() {
		pos_proc.reset();
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
			speed = new PVector(0,-3*PApplet.sin(angle[X]),-3*PApplet.cos(angle[X]));		//< Speed's magnitude 1, direction given by plane's angles.
			//speed.normalize();							//< Not needed.
			bala = parent.createShape(PApplet.SPHERE,rad);
			bala.setFill(parent.color(133,128,139));
			bala.setStroke(false);
		}
		
		
		// Updates bullet's position if possible, returns false it bullet falls off the space.
		
		boolean update_pos(){
			pos.add(speed);
			if(pos.y > HEIGHT || pos.z > DEPTH){
				return false;
			}
			return true;
		}
		
		
		// Display bullet on screen.
		
		void display(){
			parent.pushMatrix();
			
			parent.translate(pos.x, pos.y, pos.z);
			//parent.scale(5);
			parent.shape(bala);
			
			parent.popMatrix();
		}
		
	}
	
	private static class PositionProcessing {
		private final float ALPHA[];
		private static final float MAX[] = {(float) 2.2, (float) 2.5, (float) 2.2};
		
		private float[] avg = new float[3];
		private float[] min = new float[3];
		private float[] max = new float[3];
		
		PositionProcessing(float[] alpha){
			int i;
			ALPHA = alpha;
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
			if(RC == null) return null;
			
			for(i = 0; i < 3; i++){
				if(RC[i] == 0) continue;			//< Do nothing if value is zero.
				if(!auto_cal(RC[i], i)) continue;					//< Update the boundaries of the data.
				RC[i] = linear(RC[i], i);			//< Normalize and linearize the data.
				update_avg(RC[i], i);				//< Update the average.
			}
			
			
			// Update the coordinates.
			PApplet.println("avgX: " + avg[0] + "	avgY: " + avg[1] + "	avgZ: " + avg[2]);
			PApplet.println("minX: " + min[0] + "	maxX: " + max[0]);
			PApplet.println("minY: " + min[1] + "	maxY: " + max[1]);
			PApplet.println("minZ: " + min[2] + "	maxZ: " + max[2]);
			position.x = PApplet.map(avg[0], 0, 1, 0, WIDTH);
			position.y = PApplet.map(1-avg[1], 0, 1, 0, HEIGHT);
			position.z = PApplet.map(avg[2], 0, 1, 0, DEPTH);
			
			PApplet.println("Pos " + position.toString());
			
			return position;
		}
		
		
		// Autocalibrate the boundaries of the data.
		
		private boolean auto_cal(float pos, int plate){
			//if(max[plate] != Float.NEGATIVE_INFINITY && pos > 1.6 * max[plate] )
				//return false;
			if(pos < min[plate])
				min[plate] = pos;
			if(pos > max[plate])
				max[plate] = pos;
			return true;
		}
		
		
		
		// Linearize the data from the plates.
		
		private float linear(float pos, int plate){
			float normalized = normalize(pos, plate);
			if(normalized == 0)
				return 1;
			float linear = PApplet.sqrt(1 / normalized);
			linear = PApplet.map(linear, 1, (float)MAX[plate], 0, 1);
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
				avg[plate] = avg[plate] * (1 - ALPHA[plate]) + (pos * ALPHA[plate]);
			}
		}
		
		private void reset() {
			for (int i = 0; i < 3; i++) {
				avg[i] = 0;
				min[i] = Float.POSITIVE_INFINITY;
				max[i] = Float.NEGATIVE_INFINITY;
			}
		}
	}
}
	
