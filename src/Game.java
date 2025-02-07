
import java.awt.*;
import java.awt.event.KeyEvent;
import java.util.ArrayList;

import java.awt.*;


import game2D.*;

// Game demonstrates how we can override the GameCore class
// to create our own 'game'. We usually need to implement at
// least 'draw' and 'update' (not including any local event handling)
// to begin the process. You should also add code to the 'init'
// method that will initialise event handlers etc. 

// Student ID: ???????


@SuppressWarnings("serial")


public class Game extends GameCore 
{
	// Useful game constants
	static int screenWidth = 1000;
	static int screenHeight = 500;

	// Game constants
    float 	lift = 0.005f;
    float	gravity = 0.0001f;
    float	fly = -0.04f;
    float	moveSpeed = 0.05f;
    
    // Game state flags
    boolean flap = false;
    boolean moveRight = false;

    boolean dead = false;
    boolean moveLeft = false;
    boolean debug = false;
    boolean collision = false;

    // Game resources
    Animation marinerun;
    Animation marinestanding;
    Animation marinedie;

    Sprite Background1 = null;
    Sprite Background2 = null;
    Sprite Background3 = null;
    Sprite Background4 = null;
    Sprite Background5 = null;
    Sprite Background6 = null;
    Sprite Background7 = null;

    
    Sprite	player = null;
    ArrayList<Sprite> 	clouds = new ArrayList<Sprite>();
    ArrayList<Tile>		collidedTiles = new ArrayList<Tile>();


    TileMap tmap = new TileMap();	// Our tile map, note that we load it in init()
    
    long total;         			// The score will be the total time elapsed since a crash


    /**
	 * The obligatory main method that creates
     * an instance of our class and starts it running
     * 
     * @param args	The list of parameters this program might use (ignored)
     */
    public static void main(String[] args) {

        Game gct = new Game();
        gct.init();
        // Start in windowed mode with the given screen height and width
        gct.run(false,screenWidth,screenHeight);
    }

    /**
     * Initialise the class, e.g. set up variables, load images,
     * create animations, register event handlers.
     * 
     * This shows you the general principles but you should create specific
     * methods for setting up your game that can be called again when you wish to 
     * restart the game (for example you may only want to load animations once
     * but you could reset the positions of sprites each time you restart the game).
     */
    public void init()
    {         
        Sprite s;	// Temporary reference to a sprite

        // Load the tile map and print it out so we can check it is valid
        tmap.loadMap("maps", "map.txt");
        
        setSize(tmap.getPixelWidth()/4, tmap.getPixelHeight());
        setVisible(true);

        // Create a set of background sprites that we can 
        // rearrange to give the illusion of motion
        
        marinerun = new Animation();
        marinerun.loadAnimationFromSheet("images/move with FX.png", 1, 8, 150);

        marinestanding = new Animation();
        marinestanding.loadAnimationFromSheet("images/static idle.png",1,1,150);

        marinedie =new Animation();
        marinedie.loadAnimationFromSheet("images/marinedead.png",7,1,150);
        
        // Initialise the player with an animation
        player = new Sprite(marinestanding);
        
        // Load a single cloud animation
        Animation ca = new Animation();
        ca.addFrame(loadImage("images/cloud.png"), 1000);


        
        // Create 3 clouds at random positions off the screen
        // to the right
        for (int c=0; c<3; c++)
        {
        	s = new Sprite(ca);
        	s.setX(screenWidth + (int)(Math.random()*200.0f));
        	s.setY(30 + (int)(Math.random()*150.0f));
        	s.setVelocityX(-0.02f);
        	s.show();
        	clouds.add(s);
        }
        Animation backgrownd1 = new Animation();
        Animation backgrownd2 = new Animation();
        Animation backgrownd3 = new Animation();
        Animation backgrownd4 = new Animation();
        Animation backgrownd5 = new Animation();



        //Paralax bg
        backgrownd1.addFrame(loadImage("images/Paralaxbg/1.png"), 1000);
        backgrownd2.addFrame(loadImage("images/Paralaxbg/2.png"), 1000);
        backgrownd3.addFrame(loadImage("images/Paralaxbg/3.png"), 1000);
        backgrownd4.addFrame(loadImage("images/Paralaxbg/4.png"), 1000);
        backgrownd5.addFrame(loadImage("images/Paralaxbg/5.png"), 1000);




        Background1 = new Sprite(backgrownd1);

        //Background.setVelocityX(player.getVelocityX());
        Background1.setScale(0.5f,0.45f);
        Background1.setPosition(0,0);
        Background1.show();
        Background2 = new Sprite(backgrownd2);

        Background2.setVelocityX(-player.getVelocityX()*1.5f);
        Background2.setScale(0.5f,0.45f);
        Background2.setPosition(0,0);
        Background2.show();
        Background3 = new Sprite(backgrownd3);

        //Background.setVelocityX(player.getVelocityX());
        Background3.setScale(0.5f,0.45f);
        Background3.setPosition(0,0);
        Background3.show();
        Background4 = new Sprite(backgrownd4);

        //Background.setVelocityX(player.getVelocityX());
        Background4.setScale(0.5f,0.45f);
        Background4.setPosition(0,0);
        Background4.show();
        Background5 = new Sprite(backgrownd5);
        //Background.setVelocityX(player.getVelocityX());
        Background5.setScale(0.5f,0.45f);
        Background5.setPosition(0,0);
        Background5.show();


        initialiseGame();

      		
        System.out.println(tmap);
    }

