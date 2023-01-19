package com.opengl.shootdacube;

import com.jogamp.opengl.GL2;
import com.jogamp.opengl.GLAutoDrawable;
import com.jogamp.opengl.GLEventListener;
import com.jogamp.opengl.awt.GLCanvas;
import com.jogamp.opengl.glu.GLU;
import com.jogamp.opengl.util.FPSAnimator;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.Arrays;

public class ShootDaCube extends GLCanvas implements GLEventListener, MouseListener {

    private static final long serialVersionUID = 1L;
    // data used to draw the cubes
    private ArrayList<float[]> colors;
    private ArrayList<Double> offsets;
    // cube's side length inside the 3D space
    private double cubeLen = 0.2; 
    // used to keep track of click events
    private boolean clicked = false;
    private int clickX;
    private int clickY;
    

    public ShootDaCube() {
        this.addGLEventListener(this);
        this.addMouseListener(this);
        // convert hard-coded data arrays into array lists
        // which makes it easier to remove elements
        
        // cubes positioning data
        double[] offsetArray = { -0.9, -0.6, -0.3, 0.0, 0.3, 0.6 };
        offsets = new ArrayList<>();
        for(double offset : offsetArray) {
        	offsets.add(offset);
        }
        // cubes color data
        float[][] colorsArray = {
        		{ 41, 98, 255 },
        		{ 170, 0, 250 },
        		{ 22, 179, 100 },
        		{ 41, 98, 255 },
        		{ 170, 0, 250 },
        		{ 22, 179, 100 }
        };
        // same thing as above
        colors = new ArrayList<>();
        for(float[] color : colorsArray) {
        	colors.add(color);
        }
    }
    

    @Override
    public void init(GLAutoDrawable glAutoDrawable) {
        GL2 gl = glAutoDrawable.getGL().getGL2();
        gl.glClearColor(1, 1, 1, 1);
        // empty z-buffer
        gl.glClearDepth(1.0f);
        // depth test
        gl.glEnable(GL2.GL_DEPTH_TEST);
        // depth type
        gl.glDepthFunc(GL2.GL_LEQUAL);
        // perspective correction type
        gl.glHint(GL2.GL_PERSPECTIVE_CORRECTION_HINT, GL2.GL_NICEST);
        // print cubes screen coordinates
    	Point cube1 = getCubeTopLeftCoordinates(offsets.get(0));
    	Point cube2 = getCubeTopLeftCoordinates(offsets.get(1));
    	Point cube3 = getCubeTopLeftCoordinates(offsets.get(2));
    	System.out.println("First cube at (" + cube1.getX() + ", " + cube1.getY() + ")");
    	System.out.println("Second cube at (" + cube2.getX() + ", " + cube2.getY() + ")");
    	System.out.println("Third cube at (" + cube3.getX() + ", " + cube3.getY() + ")");
    }

    @Override
    public void dispose(GLAutoDrawable glAutoDrawable) {
    	// empty
    }
    

	// get the coordinates of the top-left point of a cube from its x-offset 
    private Point getCubeTopLeftCoordinates(double xOffset) {
    	// must convert from [-1; 1] bounds into screen size bounds
    	double x = (1 + xOffset) * this.getWidth() / 2;
    	double y = (1 - cubeLen) * this.getHeight() / 2;
    	return new Point((int) x, (int) y);
    }
    
    private boolean isInsideCube(Point coords, double xOffset) {
    	// get cube bounds
    	Point cubeTopLeft = getCubeTopLeftCoordinates(xOffset);
    	// get the cube's real side length (on screen)
    	double screenCubeLen = cubeLen * this.getWidth() / 2;
    	// compute the cube's bounds on the x & y axis
    	double xMin = cubeTopLeft.getX();
    	double xMax = xMin + screenCubeLen;
    	double yMin = cubeTopLeft.getY();
    	double yMax = yMin + screenCubeLen;
    	// check if `click` is inside the bounds
    	double x = coords.getX(), y = coords.getY();
    	return (x >= xMin && x <= xMax) && (y >= yMin && y <= yMax);
    }

