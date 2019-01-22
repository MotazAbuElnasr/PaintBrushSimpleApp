/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mainpkg;

import java.applet.Applet;
import java.awt.Button;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.image.RenderedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import javax.imageio.ImageIO;

/**
 *
 * @author Motaz
 */
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */



abstract class Shape {
    protected int startingX, endingX, startingY, endingY; // Starting and ending of shape
    protected boolean isFilled;
    protected String type; // to check the type of drawn shape
    protected Color currentColor;
    protected int currentNum;
    protected Shape(int x1, int y1 , int x2, int y2, Color c)

    {
       startingX = x1;
       startingY = y1;
       endingX = x2;
       endingY = y2;
       currentColor = c; //The color of shape
    }
    public int getWidth(){
        return Math.abs(startingX-endingX);
    }
    public int getHeight(){
        return Math.abs(startingY-endingY);
    }

}

class Rectangle extends Shape{
    public Rectangle(int x1, int y1 , int x2, int y2, Color c,boolean filled, String t)
    {   
        super(x1, y1, x2, y2, c);

        isFilled = filled;
        type = t; // it can be rectangle, eraser
    }
    public Rectangle(int x1, int y1 , int x2, int y2, Color c,boolean filled, String t,int currentNum)
    {   
        super(x1, y1, x2, y2, c);
        this.currentNum = currentNum;
        isFilled = filled;
        type = t;
    }
}

class Oval extends Shape{
    public Oval (int x1, int y1 , int x2, int y2, Color c,boolean filled)
    {
        super(x1, y1, x2, y2, c);
        isFilled = filled;
    }
}

class Line extends Shape{
    public Line (int x1, int y1 , int x2, int y2, Color c,String t)
    {
        super(x1, y1, x2, y2, c);
        type = t;
    }
    public Line (int x1, int y1 , int x2, int y2, Color c,String t, int currentNum)
    {
        super(x1, y1, x2, y2, c);
        type = t;
        this.currentNum = currentNum;
    }}

public class PaintBrush extends Applet {
    Image offscreenImage; // those are used to clear the blinking of screen
    Graphics offscreenGraphics;
    int oldX, oldY; // to save the coordinates of point that we started drawing at
    Color currentColor;
    String currentShape;
    boolean isFill;
    boolean startDrawing;
    Rectangle currentRectangle;
    Oval currentOval;
    Line currentLine;
    Shape lastDrawnShape;
    Shape lastRemovedShape;
    ArrayList<Shape> shapes = new ArrayList<>();
    ArrayList<Shape> undoedShapes = new ArrayList<>();
    Image img;
    int ereaserNum;
    int freelineNum;