    /**
     * You will probably want to put code to restart a game in
     * a separate method so that you can call it when restarting
     * the game when the player loses.
     */
    public void initialiseGame()
    {
    	total = 0;
    	      
        player.setPosition(150,300);
        player.setVelocity(0,0);
        player.show();
    }
    
    /**
     * Draw the current state of the game. Note the sample use of
     * debugging output that is drawn directly to the game screen.
     */
    public void draw(Graphics2D g)
    {    	
    	// Be careful about the order in which you draw objects - you
    	// should draw the background first, then work your way 'forward'

    	// First work out how much we need to shift the view in order to
    	// see where the player is. To do this, we adjust the offset so that
        // it is relative to the player's position along with a shift
        int xo = -(int)player.getX() + 200;
        int yo = -(int)player.getY() + 200;

        g.setColor(Color.white);
        g.fillRect(0, 0, getWidth(), getHeight());

        Background1.setOffsets(xo-50,yo);
        Background1.drawTransformed(g);
        Background2.setOffsets(xo-50,yo);
        Background2.drawTransformed(g);
        Background3.setOffsets(xo-50,yo);
        Background3.drawTransformed(g);
        Background4.setOffsets(xo-50,yo);
        Background4.drawTransformed(g);
        Background5.setOffsets(xo-50,yo);
        Background5.drawTransformed(g);


        // Apply offsets to sprites then draw them
        for (Sprite s: clouds)
        {
        	s.setOffsets(xo,yo);
        	s.draw(g);
        }


        // Apply offsets to tile map and draw  it
        tmap.draw(g,xo,yo);



        // Apply offsets to player and draw 
        player.setOffsets(xo, yo);
        player.drawTransformed(g);

        
        // Show score and status information
        String msg = String.format("Score: %d", total/100);
        g.setColor(Color.darkGray);
        g.drawString(msg, getWidth() - 100, 50);
        
        if (debug)
        {

        	// When in debug mode, you could draw borders around objects
            // and write messages to the screen with useful information.
            // Try to avoid printing to the console since it will produce 
            // a lot of output and slow down your game.
            tmap.drawBorder(g, xo, yo, Color.black);

            g.setColor(Color.red);
        	player.drawBoundingBox(g);
        
        	g.drawString(String.format("Player: %.0f,%.0f", player.getX(),player.getY()),
        								getWidth() - 100, 70);
        	
        	drawCollidedTiles(g, tmap, xo, yo);
        }

    }

