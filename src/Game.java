
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

import java.awt.*;
import java.util.Timer;


import game2D.*;

import javax.swing.*;

import static java.lang.Math.cos;
import static java.lang.Math.sin;

// Game demonstrates how we can override the GameCore class
// to create our own 'game'. We usually need to implement at
// least 'draw' and 'update' (not including any local event handling)
// to begin the process. You should also add code to the 'init'
// method that will initialise event handlers etc. 

// Student ID: ???????


@SuppressWarnings("serial")


public class Game extends GameCore 
{// Game constants
    static int screenWidth = 1000;
    static int screenHeight = 500;
    private int currentLevel = 1;

    // Game variables
    float lift = 0.005f;
    float gravity = 0.0001f;
    float fly = -0.06f;
    float moveSpeed = 0.05f;
    long torchHealth = 100;
    float projectileSpeed = 0.5f;
    LightEffect lightEffect = new LightEffect(screenWidth, screenHeight);

    // Game state flags
    boolean flap = false;
    boolean moveRight = false;
    boolean shoot = false;
    boolean dead = false;
    boolean moveLeft = false;
    boolean dash = false;
    boolean debug = false;
    boolean collision = false;
    boolean ideal = true;

    // Game resources
    Animation marinerun, marinestanding, marinedie, marinedash, marinewake, marineshoot, marinedamage;
    Animation vilanrun, vilanattack, Projectile, Villandeath, Pully, Spikes, Box, Button, Fire,villanrun2;

    // Background sprites
    Sprite Background1, Background2, Background3, Background4, Background5;

    // Player and other sprites
    Sprite player, projectile, pully, spike, fire, box;

    // Lists for multiple Villans and Torches
    private ArrayList<Villan> villans = new ArrayList<>();
    private ArrayList<Torch> torches = new ArrayList<>();

    // Other lists
    ArrayList<Sprite> clouds = new ArrayList<>();
    ArrayList<Tile> collidedTiles = new ArrayList<>();

    // Cooldown variables
    private long lastDamageTime = 0;
    private long damageCooldown = 2000; // 1 second cooldown
    long lasttorchTime = System.currentTimeMillis();
    private boolean isgettingDamaged = false;
    Collisions collisions = new Collisions();
    Collisions collisions2 = new Collisions();

    // Tile map
    TileMap tmap = new TileMap();

    // Score
    long total;

    // Mouse coordinates
    int mouseX, mouseY;
    /**
     * level selection
     */
    public void setLevel(int level) {
        this.currentLevel = level;
    }


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
        loadAnimations();

        // Create a set of background sprites that we can
        // rearrange to give the illusion of motion


        // Initialise the player with an animation
        player = new Sprite(marinestanding);
        // intialise the vilan with an animation

        villans.add(new Villan(vilanrun, -0.03f));
        villans.add(new Villan(villanrun2, -0.03f));
        villans.get(0).setPosition(390, 100);
        villans.get(1).setPosition(390, 150);
        //initialise the projectile with an animation
        projectile=new Sprite(Projectile);
        // initialise the pully with animation
        pully=new Sprite(Pully);
        spike = new Sprite(Spikes);
        box = new Sprite(Box);
        fire = new Sprite(Fire);
        torches.add(new Torch(Fire, 100));


        torches.get(0).setPosition(450, 200);


        // Add light source for player

        lightEffect.addLightSource(player.getX(), player.getY(), 200);
        // Add light sources for torches
        for (int i = 0; i < torches.size(); i++) {
            Torch torch = torches.get(i);
            lightEffect.addLightSource(torch.getX(), torch.getY(), 50);
        }



        initializeBackground();


        initialiseGame();


