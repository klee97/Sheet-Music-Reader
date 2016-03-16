//BlobFinder.java
//used to find blobs of pixels that make up notes and beginnings of lines 
//the general stategy for this program was adapted from a Princeton lab assignment found here: http://www.cs.princeton.edu/courses/archive/fall15/cos126/assignments/atomic.html
import java.util.*;
import java.awt.Color;

public class BlobFinder{
  Picture image;
  int height;
  int width;
  boolean[][] toVisit;
  
  //Constructor with image and luminance threshold as parameters
  public BlobFinder(Picture picture){
    image = picture;
    height = picture.height();
    width = picture.width();
    toVisit = new boolean[height][width];
  }
  
  //goes through every pixel of image; if luminance of pixel is greater than threshold,
  //change that pixel's spot in toVist to true
  private void populate(){
    //i is row; j  is col
    for (int i = 0; i < height; i++){ // check not <=
      for (int j = 0; j < width; j++){
        Color current = image.get(j, i);
          if (current.equals( Color.WHITE)){
          toVisit[i][j] = true;
        }
      }
    }
  }
  
  //recursively find blobs of pixels for notes
  private void dfs (int row, int column, Note note){
    if (column <= 0 || row <= 0){
      return;
    }
    if (row >= height || column >= width){
      return;
    }
    if (toVisit[row][column] == false){
      return;
    }
    note.add(column, row);
    toVisit[row][column] = false;
    dfs(row+1, column, note);
    dfs(row-1, column, note);
    dfs(row, column-1, note);
    dfs(row, column+1, note);

  }
  //recursively finds blobs of pixels for line beginnings (i.e. focuses on left side of image)
  private void dfsL (int row, int column, Note note){
    if (column <= 0 || row <= 0){
      return;
    }
    if (row >= height || column >= width || column >= 5){
      return;
    }
    if (toVisit[row][column] == false){
      return;
    }
    note.add(column, row);
    toVisit[row][column] = false;
    dfsL(row+1, column, note);
    dfsL(row-1, column, note);
    dfsL(row, column-1, note);
    dfsL(row, column+1, note);

  }  
  
  //given a minimum blob size, and boundaries for y in which to find blobs,
  //returns an array of blobs (usually notes but sometimes line beginnings)
  public Note[] getBeads (int P, int minY, int maxY){
    populate();
    int size=0;
    int pixels=0;
    //creates a stack of notes which will later be turned into an array
    Stack<Note> bStack = new Stack<Note>();
    //i is row; j is column
    for (int i = minY; i < maxY; i++){ 
      for (int j = 0; j < width; j++){
        if (toVisit[i][j] == true){
          pixels++;
          Note temp = new Note();
          dfs(i, j, temp);
          if (temp.mass >= P){
            bStack.push(temp);
            size++;
          }
        }
      }
    }
   Note[] array;
   array = new Note[size];
   bStack.toArray(array);
   return array;
  }
  
  //similar to getBeads but specifically for lines
  public Note[] getLines(){
    populate();
    int size=0;
    int pixels=0;
    Stack<Note> bStack = new Stack<Note>();
    //i is row; j is column
    for (int i = 0; i < height; i++){
      for (int j = 0; j < 10; j++){
        if (toVisit[i][j] == true){
          pixels++;
          Note temp = new Note();
          dfsL(i, j, temp);
          if (temp.mass >= 1){
            bStack.push(temp);
            size++;
          }
        }
      }
    }
   Note[] array;
   array = new Note[size];
   bStack.toArray(array);
   return array;
  }
}