    @Override
    public void display(GLAutoDrawable glAutoDrawable) {
        GL2 gl = glAutoDrawable.getGL().getGL2();
        // Initialize states
        // Clear screen & depth buffer
        gl.glClear(GL2.GL_COLOR_BUFFER_BIT | GL2.GL_DEPTH_BUFFER_BIT);     
        gl.glEnable(GL2.GL_DEPTH_TEST);
        gl.glLoadIdentity();
        // draw each square from the provided x offset
    	for(int i = 0; i < offsets.size(); i++) {
            gl.glPushMatrix();
    		drawSquare(gl, offsets.get(i), colors.get(i)[0], colors.get(i)[1], colors.get(i)[2]);
    	}
    	// pop each matrix once done drawing
    	for(int i = 0; i < offsets.size(); i++) {
    		gl.glPopMatrix();
    	}
        gl.glFlush();
        // on click
        if(clicked) {
        	// figure out which cube was clicked on
        	for(int i = 0; i < offsets.size(); i++) {
        		if(isInsideCube(new Point(clickX, clickY), offsets.get(i))) {
        			// remove it once found
        			System.out.println("Cube number " + (i + 1) + " was clicked!");
        			offsets.remove(i);
        			colors.remove(i);
        		}
        	}
        	clicked = false;
        }
        
        // exit if there's no more cubes
        if(offsets.isEmpty()) {
        	System.out.println("You won !\nBye :)");
        	System.exit(0);
        }
    }
    
    // draw a square from its x-offset
    // and color it, conveniently in RGB

    public void drawSquare(GL2 gl, double xOffset, float r, float g, float b) {
        gl.glBegin(GL2.GL_QUADS);
        double len = cubeLen;
        // set the color
        // must bring the RGB values into [0, 1] bounds
        gl.glColor3f(r / 255f, g / 255f,  b / 255f);
        // Front
        gl.glVertex3d(xOffset, 0, 0);
        gl.glVertex3d(xOffset + len, 0, 0);
        gl.glVertex3d(xOffset + len, len, 0);
        gl.glVertex3d(xOffset, len, 0);
        // Back
        gl.glVertex3d(xOffset, 0, -len);
        gl.glVertex3d(xOffset + len, 0, -len);
        gl.glVertex3d(xOffset + len, len, -len);
        gl.glVertex3d(xOffset, len, -len);
        // Left
        gl.glVertex3d(xOffset, 0, -len);
        gl.glVertex3d(xOffset, 0, 0);
        gl.glVertex3d(xOffset, len,  0);
        gl.glVertex3d(xOffset, len, -len);
        // Right
        gl.glVertex3d(xOffset + len, 0, -len);
        gl.glVertex3d(xOffset + len, 0, 0);
        gl.glVertex3d(xOffset + len, len,  0);
        gl.glVertex3d(xOffset + len, len, -len);
        // Bottom
        gl.glVertex3d(xOffset, 0, 0);
        gl.glVertex3d(xOffset + len, 0, 0);
        gl.glVertex3d(xOffset + len, 0, -len);
        gl.glVertex3d(xOffset, 0, -len);
        // Up
        gl.glVertex3d(xOffset, len, 0);
        gl.glVertex3d(xOffset + len, len, 0);
        gl.glVertex3d(xOffset + len, len, -len);
        gl.glVertex3d(xOffset, len, -len);
        gl.glEnd();

    }



    @Override
    public void reshape(GLAutoDrawable glAutoDrawable, int x, int y, int width, int height) {
    	// empty
    }

    public static void main(String[] args) {
        GLCanvas canvas = new ShootDaCube();
        canvas.setPreferredSize(new Dimension(800, 600));
        // add animator to set the frame rate
        final FPSAnimator animator = new FPSAnimator(canvas, 300,true );
        // basic set up
        final JFrame frame = new JFrame();
        frame.getContentPane().add(canvas);
        frame.setTitle("ShootDaCube");
        frame.pack();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
        animator.start();
    }
    
    // react to mouse click

	@Override
	public void mouseClicked(MouseEvent e) {
		// we can't do anything to remove a cube from here
		// because we don't have access to the OpenGL context
		// but we save the screen coordinates of the mouse event
		// so we can compute that inside a function that has access to the context
		clicked = true;
		clickX = e.getX();
		clickY = e.getY();
	}
	
	// unused, but required, event handlers

	@Override
	public void mousePressed(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}
}