    public void drawCollidedTiles(Graphics2D g, TileMap map, int xOffset, int yOffset)
    {
		if (collidedTiles.size() > 0)
		{	
			int tileWidth = map.getTileWidth();
			int tileHeight = map.getTileHeight();
			
			g.setColor(Color.blue);
			for (Tile t : collidedTiles)
			{
				g.drawRect(t.getXC()+xOffset, t.getYC()+yOffset, tileWidth, tileHeight);
			}
		}
    }
	
    /**
     * Update any sprites and check for collisions
     * 
     * @param elapsed The elapsed time between this call and the previous call of elapsed
     */    
    public void update(long elapsed)
    {
    	
        // Make adjustments to the speed of the sprite due to gravity
        if(!collision) {
            player.setVelocityY(player.getVelocityY() + (gravity * elapsed));
        }
       	player.setAnimationSpeed(1.0f);
        player.setAnimation(marinestanding);

       	if(dead){
            player.setAnimation(marinedie);
        }


       	if (flap) 
       	{
            player.setAnimation(marinedie);
       		player.setVelocityY(fly);
       	}

        if (moveRight && !moveLeft) {
            player.setVelocityX(moveSpeed);
            player.setScale(1.0f, 1f);
            player.setAnimation(marinerun);
        }
        else if (moveLeft && !moveRight) {
            player.setVelocityX(-moveSpeed);
            player.setScale(-1.0f, 1f);
            player.setAnimation(marinerun);
        }
        else {
            player.setVelocityX(0);
        }




        for (Sprite s: clouds)
       		s.update(elapsed);
       	
        // Now update the sprites animation and position
        player.update(elapsed);

        Background1.setVelocityX(-player.getVelocityX()*0.05f);
        Background2.setVelocityX(-player.getVelocityX()*0.1f);
        Background3.setVelocityX(-player.getVelocityX()*0.08f);
        Background4.setVelocityX(-player.getVelocityX()*0.2f);
        Background5.setVelocityX(-player.getVelocityX()*0.2f);

        Background1.update(elapsed);
        Background2.update(elapsed);
        Background3.update(elapsed);
        Background4.update(elapsed);
        Background5.update(elapsed);


       
        // Then check for any collisions that may have occurred
        handleScreenEdge(player, tmap, elapsed);
        checkTileCollision(player, tmap);
    }
    
    
    /**
     * Checks and handles collisions with the edge of the screen. You should generally
     * use tile map collisions to prevent the player leaving the game area. This method
     * is only included as a temporary measure until you have properly developed your
     * tile maps.
     * 
     * @param s			The Sprite to check collisions for
     * @param tmap		The tile map to check 
     * @param elapsed	How much time has gone by since the last call
     */
    public void handleScreenEdge(Sprite s, TileMap tmap, long elapsed)
    {
    	// This method just checks if the sprite has gone off the bottom screen.
    	// Ideally you should use tile collision instead of this approach
    	
    	float difference = s.getY() + s.getHeight() - tmap.getPixelHeight();
        if (difference > 0)
        {
        	// Put the player back on the map according to how far over they were
        	s.setY(tmap.getPixelHeight() - s.getHeight() - (int)(difference)); 
        	
        	// and make them bounce
        	s.setVelocityY(-s.getVelocityY()*0.75f);
        }
    }
    
    
     
    /**
     * Override of the keyPressed event defined in GameCore to catch our
     * own events
     * 
     *  @param e The event that has been generated
     */
    public void keyPressed(KeyEvent e) 
    { 
    	int key = e.getKeyCode();
    	
		switch (key)
		{
			case KeyEvent.VK_UP     : flap = true; break;
			case KeyEvent.VK_RIGHT  : moveRight = true; break;
            case KeyEvent.VK_LEFT   : moveLeft= true;  break;
			case KeyEvent.VK_S 		: Sound s = new Sound("sounds/caw.wav"); 
									  s.start();
									  break;
			case KeyEvent.VK_ESCAPE : stop(); break;
			case KeyEvent.VK_B 		: debug = !debug; break; // Flip the debug state
			default :  break;
		}
    
    }