    @Override
        public void init() {
        setSize(1970, 980);
        offscreenImage = createImage(getWidth(), getHeight());
        offscreenGraphics = offscreenImage.getGraphics();
        File outputfile = new File("./build/image.jpg");
        //Default values 
        currentColor = Color.black;
        currentShape = "line";
        isFill = false;
        startDrawing = false;
        ereaserNum = 0;
        freelineNum = 0;
        // 
            this.addMouseListener(new MouseListener(){
            @Override
            public void mousePressed(MouseEvent e) { // It create the object and save the starting coordinates 
                startDrawing = true;
                oldX = e.getX();
                oldY = e.getY();
                undoedShapes.clear();
                switch(currentShape){
                    case "rect" :
                        currentRectangle = new Rectangle(e.getX(), e.getY(), 1, 1, currentColor, isFill , "Rectangle");
                        shapes.add(currentRectangle);
                        break;
                    case "oval" :
                        currentOval = new Oval(e.getX(), e.getY(), 1, 1, currentColor, isFill);
                        shapes.add(currentOval);
                        break;
                    case "line" :
                        currentLine = new Line(e.getX(), e.getY(), 1, 1, currentColor,"line");
                        shapes.add(currentLine);
                        break;
                    case "eraser" :
                        ereaserNum++;
                        shapes.add(new Rectangle(e.getX(), e.getY(),e.getX()+7,e.getY()+7,Color.white,true,"Eraser",ereaserNum));
                       break;
                    case "freeHand":
                        freelineNum++;
                        shapes.add(new Line(e.getX(), e.getY(),e.getX(),e.getY(),currentColor,"Freehand",freelineNum));                           
                        break;  
                        }
            }          
            @Override
            public void mouseReleased(MouseEvent e) {
                paintShape(e, currentShape);
            }
            
            @Override
            public void mouseClicked(MouseEvent e) {
            }
            @Override
            public void mouseEntered(MouseEvent e) {
            }
            @Override
            public void mouseExited(MouseEvent e) {
            }
        });
        
        this.addMouseMotionListener(new MouseMotionListener() {

            @Override
            public void mouseDragged(MouseEvent e) {
                paintShape(e,currentShape);                                 
                repaint();
            }
            @Override
            public void mouseMoved(MouseEvent e) {
            }
        });
        //---------------------Buttons--------------------//
        Button rectButton= new Button("Rect");
        rectButton.addActionListener(
        new ActionListener(){
            public void actionPerformed(ActionEvent e){
                currentShape = "rect";
            }
        });
        
        Button ovalButton= new Button("Oval");
        ovalButton.addActionListener(
        new ActionListener(){
            public void actionPerformed(ActionEvent e){
                currentShape = "oval";
            }
        });
        
        Button lineButton= new Button("Line");
        lineButton.addActionListener(
        new ActionListener(){
            public void actionPerformed(ActionEvent e){
                currentShape = "line";
            }
        });
        
        Button eraserButton= new Button("Eraser");
        eraserButton.addActionListener(
        new ActionListener(){
            public void actionPerformed(ActionEvent e){
                currentShape = "eraser";
            }
        });

        Button freeHandButton= new Button("Free hand");
        freeHandButton.addActionListener(
        new ActionListener(){
            public void actionPerformed(ActionEvent e){
                currentShape = "freeHand";
            }
        });

        Button fillButton= new Button("Fill");
        fillButton.addActionListener(
        new ActionListener(){
            public void actionPerformed(ActionEvent e){
                isFill = !isFill;
                if(isFill){
                    fillButton.setLabel("unfill");
                }
                else{
                    fillButton.setLabel("fill");
                    
                }
            }
        });
        
        Button greenButton= new Button();
        greenButton.addActionListener(
        new ActionListener(){
            public void actionPerformed(ActionEvent e){
                currentColor = Color.green;
            }
        });

        Button redButton= new Button();
        redButton.addActionListener(
        new ActionListener(){
            public void actionPerformed(ActionEvent e){
                currentColor = Color.red;
            }
        });
        
        Button blueButton= new Button();
        blueButton.addActionListener(
        new ActionListener(){
            public void actionPerformed(ActionEvent e){
                currentColor = Color.blue;
            }
        });
        
        Button blackButton= new Button();
        blackButton.addActionListener(
        new ActionListener(){
            public void actionPerformed(ActionEvent e){
                currentColor = Color.black;
            }
        });
        
        Button clearButton= new Button("Clear");
        clearButton.addActionListener(
        new ActionListener(){
            public void actionPerformed(ActionEvent e){
                shapes.add(new Rectangle(1, 1, 2000, 2000, Color.WHITE, true, "Clear"));
                repaint();
            }
        });
        
        Button undoButton= new Button("Undo");
        undoButton.addActionListener(
        new ActionListener(){
            public void actionPerformed(ActionEvent e){
                if(shapes.size()>0){
                    int lastIndex = shapes.size()-1;
                    lastDrawnShape = shapes.get(lastIndex);
                    //undo shapes
                    if((lastDrawnShape instanceof Rectangle && "Rectangle".equals(lastDrawnShape.type))||lastDrawnShape instanceof Line || lastDrawnShape instanceof Oval){
                        undoedShapes.add(lastDrawnShape);
                        shapes.remove(lastIndex);
                    }
                    // Undo ereaser and freehand
                    else if ("Clear".equals(lastDrawnShape.type)) {
                        do{
                             
                            undoedShapes.add(shapes.get(lastIndex));
                            shapes.remove(lastIndex);
                            lastIndex --;
                        }while(lastIndex>=0&&"Clear".equals(shapes.get(lastIndex).type));
                    }
                    else if (lastDrawnShape instanceof Line && "Freehand".equals(lastDrawnShape.type)){
                        
                        int lastCheckNum = shapes.get(lastIndex).currentNum;
                        do{
                            undoedShapes.add(shapes.get(lastIndex));
                            shapes.remove(lastIndex);
                            lastIndex --;                        
                        }while(lastIndex>=0&&"Freehand".equals(shapes.get(lastIndex).type)&&shapes.get(lastIndex).currentNum==lastCheckNum);                        
                    }
                    else{
                        int EreaserNum = shapes.get(lastIndex).currentNum;
                        do{
                            undoedShapes.add(shapes.get(lastIndex));
                            shapes.remove(lastIndex);
                            lastIndex --;     
                        }while(lastIndex>=0&&"Eraser".equals(shapes.get(lastIndex).type)&&shapes.get(lastIndex).currentNum==EreaserNum);                        
                    }
                repaint();                         
                }
                }
            });
        Button redoButton= new Button("Redo");
        
        redoButton.addActionListener(
        new ActionListener(){
            public void actionPerformed(ActionEvent e){
                if(undoedShapes.size()>0){
                    int lastIndex = undoedShapes.size()-1;
                    lastRemovedShape = undoedShapes.get(lastIndex);
                    if((lastDrawnShape instanceof Rectangle && "Rectangle".equals(lastDrawnShape.type))||lastDrawnShape instanceof Line || lastDrawnShape instanceof Oval){
                        shapes.add(lastRemovedShape);
                        undoedShapes.remove(lastIndex);
                    } 
                        
                    // Undo ereaser and freehand
                    else if ("Clear".equals(lastRemovedShape.type)) {
                        do{
                            shapes.add(undoedShapes.get(lastIndex));
                            undoedShapes.remove(lastIndex);
                            lastIndex--;
                        }while(lastIndex>=0&&"Clear".equals(undoedShapes.get(lastIndex).type));
                    }
                     else if (lastDrawnShape instanceof Line && "Freehand".equals(lastDrawnShape.type)){
                        
                        int lastCheckNum = shapes.get(lastIndex).currentNum;
                        do{
                            shapes.add(undoedShapes.get(lastIndex));
                            undoedShapes.remove(lastIndex);
                            lastIndex --;                        
                        }while(lastIndex>=0&&"Freehand".equals(undoedShapes.get(lastIndex).type)&&undoedShapes.get(lastIndex).currentNum==lastCheckNum);                        
                    }
                    else{
                        int lastCheckNum = undoedShapes.get(lastIndex).currentNum;
                        do{
                            shapes.add(undoedShapes.get(lastIndex));
                            undoedShapes.remove(lastIndex);
                            lastIndex--;
                           
                        }while(lastIndex>=0&&"Eraser".equals(undoedShapes.get(lastIndex).type)&&undoedShapes.get(lastIndex).currentNum==lastCheckNum);                        
                    }
                }
                repaint();  
                }
            });
        //save
        Button saveButton = new Button("save");
        saveButton.addActionListener(
        new ActionListener(){
            public void actionPerformed(ActionEvent e){
                try {
                ImageIO.write((RenderedImage) offscreenImage, "jpg", outputfile);

                } catch (IOException ex) {
                    System.out.println("file is not accessable");
                }
            }
        });
        
        Button loadButton = new Button("Load");
        loadButton.addActionListener(
        new ActionListener(){
            public void actionPerformed(ActionEvent e){
            undoedShapes.clear();
            shapes.clear();
            img = getImage(getDocumentBase(),"image.jpg"); 
            repaint();
            }
        });
        
        greenButton.setPreferredSize(new Dimension(30, 30));
        blueButton.setPreferredSize(new Dimension(30, 30));
        blackButton.setPreferredSize(new Dimension(30, 30));
        redButton.setPreferredSize(new Dimension(30, 30));
        lineButton.setPreferredSize(new Dimension(50, 30));
        rectButton.setPreferredSize(new Dimension(50, 30));
        ovalButton.setPreferredSize(new Dimension(50, 30));
        redoButton.setPreferredSize(new Dimension(50, 30));
        undoButton.setPreferredSize(new Dimension(50, 30));
        eraserButton.setPreferredSize(new Dimension(50, 30));
        freeHandButton.setPreferredSize(new Dimension(80, 30));
        fillButton.setPreferredSize(new Dimension(50, 30));
        clearButton.setPreferredSize(new Dimension(50, 30));
        saveButton.setPreferredSize(new Dimension(50, 30));
        loadButton.setPreferredSize(new Dimension(50, 30));
        
        greenButton.setBackground(Color.green);
        redButton.setBackground(Color.red);
        blackButton.setBackground(Color.black);
        blueButton.setBackground(Color.blue);

        add(rectButton);
        add(lineButton);
        add(ovalButton);
        add(freeHandButton);
        add(fillButton);
        add(redButton);
        add(blackButton);
        add(blueButton);
        add(greenButton);
        add(undoButton);
        add(redoButton);
        add(eraserButton);
        add(clearButton);
        add(saveButton);
        add(loadButton);
    }
    // TODO overwrite start(), stop() and destroy() methods
    @Override
    public void paint(Graphics g){ 
        offscreenGraphics.clearRect(0, 0, getWidth(), getHeight());
        offscreenGraphics.drawImage(img, 0, 0, this);
        int i = 0;
        Shape toBeDrawn;
        if(!shapes.isEmpty()){
        do{
            toBeDrawn = shapes.get(i);
            offscreenGraphics.setColor(toBeDrawn.currentColor);
            if(!startDrawing)
                break;
            if(shapes.get(i) instanceof Rectangle){            
                if(toBeDrawn.isFilled){
                    offscreenGraphics.fillRect(toBeDrawn.startingX, toBeDrawn.startingY, toBeDrawn.getWidth(), toBeDrawn.getHeight());
                }
                else{
                    offscreenGraphics.drawRect(toBeDrawn.startingX, toBeDrawn.startingY, toBeDrawn.getWidth(), toBeDrawn.getHeight());                    
                }
            }
            else if(shapes.get(i) instanceof Oval){            
                if(toBeDrawn.isFilled){
                    offscreenGraphics.fillOval(toBeDrawn.startingX, toBeDrawn.startingY, toBeDrawn.getWidth(), toBeDrawn.getHeight());
                }
                else{
                    offscreenGraphics.drawOval(toBeDrawn.startingX, toBeDrawn.startingY, toBeDrawn.getWidth(), toBeDrawn.getHeight());                    
                }
            }
            else{
                offscreenGraphics.drawLine(toBeDrawn.startingX, toBeDrawn.startingY, toBeDrawn.endingX, toBeDrawn.endingY);
            }
            i++;
        }while(i<shapes.size());
    }
        g.drawImage(offscreenImage, 0, 0, this);      
    }
    @Override
    public void update(Graphics g) {
    paint(g);
    }
    //Private methods
    private void checkMode(MouseEvent e,Shape shape){
        if(e.getX()>oldX&&e.getY()>oldY){ //final point at bottom right
            shape.endingX = e.getX();
            shape.endingY = e.getY();                    
        }
        else if (e.getX()<oldX && e.getY()<oldY) { //final point at upper Left
            shape.startingX = e.getX();
            shape.startingY = e.getY();      
            shape.endingX = oldX;
            shape.endingY = oldY;
        } 
        else if (e.getX()>oldX && e.getY()< oldY){ //final point at upper right
            shape.startingY = e.getY();   
            shape.endingX = e.getX();
            shape.endingY = oldY;
        } 
        else  { //final point at bottom Left
            shape.startingX = e.getX();
            shape.endingX = oldX;
            shape.endingY = e.getY();
        }  
    }
    private void drawingLine(MouseEvent e,Line line){
        line.endingX = e.getX();
        line.endingY = e.getY();
        }  
    private void paintShape (MouseEvent e,String currentShape) {
        switch (currentShape){
            case "rect" :
                checkMode(e,currentRectangle);
                break;
            case "oval" :
                checkMode(e,currentOval);
                break;
            case "line" :
                drawingLine(e,currentLine);
                break;
            case "eraser" :
                shapes.add(new Rectangle(e.getX(), e.getY(),e.getX()+7,e.getY()+7,Color.white,true,"Eraser",ereaserNum));
               break;
            case "freeHand":
                shapes.add(new Line(oldX, oldY,e.getX(),e.getY(),currentColor,"Freehand",freelineNum));  
                oldX = e.getX();
                oldY=e.getY();
                break;  
                
        }
    }
}