        System.out.println(tmap);
    }
    private void loadAnimations() {
        marinerun = new Animation();
        marinerun.loadAnimationFromSheet("images/move with FX.png", 1, 8, 150);

        marinestanding = new Animation();
        marinestanding.loadAnimationFromSheet("images/static idle.png", 1, 1, 150);

        marinedie = new Animation();
        marinedie.loadAnimationFromSheet("images/deathPlayer.png", 1, 6, 150);

        marinedash = new Animation();
        marinedash.loadAnimationFromSheet("images/GAS dash with FX.png", 1, 7, 110);

        marinewake = new Animation();
        marinewake.loadAnimationFromSheet("images/wake.png", 1, 5, 150);

        marineshoot = new Animation();
        marineshoot.loadAnimationFromSheet("images/shoot with FX.png", 1, 4, 150);

        marinedamage = new Animation();
        marinedamage.loadAnimationFromSheet("images/damaged.png", 1, 2, 150);

        vilanrun = new Animation();
        vilanrun.loadAnimationFromSheet("images/run.png", 1, 8, 150);

        Villandeath = new Animation();
        Villandeath.loadAnimationFromSheet("images/death.png", 1, 5, 150);

        vilanattack = new Animation();
        vilanattack.loadAnimationFromSheet("images/attack.png", 1, 4, 150);

        Projectile = new Animation();
        Image animProjec = new ImageIcon("images/Projectile.png").getImage();
        Projectile.addFrame(animProjec, 150);

        Pully = new Animation();
        Pully.loadAnimationFromSheet("images/Pully.png", 3, 1, 300);

        Spikes = new Animation();
        Image animSpike = new ImageIcon("maps/tile_0068.png").getImage();
        Spikes.addFrame(animSpike, 150);

        Fire = new Animation();
        Fire.loadAnimationFromSheet("images/fire.png", 8, 1, 150);
        Box = new Animation();
        Image animBox = new ImageIcon("maps/tile_0028.png").getImage();
        Box.addFrame(animBox,150);
        villanrun2 = new Animation();
        villanrun2.loadAnimationFromSheet("images/run.png", 1, 8, 150);
    }
    private void initializeBackground() {
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
        Background3.setPosition(0,80);
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


        // Initialize other backgrounds similarly...
    }

    /**
     * You will probably want to put code to restart a game in
     * a separate method so that you can call it when restarting
     * the game when the player loses.
     */
    public void initialiseGame()
    {
    	total = 0;
        torchHealth=100;
        for (int i = 0; i < villans.size(); i++){

            villans.get(i).setVelocity(-0.03f,0);
            villans.get(i).setHealth(100);
            villans.get(i).setAnimation(vilanrun);
            villans.get(i).activate();
            villans.get(i).show();
        }
        villans.get(0).setPosition(390,100);
        villans.get(1).setPosition(390,180);

        player.setPosition(200,150);
        player.setVelocity(0,0);
        player.setHealth(100);
        player.setAnimation(marinestanding);
        player.show();
        projectile.setPosition(player.getX()+5,player.getY());
        projectile.setAnimation(Projectile);
        projectile.setScale(0.5f);
        projectile.deactivate();
        projectile.show();
        pully.setPosition(500,150);
        pully.setAnimation(Pully);
        pully.show();
        spike.setPosition(230,217);
        spike.setAnimation(Spikes);
        spike.show();
        fire.setPosition(torches.get(0).getX()-12,torches.get(0).getY()-15);
        fire.setScale(1.5f);
        fire.setAnimation(Fire);
        fire.show();
        box.setPosition(550,190);
        box.setAnimation(Box);
        box.show();
        lightEffect.setEffectOn(true);
        //lightEffect .addLightSource(fire.getX(), fire.getY());

       // lightEffect.addLightSource(player.getX(), player.getY(),200);
        //lightEffect .addLightSource(fire.getX(), fire.getY(),50);

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
        pully.setOffsets(xo,yo);
        pully.draw(g);
        spike.setOffsets(xo,yo);
        fire.setOffsets(xo,yo);
        box.setOffsets(xo,yo);
        box.draw(g);
        for (Villan villan : villans) {
            villan.setOffsets(xo,yo);
            if(villan.isActive()) {

                villan.setOffsets(xo, yo);
                villan.drawTransformed(g);

            }
        }
        if(spike.isActive()){
            spike.draw(g);
        }
        if (fire.isActive()){fire.draw(g);}


        // Apply offsets to player and draw
        player.setOffsets(xo, yo);
        player.drawTransformed(g);




        if (debug)
        {

        	// When in debug mode, you could draw borders around objects
            // and write messages to the screen with useful information.
            // Try to avoid printing to the console since it will produce
            // a lot of output and slow down your game.
            tmap.drawBorder(g, xo, yo, Color.black);

            g.setColor(Color.red);
        	player.drawBoundingBox(g);
            for (Villan villan : villans) {
                villan.drawBoundingBox(g);
            }
            projectile.drawBoundingBox(g);

        	g.drawString(String.format("Player: %.0f,%.0f", player.getX(),player.getY()),
        								getWidth() - 100, 70);

        	drawCollidedTiles(g, tmap, xo, yo);
        }
        if (projectile != null && projectile.isActive()) {
            projectile.setOffsets(xo, yo);
            projectile.drawTransformed(g);
        }

        lightEffect.draw(g);


        // Show score and status information
        String msg = String.format("Score: %d", total/100);
        String fps = String.format("Frames: %d", getFPS());
        String msghealth = String.format("Health: %d", player.getHealth());
        String TorchLevel = String.format("Torch health: %d",torchHealth);
        g.setColor(Color.white);
        g.drawString(msg, getWidth() - 100, 60);
        g.drawString(msghealth, getWidth() - 100, 90);
        g.drawString(TorchLevel, getWidth() - 100, 120);
        g.drawString(fps, getWidth() - 100, 150);



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
        int xo = -(int)player.getX() + 200; // Camera offset X
        int yo = -(int)player.getY() + 200; // Camera offset Y
        lightEffect.updateLightSource(0,player.getX(), player.getY(),200, xo, yo);
        for (int i = 0; i < torches.size(); i++) {
            Torch torch = torches.get(i);
            lightEffect.updateLightSource(i + 1, torch.getX(), torch.getY(), 50, xo, yo + 5);
        }
        // Make adjustments to the speed of the sprite due to gravity
        player.setVelocityY(player.getVelocityY() + (gravity * elapsed));
        box.setVelocityY(box.getVelocityY() + (gravity * elapsed));

        //Remove bullet if it goes out of bounds
        if(projectile.getY()>screenHeight){projectile.deactivate();}
        if(projectile.getX()>screenWidth){projectile.deactivate();}

        if(player.getHealth()<=0){dead= true;}
        if(collisions.boundingBoxCollision(player,fire)&&fire.isActive()){torchHealth=100;fire.deactivate();}
        if(collisions.boundingBoxCollision(player,box)){
            moveSpeed = 0.02f;
            box.setVelocityX(player.getVelocityX());
            }else {moveSpeed = 0.05f;box.setVelocityX(0.0f);}

        player.setAnimationSpeed(1.0f);
        long currentTime = System.currentTimeMillis();
        if(torchHealth<0){lightEffect.setEffectOn(false);}

        if(lightEffect.isEffectOn()){

            long torchdrainlevel = 2000;

            if (currentTime - lasttorchTime > torchdrainlevel) {
                torchHealth = torchHealth-1;

                lasttorchTime = currentTime;

            }
        }
        //update villan
        // Update Villans
        for (Villan villan : villans) {
            villan.update(elapsed);
            collisions.checkTileCollisionNPC2(villan,tmap);

            handleScreenEdge(villan,tmap,elapsed);

            //if projectile hits villan
            if (projectile.isActive()&& collisions.boundingBoxCollision(projectile,villan)&&villan.isActive()) {
                projectile.deactivate();
                villan.setAnimation(Villandeath);
                timer.schedule(new java.util.TimerTask() {
                    @Override
                    public void run() {
                        villan.deactivate();


                    }
                }, 750);
            }


            // Check for collisions with the player
            if (collisions.boundingBoxCollision(player, villan) && villan.isActive()) {
                // long currentTime = System.currentTimeMillis();
                if (currentTime - lastDamageTime > damageCooldown) {
                    villan.setAnimation(vilanattack);
                    player.setAnimation(marinedamage);
                    player.setHealth(player.getHealth() - 50);
                    lastDamageTime = currentTime;
                    isgettingDamaged = true;
                } else {
                    isgettingDamaged = false;
                   // villan.setAnimation(vilanrun);
                }
            }
        }
        //update Torches

        for (int i = 0; i < torches.size(); i++) {
            Torch torch = torches.get(i);
            torch.update(elapsed);

            // Check for collisions with the player
            if (collisions.boundingBoxCollision(player, torch) && torch.isActive()) {
                torchHealth = 100; // Restore torch health
                torch.deactivate(); // Deactivate the torch
                if (lightEffect.getLightSourceCount() > i+1) {
                    lightEffect.removeLightSource(i+1);
                }
            }
        }




        if(ideal&&isgettingDamaged==false){
            player.setAnimation(marinestanding);
        }

       	if(dead){
            player.setAnimation(marinedie);
            timer.schedule(new java.util.TimerTask() {
                @Override
                public void run() {
                    stop();

                }
            }, 700);
        }


       	if (flap&&isgettingDamaged==false)
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

        if (moveRight && !moveLeft && isgettingDamaged==false) {
            player.setVelocityX(moveSpeed);
            player.setScale(1.0f, 1f);
            player.setAnimation(marinerun);
        }
        else if (moveLeft && !moveRight && isgettingDamaged==false) {
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
        box.update(elapsed);

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


        collisions.checkTileCollision(player,tmap);
        collisions2.checkTileCollision(box,tmap);


        collisions.checkProjectileCollision(projectile,tmap);
        collision = collisions.isCollision();

        if (collisions.boundingBoxCollision(player,pully)){
            pully.activate();
            pully.setAnimationSpeed(1f);
            timer.schedule(new java.util.TimerTask() {
                @Override
                public void run() {
                    pully.pauseAnimation();
                }
            }, 1500);
            pully.update(elapsed);
            spike.deactivate();
        }
        // player hit spike
        if(spike.isActive()&& collisions.boundingBoxCollision(player,spike)){
            player.setHealth(0);
        }




        //lightEffect.draw(g, player.getX()-10, player.getY()+75);

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
            case KeyEvent.VK_L      :  if(torchHealth>0){ lightEffect.setEffectOn(!lightEffect.isEffectOn());};lasttorchTime = System.currentTimeMillis();
                break;
            default : ideal=true; break;
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
        // player shoots
        if (e.getButton() == MouseEvent.BUTTON1 && !projectile.isActive()) { // Left mouse button
            torchHealth = torchHealth-20;

            // Get the mouse coordinates relative to the screen
            int screenMouseX = e.getX();
            int screenMouseY = e.getY();

            // Calculate the camera offset
            int xo = -(int) player.getX() + 200;
            int yo = -(int) player.getY() + 200;

            // Convert screen mouse coordinates to world coordinates
            float worldMouseX = screenMouseX - xo;
            float worldMouseY = screenMouseY - yo;

            // Calculate the angle between the player and the mouse position in world coordinates
            double angle = Math.atan2(worldMouseY - player.getY()-15, worldMouseX - player.getX());
            float speed = projectileSpeed;
            float velocityX = (float) (speed * cos(angle));
            float velocityY = (float) (speed * sin(angle));

            // Offset to start bullet from the player's gun position
            float bulletStartX = player.getX() + player.getWidth() / 2;
            float bulletStartY = player.getY() + player.getHeight() / 2;

            projectile.setPosition(bulletStartX, bulletStartY);
            projectile.activate();

            // Set the velocity of the projectile
            projectile.setVelocityX(velocityX);
            projectile.setVelocityY(velocityY);
            if(velocityX<0){player.setScale(-1,1);}
            else player.setScale(1);
            player.setAnimationFrame(0);
            shoot = true;
        }
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
