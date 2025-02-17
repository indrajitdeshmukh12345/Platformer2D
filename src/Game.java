
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

import java.awt.*;
import java.util.Timer;


import game2D.*;

import javax.swing.*;

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
    float	fly = -0.06f;
    float	moveSpeed = 0.05f;

    float projectileSpeed = 0.2f;
    
    // Game state flags
    boolean flap = false;
    boolean moveRight = false;
    boolean shoot = false;

    boolean dead = false;
    boolean moveLeft = false;

    boolean dash = false;
    boolean debug = false;
    boolean collision = false;
    boolean ideal =true;

    // Game resources
    Animation marinerun;
    Animation marinestanding;
    Animation marinedie;
    Animation marinedash;
    Animation marinewake;
    Animation marineshoot;
    Animation vilanrun;
    Animation vilanattack;
    Animation Projectile;
    Animation Villandeath;



    Sprite Background1 = null;
    Sprite Background2 = null;
    Sprite Background3 = null;
    Sprite Background4 = null;
    Sprite Background5 = null;
    Sprite Background6 = null;
    Sprite Background7 = null;

    
    Sprite	player = null;
    Sprite villan = null;
    Sprite projectile = null;
    ArrayList<Sprite> 	clouds = new ArrayList<Sprite>();
    ArrayList<Tile>		collidedTiles = new ArrayList<Tile>();


    TileMap tmap = new TileMap();	// Our tile map, note that we load it in init()
    
    long total;         			// The score will be the total time elapsed since a crash
    int healthPlayer = 100;


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

        marinedash = new Animation();
        marinedash.loadAnimationFromSheet("images/GAS dash with FX.png",1,7,110);

        marinewake = new Animation();
        marinewake.loadAnimationFromSheet("images/wake.png",1,5,150);

        marineshoot = new Animation();
        marineshoot.loadAnimationFromSheet("images/shoot with FX.png",1,4,150);

        vilanrun = new Animation();
        vilanrun.loadAnimationFromSheet("images/run.png",1,8,150);
        Villandeath = new Animation();
        Villandeath.loadAnimationFromSheet("images/death.png",1,5,150);
        Projectile = new Animation();
        Image animProjec = new ImageIcon("images/Projectile.png").getImage();
        Projectile.addFrame(animProjec,150);

        
        // Initialise the player with an animation
        player = new Sprite(marinestanding);
        // intialise the vilan with an animation
        villan = new Sprite(vilanrun);
        //initialise the projectile with an animation
        projectile=new Sprite(Projectile);
        
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
    	villan.setPosition(390,100);
        villan.setVelocity(-0.03f,0);
        villan.setAnimation(vilanrun);
        villan.activate();
        villan.show();
        player.setPosition(200,150);
        player.setVelocity(0,0);
        player.setAnimation(marinestanding);
        player.show();
        projectile.setPosition(player.getX()+5,player.getY());
        projectile.setAnimation(Projectile);
        projectile.setScale(0.5f);
        projectile.deactivate();
        projectile.show();


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

        g.setColor(Color.PINK);
        g.fillRect(0, 0, getWidth(), getHeight());

        Background1.setOffsets(xo-50,yo-100);
        Background1.drawTransformed(g);
        Background2.setOffsets(xo-50,yo-200);
        Background2.drawTransformed(g);
        Background3.setOffsets(xo-50,yo-200);
        Background3.drawTransformed(g);
        Background4.setOffsets(xo-50,yo-200);
        Background4.drawTransformed(g);
        Background5.setOffsets(xo-50,yo-200);
        Background5.drawTransformed(g);



        // Apply offsets to sprites then draw them
        for (Sprite s: clouds)
        {
        	s.setOffsets(xo,yo);
        	s.draw(g);
        }


        // Apply offsets to tile map and draw  it
        tmap.draw(g,xo,yo);

        villan.setOffsets(xo,yo);
        if(villan.isActive()){
            villan.drawTransformed(g);
        }


        // Apply offsets to player and draw
        player.setOffsets(xo, yo);
        player.drawTransformed(g);


        // Show score and status information
        String msg = String.format("Score: %d", total/100);
        String msghealth = String.format("Health: %d", healthPlayer);
        g.setColor(Color.red);
        g.drawString(msg, getWidth() - 100, 50);
        g.drawString(msghealth, getWidth() - 100, 90);

        if (debug)
        {

        	// When in debug mode, you could draw borders around objects
            // and write messages to the screen with useful information.
            // Try to avoid printing to the console since it will produce
            // a lot of output and slow down your game.
            tmap.drawBorder(g, xo, yo, Color.black);

            g.setColor(Color.red);
        	player.drawBoundingBox(g);
            villan.drawBoundingBox(g);
            projectile.drawBoundingBox(g);

        	g.drawString(String.format("Player: %.0f,%.0f", player.getX(),player.getY()),
        								getWidth() - 100, 70);

        	drawCollidedTiles(g, tmap, xo, yo);
        }
        if (projectile != null && projectile.isActive()) {
            projectile.setOffsets(xo, yo);
            projectile.drawTransformed(g);
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
        player.setVelocityY(player.getVelocityY() + (gravity * elapsed));
        villan.setVelocityY(0.02f);

        projectile.setVelocityY((gravity*elapsed));
        projectile.setVelocityX(projectileSpeed);

       	player.setAnimationSpeed(1.0f);

        if(ideal){
            player.setAnimation(marinestanding);
        }

       	if(dead){
            player.setAnimation(marinedie);
        }


       	if (flap) 
       	{
            if(collision) {
                player.setVelocityY(fly);
            }
           }

         if(dash){

               player.setAnimation(marinedash);
              // player.setAnimationSpeed(1f);

           }

        if(shoot){
            if(!moveRight && !moveLeft) {

                player.setAnimation(marineshoot);
                player.setAnimationFrame(0);


            }
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
        villan.update(elapsed);
        projectile.update(elapsed);

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
        handleScreenEdge(villan,tmap,elapsed);
        checkTileCollision(player, tmap);
        checkTileCollisionNPC2(villan,tmap);
        checkProjectileCollision(projectile,tmap);
        if (projectile.isActive()&&boundingBoxCollision(projectile,villan)){
            projectile.deactivate();
            villan.setAnimation(Villandeath);
            timer.schedule(new java.util.TimerTask() {
                @Override
                public void run() {
                    villan.deactivate();

                }
            }, 750);


        }
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
			default : ideal=true; break;
		}
    
    }

    /** Use the sample code in the lecture notes to properly detect
     * a bounding box collision between sprites s1 and s2.
     * 
     * @return	true if a collision may have occurred, false if it has not.
     */
    public boolean boundingBoxCollision(Sprite s1, Sprite s2)
    {
        return (s1.getX() + s1.getWidth() >= s2.getX()) && (s1.getX() <= s2.getX() + s2.getWidth()) &&
                (s1.getY() + s1.getHeight() >= s2.getY()) && (s1.getY() <= s2.getY() + s2.getHeight());
    }
    
    /**
     * Check and handles collisions with a tile map for the
     * given sprite 's'. Initial functionality is limited...
     * 
     * @param s			The Sprite to check collisions for
     * @param tmap		The tile map to check 
     */
    public void checkTileCollision(Sprite s, TileMap tmap) {
        collision = false;
        float sLeftDX = s.getX() + 5; // Shrink collision box
        float sRightDX = s.getX() + s.getWidth() - 5;
        float sTopDy = s.getY() + 5;
        float sBottomDY = s.getY() + s.getHeight();

        int leftCol = (int) (sLeftDX / tmap.getTileWidth());
        int rightCol = (int) (sRightDX / tmap.getTileWidth());
        int topRow = (int) (sTopDy / tmap.getTileHeight());
        int bottomRow = (int) (sBottomDY / tmap.getTileHeight());

        // Ensure character is not inside a solid tile
        while (isTileSolid(tmap, leftCol, bottomRow) || isTileSolid(tmap, rightCol, bottomRow)) {
            s.setY(s.getY() - 1); // Push up until outside the solid block
            bottomRow = (int) ((s.getY() + s.getHeight()) / tmap.getTileHeight());
            collision=true;
        }

        // Check vertical collision (Up and Down)
        if (s.getVelocityY() < 0) { // Moving Up
            int newTopRow = (int) ((sTopDy + s.getVelocityY()) / tmap.getTileHeight());
            if (isTileSolid(tmap, leftCol, newTopRow) || isTileSolid(tmap, rightCol, newTopRow)) {
                s.setVelocityY(0.0f);
                s.setY((newTopRow + 1) * tmap.getTileHeight()); // Adjust position
                collision=true;
            }
        } else if (s.getVelocityY() > 0) { // Moving Down
            int newBottomRow = (int) ((sBottomDY + s.getVelocityY()) / tmap.getTileHeight());
            if (isTileSolid(tmap, leftCol, newBottomRow) || isTileSolid(tmap, rightCol, newBottomRow)) {
                s.setVelocityY(0.0f);
                s.setY(newBottomRow * tmap.getTileHeight() - s.getHeight()); // Adjust position
                collision=true;
            }
        }

        // Check horizontal collision (Left and Right)
        if (s.getVelocityX() < 0) { // Moving Left
            int newLeftCol = (int) ((sLeftDX + s.getVelocityX()) / tmap.getTileWidth());
            if (isTileSolid(tmap, newLeftCol, topRow) || isTileSolid(tmap, newLeftCol, bottomRow)) {
                s.setVelocityX(0.0f);
                s.setX((newLeftCol + 1) * tmap.getTileWidth()); // Adjust position
                collision=true;
            }
        } else if (s.getVelocityX() > 0) { // Moving Right
            int newRightCol = (int) ((sRightDX + s.getVelocityX()) / tmap.getTileWidth());
            if (!isTileSolid(tmap, newRightCol, topRow) && !isTileSolid(tmap, newRightCol, bottomRow)) {
                s.setX(s.getX() + s.getVelocityX()); // Allow movement if not blocked
            } else {
                s.setVelocityX(0.0f);
                s.setX(newRightCol * tmap.getTileWidth() - s.getWidth()); // Adjust position
                collision=true;
            }
        }
    }

    private boolean isTileSolid(TileMap tmap, int col, int row) {
        Tile tile = tmap.getTile(col, row);
        return tile != null && tile.getCharacter() != '.' && tile.getCharacter() !='d';
    }
    public void checkTileCollisionNPC2(Sprite s, TileMap tmap) {
        boolean colide = false;
        float sLeftDX = s.getX()+5; // Shrink collision box
        float sRightDX = s.getX() + s.getWidth()-5;
        float sTopDy = s.getY()+10;
        float sBottomDY = s.getY() + s.getHeight();

        int leftCol = (int) (sLeftDX / tmap.getTileWidth());
        int rightCol = (int) (sRightDX / tmap.getTileWidth());
        int topRow = (int) (sTopDy / tmap.getTileHeight());
        int bottomRow = (int) (sBottomDY / tmap.getTileHeight());

        // Ensure character is not inside a solid tile (push up logic)
        while (isTileSolid(tmap, leftCol, bottomRow) || isTileSolid(tmap, rightCol, bottomRow)) {
            s.setY(s.getY() - 1); // Push up until outside the solid block
            bottomRow = (int) ((s.getY() + s.getHeight()) / tmap.getTileHeight());
            colide = true;
        }
        s.setVelocityY(+0.01f);
        // Check vertical collision (Up and Down)
        if (s.getVelocityY() < 0) { // Moving Up
            int newTopRow = (int) ((sTopDy + s.getVelocityY()) / tmap.getTileHeight());
            if (isTileSolid(tmap, leftCol, newTopRow) || isTileSolid(tmap, rightCol, newTopRow)) {
                s.setVelocityY(0.0f);
                s.setY((newTopRow + 1) * tmap.getTileHeight()); // Adjust position
                colide=true;
            }
        } else if (s.getVelocityY() > 0) { // Moving Down
            int newBottomRow = (int) ((sBottomDY + s.getVelocityY()) / tmap.getTileHeight());
            if (isTileSolid(tmap, leftCol, newBottomRow) || isTileSolid(tmap, rightCol, newBottomRow)) {
                s.setVelocityY(0.0f);
                s.setY(newBottomRow * tmap.getTileHeight() - s.getHeight()); // Adjust position
                colide=true;
            }
        }


        // Check horizontal collision (Left and Right)
        boolean hitWall = false;

        if (s.getVelocityX() < 0) { // Moving Left
            int newLeftCol = (int) ((sLeftDX + s.getVelocityX()) / tmap.getTileWidth());
            if (isTileSolid(tmap, newLeftCol, topRow) || isTileSolid(tmap, newLeftCol, bottomRow)) {
                hitWall = true;
                s.setX((newLeftCol + 1) * tmap.getTileWidth()); // Adjust position
                colide=true;
            }
        } else if (s.getVelocityX() > 0) { // Moving Right
            int newRightCol = (int) ((sRightDX + s.getVelocityX()) / tmap.getTileWidth());
            if (isTileSolid(tmap, newRightCol, topRow) || isTileSolid(tmap, newRightCol, bottomRow)) {
                hitWall = true;
                s.setX(newRightCol * tmap.getTileWidth() - s.getWidth()); // Adjust position
                colide=true;
            }
        }

        // If hit a wall, reverse direction & flip sprite
        if (hitWall) {
            s.setVelocityX(-s.getVelocityX()); // Reverse direction
            s.setScale((float) (s.getScaleX() * -1), 1.0f); // Flip sprite horizontally
        } else {
            s.setX(s.getX() + s.getVelocityX()); // Move normally if no collision
        }
    }
    public void checkProjectileCollision(Sprite p, TileMap tmap) {
        if (p == null || !p.isActive()) return; // Avoid null pointer errors

        float pLeft = p.getX();
        float pRight = p.getX() + p.getWidth();
        float pTop = p.getY();
        float pBottom = p.getY() + p.getHeight();

        int leftCol = (int) (pLeft / tmap.getTileWidth());
        int rightCol = (int) (pRight / tmap.getTileWidth());
        int topRow = (int) (pTop / tmap.getTileHeight());
        int bottomRow = (int) (pBottom / tmap.getTileHeight());

        // Check if the projectile is hitting a solid tile
        if (isTileSolid(tmap, leftCol, topRow) || isTileSolid(tmap, rightCol, topRow) ||
                isTileSolid(tmap, leftCol, bottomRow) || isTileSolid(tmap, rightCol, bottomRow)) {

            // Make the projectile vanish
            p.deactivate();
        } else {
            // Move the projectile normally
            p.setX(p.getX() + p.getVelocityX());
            p.setY(p.getY() + p.getVelocityY());
        }
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

    @Override
    public void mouseClicked(MouseEvent e) {

    }

    @Override
    public void mousePressed(MouseEvent e) {
        if (e.getButton() == MouseEvent.BUTTON3) { // Right mouse button
           // System.out.println("Right mouse button clicked!");
            dash = true;
            player.setAnimationFrame(0);

            if (player.getScaleX() < 0.0F && player.getAnimation()==marinestanding ) {
                player.setPosition(player.getX()-80, player.getY());
            }
            if (player.getScaleX()>0.0f && player.getAnimation()==marinestanding){
                player.setPosition(player.getX()+80, player.getY());
            }
        }
        if (e.getButton() == MouseEvent.BUTTON1) { // Right mouse button
            // System.out.println("Right mouse button clicked!");
            shoot = true;
            projectile.setPosition(player.getX(),player.getY());
            projectile.activate();

            player.setAnimationFrame(0);


        }
    }
    public void setFalseAfterDelay(int delay) {
        new javax.swing.Timer(delay, e -> {
            dash = false;
            System.out.println("Boolean set to false!");
        }).start();
    }
    java.util.Timer timer = new java.util.Timer();
    @Override
    public void mouseReleased(MouseEvent e) {
        if (e.getButton() == MouseEvent.BUTTON3) { // Right mouse button

            timer.schedule(new java.util.TimerTask() {
                @Override
                public void run() {
                    dash = false;
                    ideal = true;

                }
            }, 700);


        }
        if (e.getButton() == MouseEvent.BUTTON1) { // Left mouse button

            timer.schedule(new java.util.TimerTask() {
                @Override
                public void run() {
                    shoot = false;
                    ideal = true;

                }
            }, 600);
        }

        }

    @Override
    public void mouseEntered(MouseEvent e) {

    }

    @Override
    public void mouseExited(MouseEvent e) {

    }
}
