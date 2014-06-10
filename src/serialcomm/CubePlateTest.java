package serialcomm;
import java.io.IOException;

import processing.core.*;
import processing.serial.Serial;
public class CubePlateTest extends PApplet{
	float RC, pos, avg, min, max;
	//Serial serial;
	SerialComm reader;
	String line;
	long lasttime;
	public void setup(){
	  size(800, 600);
	  reader = new SerialComm(this, SerialComm.list()[1], 57600);
	  pos = height;
	  avg = (float) 0;
	  min = Float.POSITIVE_INFINITY;
	  max = Float.NEGATIVE_INFINITY;
	  //reader = createReader("data.txt");
	  //lasttime = millis();
	  //frameRate(30);
	}


	public void draw(){
	  background(255);
	  update_pos();
	  update_screen();
	  //while (millis() - lasttime <= 100);
	}

	void update_pos(){
	  read_file();
	  if (pos != -1) {
	    if(pos == 0) noLoop();
	    auto_cal(pos);
	    pos = linear(pos);
	    println("posLinear: " + pos);
	    pos = 1 - pos;
	    update_avg(pos);
	  }
	  pos = avg;
	  
	}

	void update_screen(){
	  if(pos == -1) return;
	  pos = map(pos, 0, 1, 0, height);
	  println("posh: " + pos);
	  rect(width/2-width/20, pos, width/10, width/10);
	}

	void read_file(){
	  byte[] a = null;
	  if (reader.data_available()) {
		  try {
			a = reader.get_next();
			//println(a);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	  }
	  pos = get_position(a);
	  println(pos + "\n");
	  
	}
	
	float get_position(byte[] trama) {
		int b = 0, correct= 0;
		if(trama == null) return -1;
		//PApplet.println("No es null");
		//PApplet.println(trama);
		
		if(trama.length != 6) return -1;
		if(trama[1] != 2) return -1;
		
		correct = 0 | trama[4] & 0x1 | (trama[4] & 0x2) << 7;													//< Correccion
		b = (trama[2] << 8) & 0x0000FFFF | (trama[3]) & 0x000000FF | correct;									//< Reconstruye el numero en punto flotante.	
		return (float) b;
	}

	void auto_cal(float x){
	    if(x < min)
	      min = x;
	    if(x > max)
	      max = x;
	}

	float linear(float x){
	    float normalized = normalize(x);
	    if(normalized == 0)
	      return 1;
	    println("normalized: " + normalized);
	    float linear = sqrt(1 / normalized);
	    println("linear: " + linear);
	    linear = map(linear, 1, (float) 4, 0, 1);
	      //minDistance: 1, maxDistance: 4
	    return constrain(linear, 0, 1);
	}

	float normalize(float x) {
	    if(min == max || min == Float.POSITIVE_INFINITY)
	      return 0;
	    float n = map(x, min, max, 0, 1);
	    return constrain(n, 0, 1);
	}

	void update_avg(float x){
	    float alpha = (float) 0.15;
	    if(x == Float.POSITIVE_INFINITY)
	      return;
	    else {
	      avg = (avg * (1 - alpha)) + (x * alpha);
	        //alpha: .15
	      println("avg: " + avg + "\n");
	      println("min:" + min + " max:" + max);
	    }
	}
	
	public void serialEvent(SerialComm port) {
		port.get_trama();
	}
}
