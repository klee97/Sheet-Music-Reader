//Note.java
//represents a significant cluster of pixels
public class Note{
  int mass;
  int maxX;
  int minX;
  int maxY;
  int minY;
  
//default constructor   
  Note(){
    mass = 0;
    maxX = 0;
    minX = 0; 
    maxX = 0;
    minY = 0;
  }
  
//adds a pixel to the note
  public void add(int inX, int inY){
    if (minY == 0){
      minY= inY;
    }
    if (inY < minY){
      minY = inY;
    }
    if (minX == 0){
      minX= inX;
    }
    if (inX < minX){
      minX = inX;
    }
    if (inX > maxX){
      maxX = inX;
    }
    if (inY > maxY){
      maxY = inY;
    }
    mass++;
  }
//mass accessor
  public int mass(){
    return mass; 
  }
  
//finds average x
  public double avgX(){
    return minX + ((double) (maxX-minX))/2; 
  }
 
//finds average y  
  public double avgY(){
    return minY + ((double) (maxY-minY))/2; 
  }
}