    /** Use the sample code in the lecture notes to properly detect
     * a bounding box collision between sprites s1 and s2.
     * 
     * @return	true if a collision may have occurred, false if it has not.
     */
    public boolean boundingBoxCollision(Sprite s1, Sprite s2)
    {
    	return false;   	
    }
    
    /**
     * Check and handles collisions with a tile map for the
     * given sprite 's'. Initial functionality is limited...
     * 
     * @param s			The Sprite to check collisions for
     * @param tmap		The tile map to check 
     */
    public void checkTileCollision(Sprite s, TileMap tmap) {
        float sLeftDX = s.getX() + 50; // Shrink collision box
        float sRightDX = s.getX() + s.getWidth() - 20;
        float sTopDy = s.getY() + 20;
        float sBottomDY = s.getY() + s.getHeight();

        int leftCol = (int) (sLeftDX / tmap.getTileWidth());
        int rightCol = (int) (sRightDX / tmap.getTileWidth());
        int topRow = (int) (sTopDy / tmap.getTileHeight());
        int bottomRow = (int) (sBottomDY / tmap.getTileHeight());

        // Ensure character is not inside a solid tile
        while (isTileSolid(tmap, leftCol, bottomRow) || isTileSolid(tmap, rightCol, bottomRow)) {
            s.setY(s.getY() - 1); // Push up until outside the solid block
            bottomRow = (int) ((s.getY() + s.getHeight()) / tmap.getTileHeight());
        }

        // Check vertical collision (Up and Down)
        if (s.getVelocityY() < 0) { // Moving Up
            int newTopRow = (int) ((sTopDy + s.getVelocityY()) / tmap.getTileHeight());
            if (isTileSolid(tmap, leftCol, newTopRow) || isTileSolid(tmap, rightCol, newTopRow)) {
                s.setVelocityY(0.0f);
                s.setY((newTopRow + 1) * tmap.getTileHeight()); // Adjust position
            }
        } else if (s.getVelocityY() > 0) { // Moving Down
            int newBottomRow = (int) ((sBottomDY + s.getVelocityY()) / tmap.getTileHeight());
            if (isTileSolid(tmap, leftCol, newBottomRow) || isTileSolid(tmap, rightCol, newBottomRow)) {
                s.setVelocityY(0.0f);
                s.setY(newBottomRow * tmap.getTileHeight() - s.getHeight()); // Adjust position
            }
        }

        // Check horizontal collision (Left and Right)
        if (s.getVelocityX() < 0) { // Moving Left
            int newLeftCol = (int) ((sLeftDX + s.getVelocityX()) / tmap.getTileWidth());
            if (isTileSolid(tmap, newLeftCol, topRow) || isTileSolid(tmap, newLeftCol, bottomRow)) {
                s.setVelocityX(0.0f);
                s.setX((newLeftCol + 1) * tmap.getTileWidth()); // Adjust position
            }
        } else if (s.getVelocityX() > 0) { // Moving Right
            int newRightCol = (int) ((sRightDX + s.getVelocityX()) / tmap.getTileWidth());
            if (!isTileSolid(tmap, newRightCol, topRow) && !isTileSolid(tmap, newRightCol, bottomRow)) {
                s.setX(s.getX() + s.getVelocityX()); // Allow movement if not blocked
            } else {
                s.setVelocityX(0.0f);
                s.setX(newRightCol * tmap.getTileWidth() - s.getWidth()); // Adjust position
            }
        }
    }

    private boolean isTileSolid(TileMap tmap, int col, int row) {
        Tile tile = tmap.getTile(col, row);
        return tile != null && tile.getCharacter() != '.';
    }


    public void keyReleased(KeyEvent e) {

		int key = e.getKeyCode();

		switch (key)
		{
			case KeyEvent.VK_ESCAPE : stop(); break;
			case KeyEvent.VK_UP     : flap = false; break;
			case KeyEvent.VK_RIGHT  : moveRight = false; break;
            case KeyEvent.VK_LEFT   : moveLeft = false; break;
			default :  break;
		}
	}
}
