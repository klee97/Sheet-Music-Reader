//PictureReader.java
//main class to read sheet music
import java.awt.Color;
import java.util.*;
import java.lang.Math;

//files for javax.sound.Clip
import java.io.*;
import java.net.URL;
import javax.sound.sampled.*;
import javax.swing.*;
   
public class PictureReader{
//http://theremin.music.uiowa.edu/MISpiano.html  

  //extracts files from a folder and returns them as an array
  public static String[] extract(File folder){
    String [] files = new String[13];
    int index=0;
    
    for (File fileEntry : folder.listFiles()){
      if (!fileEntry.getName().equals(".DS_Store")){//it's a mac thing
      files[index] = fileEntry.getName();
      index++;
      }
    }
    Arrays.sort(files);
    Collections.reverse(Arrays.asList(files));
    return files;
  }

//plays a soundFile with a pause dependent on temp
  public static void play(File soundFile, int tempo){
    //1 sec = 1000; to find seconds, divide tempo by 60
    int sec = 1000 * 60/tempo; 
      try{
        AudioInputStream audioIn = AudioSystem.getAudioInputStream(soundFile);
        Clip clip= AudioSystem.getClip();
        clip.open(audioIn);
        clip.start(); 
        Thread.sleep(sec);
  
      //exceptions
      } catch (UnsupportedAudioFileException e) {
        e.printStackTrace();
      } catch (IOException e) {
        e.printStackTrace();
      } catch (LineUnavailableException e) {
       e.printStackTrace();
      } catch (InterruptedException e) {
        Thread.currentThread().interrupt();
      }
  }
  
  //sorts list notes by average x values
  public static Note[] sortX(Note [] notes){
    for (int i = (notes.length - 1); i >= 0; i--)
   {
      for (int j = 1; j <= i; j++)
      {
         if (notes[j-1].avgX() > notes[j].avgX())
         {
              Note temp = notes[j-1];
              notes[j-1] = notes[j];
              notes[j] = temp;
         } 
      }
    }
    return notes;
  }

  //converts picture into just black and white
  public static Picture convert(Picture picture){
    //i is col, j is row
    for (int i = 0; i < picture.width(); i++){
      for (int j = 0; j < picture.height(); j++){
        Color c = picture.get(i,j);
        if (Luminance.compatible(c, Color.BLACK)){
          picture.set(i, j, Color.BLACK);
        }
        else{
          picture.set(i, j, Color.WHITE);
        }

      }
    }
    return picture; 
  }

  //removes horizontal lines of a picture given a list of line beginnings
  public static Picture removeLines(Picture picture, Note[] lines, int measureMin, int measureMax){
   //   Picture newPicture = new Picture(picture);
      int maxY, minY;
      for (int i = 0; i < lines.length; i++){
       
        maxY = lines[i].maxY;
        minY = lines[i].minY;
        if (minY >= measureMin && maxY <= measureMax){
          //for each column, going along the picture, checks above and below line for activity
          for (int x = 0; x < picture.width(); x++){
           //does nothing if there is activity above or below; else, converts pixel to black
            if (picture.get(x, maxY+3).equals(Color.WHITE) || picture.get(x, minY-3).equals(Color.WHITE)){
            }
            else{
              for (int y = minY-1; y <= maxY+1; y++){
                picture.set(x, y, Color.BLACK);
              }
            }
          }
        }
      }
      return picture;
      
  }
  
  //given array of line centers and note, returns the index of line the note is closest to
  public static int findLine(Note note, double[] centers){
    //initial min is distance between center of note and first line space
    double min = Math.abs(note.avgY() - centers[0]);
    double temp = 0;
    int index = 0;
    for (int i = 0; i < centers.length; i++){
      temp = Math.abs(note.avgY() - centers[i]);
      if (temp < min){

        min = temp;
        index = i;
      }
    }
    return index;
  }
  
  //given picture, returns a picture with vertical lines removed
  public static Picture removeStems(Picture picture){
    //height is row max, width is column max
    int count;//count will be a pixel count; to be a vertical line, this count must be less than a certain width
    for (int i = 0; i < picture.height(); i++){
      count = 0;
      for (int j = 0; j < picture.width(); j++){
        //if there is a white pixel, increase the pixel count
        if (picture.get(j, i).equals(Color.WHITE)){
          count++;
        }
        //if there is a black pixel and the pixel count is low, go back and turn the other white pixels black
        else{
          if (count > 0 && count < 6){
            while (count > 0){
              picture.set(j-count,i,Color.BLACK); // j is the column
              count--;
            }
          }
          else{
            count = 0;
          }
        }
      }
    }
    return picture;
  }
 
  public static void main(String[] args) {

    Picture picture = new Picture(args[0]); //picture is first argument
    int tempo = Integer.parseInt(args[1]); //tempo is second argument
    int nSize = 50; //minimum size for a blob to be "significant"
    
    picture = convert(picture); 

   
    //creates a BlobFinder to find lines in the picture
    BlobFinder lineFind = new BlobFinder(picture);
    Note[] lines = lineFind.getLines(); //the type "Note" is a little misleading...
    int sets = lines.length/5; //how many lines of music there are
    picture = removeStems(picture);
    
    //if the number of lines is not a multiple of 5, return error and exit
    if (lines.length%5 !=0){
      System.out.println("Improperly formatted staff lines");
      System.exit(0);
    }
    
    //creates a list of line centers
    double[] centers = new double[(lines.length)*2*sets+3*sets]; //will go from top to bottom
    int j = 0;
    //for loop that runs once for each set of 5 staff lines (i.e. 1 line of music)
    for (int k = 0; k <lines.length; k = k+5){
      
      //adds centers for intervals above the staff lines
      double avgHalf = ((double) (lines[k+1].minY - lines[k].maxY))/2;
      centers[k] = lines[k].minY - avgHalf*2;
      j++;    
      centers[k+1] = lines[k].minY - avgHalf;
      j++;
      
      //adds centers for intervals on and between staff lines
      for (int i = 0; i < 5; i++){
        centers[j] = lines[k+i].avgY();
        j++;

        if (i+1 <lines.length){
          centers[j] = centers[j-1] + avgHalf;
          j++;
        } 
      
        picture.set(lines[k+i].maxX, lines[k+i].maxY, Color.red);
        picture.set(lines[k+i].minX, lines[k+i].minY, Color.green);
      }
      //adds centers for intervals below staff lines
      centers[j] = lines[k+4].maxY + avgHalf;
      j++;    
      
      //remove lines from picture so we can find notes
      picture = removeLines(picture, lines, lines[k].minY, lines[k+4].maxY);
      BlobFinder noteFind = new BlobFinder(picture);
      
      //sets boundaries for where to find notes based on location of staff lines
      int min = lines[k].minY-nSize;
      if (min < 0){min = 0;}
      int max = lines[k+4].maxY+nSize;
      if (max > picture.height()){max = picture.height();}
     
      //finds notes and sorts them based on x values
      Note[] notes = sortX(noteFind.getBeads(nSize, min, max)); 
      
      File soundFile = new File("notelibrary");
      String [] fileNames = extract(soundFile); 
      int temp;
      //for each note, find nearest line and plays corresponding audiofile
      for (int i = 0; i < notes.length; i++){
        temp = findLine(notes[i], centers);

        sets = k/5;
        soundFile = new File("notelibrary/"+fileNames[temp-sets*13]);
        play(soundFile, tempo);
      }

    }
  }
}