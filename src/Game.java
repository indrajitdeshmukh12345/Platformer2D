
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

import game2D.*;

import javax.swing.*;

import static java.lang.Math.cos;
import static java.lang.Math.sin;

// Student ID: 3300017

@SuppressWarnings("serial")


public class Game extends GameCore {// Game constants
    static int screenWidth = 1000;
    static int screenHeight = 500;
    private static int currentLevel = 1;

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
    Animation vilanrun, vilanattack, Projectile, Projectile2, Villandeath, Pully, Spikes, Box, Flag, Fire, villanrun2, bounce, bossrun, bossdeath, bossdash;

    // Background sprites
    Sprite Background1, Background2, Background3, Background4, Background5;

    // Player and other sprites
    Sprite player, projectile, projectile2, pully, box, spring, flag, boss;

    // Lists for multiple Villans , Torches and Spikes
    private ArrayList<Villan> villans = new ArrayList<>();
    private ArrayList<Torch> torches = new ArrayList<>();
    private ArrayList<Spike> spikes = new ArrayList<>();

    // Other lists
    ArrayList<Sprite> clouds = new ArrayList<>();
    ArrayList<Tile> collidedTiles = new ArrayList<>();

    // Cooldown variables
    private long lastDamageTime = 0;
    private long damageCooldown = 2000; // 1 second cooldown
    long lastPullySoundTime = 0;
    long pullySoundCooldown = 3000;
    long WalkSoundCooldown = 500;
    long lastWalkSoundTime = 0;
    long deadSoundCooldown = 90000;
    long lastdeadSoundTime = 0;
    long fireSoundCooldown = 2300;
    long lastfireSoundTime = 0;
    long slideSoundCooldown = 8000;
    long lastslideSoundTime = 0;
    long slidegrowlCooldown = 3000;
    long lasgrowlSoundTime = 0;
    long SpringCooldown = 500;
    long springSoundTime = 0;
    long lasttorchTime = System.currentTimeMillis();
    private boolean isgettingDamaged = false;
    // Collison instances
    Collisions collisions = new Collisions();
    Collisions collisions2 = new Collisions();

    // Tile map
    TileMap tmap = new TileMap();


    //Sounds
    BackgroundMusic backgroundMusic;

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
     * @param args The list of parameters this program might use (ignored)
     */


    public static void main(String[] args) {

        Game game = new Game();
        currentLevel = GameLauncherGUI.getSelectedLevel();

        if (currentLevel == 3) {
            GameInfoGUI.createGameInfoWindow();
        }
        if (currentLevel != 3) {
            game.init();
            game.run(false, screenWidth, screenHeight);
        }

    }


    /**
     * Initialise the class, e.g. set up variables, load images,
     * create animations, register event handlers.
     * <p>
     * This shows you the general principles but you should create specific
     * methods for setting up your game that can be called again when you wish to
     * restart the game (for example you may only want to load animations once
     * but you could reset the positions of sprites each time you restart the game).
     */


    public void init() {
        Sprite s;    // Temporary reference to a sprite
        setSize(tmap.getPixelWidth() / 4, tmap.getPixelHeight());
        setVisible(true);
        // Initialise  animations
        loadAnimations();
        // Initialise the player with an animation
        player = new Sprite(marinestanding);
        //initialise the projectile with an animation
        projectile = new Sprite(Projectile);
        projectile2 = new Sprite(Projectile2);

        // initialise the pully with animation
        pully = new Sprite(Pully);
        spikes.add(new Spike(Spikes, 150));
        spikes.add(new Spike(Spikes, 150));
        spikes.add(new Spike(Spikes, 150));
        //initialise the other sprites  with animation
        box = new Sprite(Box);
        spring = new Sprite(bounce);
        flag = new Sprite(Flag);
        torches.add(new Torch(Fire, 100));
        torches.add(new Torch(Fire, 150));

        // intialise the vilan with an animation

        villans.add(new Villan(vilanrun, -0.03f));
        villans.add(new Villan(villanrun2, -0.03f));
        villans.add(new Villan(villanrun2, -0.03f));

        boss = new Sprite(bossrun);
        boss.setVelocityX(-0.04f);
        boss.setScale(-1f);

        // Add light source for player
        lightEffect.addLightSource(player.getX(), player.getY(), 200);
        lightEffect.addLightSource(boss.getX(), boss.getY(), 70);
        // Add light sources for torches
        for (int i = 0; i < torches.size(); i++) {
            Torch torch = torches.get(i);
            lightEffect.addLightSource(torch.getX() - 25, torch.getY() - 25, 50);
        }

        if (currentLevel == 1) {
            // Load the tile map and print it out so we can check it is valid
            tmap.loadMap("maps", "map.txt");
            initialiseGameL1();

        } else if (currentLevel == 2) {
            tmap.loadMap("maps", "map2.txt");
            initialiseGameL2();
        }

        initializeBackground();
        System.out.println(tmap);


        // initialize MDI SOUND
        backgroundMusic = new BackgroundMusic("sounds/background2.mid", true);
        backgroundMusic.play();


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
        Projectile2 = new Animation();
        Image projctile2 = new ImageIcon("images/Projectile2.png").getImage();
        Projectile2.addFrame(projctile2, 150);

        Pully = new Animation();
        Pully.loadAnimationFromSheet("images/Pully.png", 3, 1, 300);

        Spikes = new Animation();
        Image animSpike = new ImageIcon("maps/tile_0068.png").getImage();
        Spikes.addFrame(animSpike, 150);

        bounce = new Animation();
        Image animspring1 = new ImageIcon("images/spring1.png").getImage();
        Image animspring2 = new ImageIcon("images/spring2.png").getImage();
        bounce.addFrame(animspring1, 150);
        bounce.addFrame(animspring2, 150);

        Fire = new Animation();
        Fire.loadAnimationFromSheet("images/fire.png", 8, 1, 150);

        Box = new Animation();
        Image animBox = new ImageIcon("maps/tile_0028.png").getImage();
        Box.addFrame(animBox, 150);

        Flag = new Animation();
        Image animFlag1 = new ImageIcon("images/Flag1.png").getImage();
        Image animFlag2 = new ImageIcon("images/Flag2.png").getImage();
        Flag.addFrame(animFlag1, 150);
        Flag.addFrame(animFlag2, 150);

        villanrun2 = new Animation();
        villanrun2.loadAnimationFromSheet("images/run.png", 1, 8, 150);
        bossrun = new Animation();
        bossrun.loadAnimationFromSheet("images/bossrun.png", 8, 1, 150);
        bossdeath = new Animation();
        bossdeath.loadAnimationFromSheet("images/bossdeath.png", 10, 1, 150);
        bossdash = new Animation();
        bossdash.loadAnimationFromSheet("images/bossdash.png", 4, 1, 150);

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
        Background1.setScale(0.5f, 0.45f);
        Background1.setPosition(0, 0);
        Background1.show();

        Background2 = new Sprite(backgrownd2);
        Background2.setVelocityX(-player.getVelocityX() * 1.5f);
        Background2.setScale(0.5f, 0.45f);
        Background2.setPosition(0, 0);
        Background2.show();

        Background3 = new Sprite(backgrownd3);
        Background3.setScale(0.5f, 0.45f);
        Background3.setPosition(0, 80);
        Background3.show();

        Background4 = new Sprite(backgrownd4);
        Background4.setScale(0.5f, 0.45f);
        Background4.setPosition(0, 0);
        Background4.show();

        Background5 = new Sprite(backgrownd5);
        Background5.setScale(0.5f, 0.45f);
        Background5.setPosition(0, 0);
        Background5.show();
    }

    /**
     * You will probably want to put code to restart a game in
     * a separate method so that you can call it when restarting
     * the game when the player loses.
     */
    public void initialiseGameL1() {
        torchHealth = 100;
        spring.deactivate();
        //set villains position -
        villans.get(0).setPosition(390, 100);
        villans.get(1).setPosition(390, 150);
        villans.get(2).setPosition(272, 270);
        boss.setPosition(350, 410);
        boss.show();
        boss.setScale(-1, 1);
        for (int i = 0; i < villans.size(); i++) {

            villans.get(i).setVelocity(-0.03f, 0);
            villans.get(i).setHealth(100);
            villans.get(i).setAnimation(vilanrun);
            villans.get(i).activate();
            villans.get(i).show();
        }
        villans.get(0).setPosition(390, 100);
        villans.get(1).setPosition(390, 180);
        //set player position -
        player.setPosition(200, 150);
        player.setVelocity(0, 0);
        player.setHealth(100);
        player.setAnimation(marinestanding);
        player.show();
        //set Bullets position -
        projectile.setPosition(player.getX() + 5, player.getY());
        projectile.setAnimation(Projectile);
        projectile.setScale(0.5f);
        projectile.deactivate();
        projectile.show();
        projectile2.setPosition(boss.getX() + 5, boss.getY());
        projectile2.setAnimation(Projectile2);
        projectile2.setScale(0.5f);
        projectile2.deactivate();
        projectile2.show();
        //set Pull position -
        pully.setPosition(500, 150);
        pully.setAnimation(Pully);
        pully.show();
        //set Spike position -
        spikes.get(0).setPosition(230, 217);
        spikes.get(1).setPosition(672, 361);
        spikes.get(2).setPosition(412, 361);
        //set Torch position -
        torches.get(0).setPosition(470, 120);
        torches.get(1).setPosition(425, 320);


        for (int i = 0; i < spikes.size(); i++) {
            spikes.get(i).setAnimation(Spikes);
            spikes.get(i).activate();
            spikes.get(i).show();
        }

        flag.setPosition(550, 409);
        flag.show();
        box.setPosition(620, 190);
        box.setAnimation(Box);
        box.show();
        lightEffect.setEffectOn(true);


    }

    public void initialiseGameL2() {
        torchHealth = 100;
        villans.get(0).setPosition(590, 100);
        villans.get(1).setPosition(430, 200);
        villans.get(2).setPosition(600, 330);
        boss.setPosition(250, 330);
        boss.show();
        boss.setScale(-1, 1);
        for (int i = 0; i < villans.size(); i++) {

            villans.get(i).setVelocity(-0.03f, 0);
            villans.get(i).setHealth(100);
            villans.get(i).setAnimation(vilanrun);
            villans.get(i).activate();
            villans.get(i).show();
        }

        player.setPosition(200, 150);
        player.setVelocity(0, 0);
        player.setHealth(100);
        player.setAnimation(marinestanding);
        player.show();
        projectile.setPosition(player.getX() + 5, player.getY());
        projectile.setAnimation(Projectile);
        projectile.setScale(0.5f);
        projectile.deactivate();
        projectile.show();
        projectile2.setPosition(player.getX() + 5, player.getY());
        projectile2.setAnimation(Projectile2);
        projectile2.setScale(0.5f);
        projectile2.deactivate();
        projectile2.show();
        pully.setPosition(400, 150);
        pully.setAnimation(Pully);
        pully.show();
        spring.setPosition(455, 251);
        spring.setAnimation(bounce);
        spring.show();
        spikes.add(new Spike(Spikes, 150));
        spikes.get(0).setPosition(560, 145);
        spikes.get(1).setPosition(863, 361);
        spikes.get(2).setPosition(412, 361);
        spikes.get(3).setPosition(863, 448);
        torches.get(0).setPosition(570, 120);
        torches.get(1).setPosition(425, 320);
        for (int i = 0; i < spikes.size(); i++) {
            spikes.get(i).setAnimation(Spikes);
            spikes.get(i).activate();
            spikes.get(i).show();
        }

        flag.show();
        flag.setPosition(150, 340);
        box.setPosition(800, 190);
        box.setAnimation(Box);
        box.show();
        lightEffect.setEffectOn(true);
        Sound s = new Sound("sounds/try.wav");
        s.start();

    }


    /**
     * Draw the current state of the game. Note the sample use of
     * debugging output that is drawn directly to the game screen.
     */
    public void draw(Graphics2D g) {
        int xo = -(int) player.getX() + 200;
        int yo = -(int) player.getY() + 200;

        g.setColor(Color.darkGray);
        g.fillRect(0, 0, getWidth(), getHeight());

        Background1.setOffsets(xo - 50, yo - 100);
        Background1.drawTransformed(g);
        Background2.setOffsets(xo - 50, yo - 200);
        Background2.drawTransformed(g);
        Background3.setOffsets(xo - 50, yo - 200);
        Background3.drawTransformed(g);
        Background4.setOffsets(xo - 50, yo - 200);
        Background4.drawTransformed(g);
        Background5.setOffsets(xo - 50, yo - 200);
        Background5.drawTransformed(g);


        // Apply offsets to sprites then draw them
        for (Sprite s : clouds) {
            s.setOffsets(xo, yo);
            s.draw(g);
        }


        // Apply offsets to tile map and draw  it
        tmap.draw(g, xo, yo);
        pully.setOffsets(xo, yo);
        pully.draw(g);

        spring.setOffsets(xo, yo);
        if (isActive()) {
            spring.draw(g);
        }


        flag.setOffsets(xo, yo);
        flag.draw(g);


        box.setOffsets(xo, yo);
        box.draw(g);
        for (Villan villan : villans) {
            villan.setOffsets(xo, yo);
            if (villan.isActive()) {

                villan.setOffsets(xo, yo);
                villan.drawTransformed(g);

            }
        }

        boss.setOffsets(xo, yo);
        if (boss.isActive()) {
            boss.drawTransformed(g);
        }

        for (Spike spike : spikes) {
            if (spike.isActive()) {
                spike.setOffsets(xo, yo);
                spike.draw(g);
            }
        }
        for (Torch fire : torches) {

            if (fire.isActive()) {
                fire.setOffsets(xo, yo);
                fire.draw(g);
            }
        }

        // Apply offsets to player and draw
        player.setOffsets(xo, yo);
        player.drawTransformed(g);

        if (debug) {
            tmap.drawBorder(g, xo, yo, Color.black);

            g.setColor(Color.red);
            player.drawBoundingBox(g);
            for (Villan villan : villans) {
                villan.drawBoundingBox(g);
            }
            projectile.drawBoundingBox(g);
            projectile2.drawBoundingBox(g);
            boss.drawBoundingBox(g);

            g.drawString(String.format("Player: %.0f,%.0f", player.getX(), player.getY()),
                    getWidth() - 100, 70);

            drawCollidedTiles(g, tmap, xo, yo);
        }
        if (projectile != null && projectile.isActive()) {
            projectile.setOffsets(xo, yo);
            projectile.drawTransformed(g);
        }
        if (projectile2 != null && projectile2.isActive()) {
            projectile2.setOffsets(xo, yo);
            projectile2.drawTransformed(g);
        }

        lightEffect.draw(g);
        // Show score and status information

        String fps = String.format("Frames: %d", getFPS());
        String msghealth = String.format("Health: %d", player.getHealth());
        String TorchLevel = String.format("Torch health: %d", torchHealth);
        g.setColor(Color.white);

        g.drawString(msghealth, getWidth() - 100, 90);
        g.drawString(TorchLevel, getWidth() - 100, 120);
        g.drawString(fps, getWidth() - 100, 150);
    }

    public void drawCollidedTiles(Graphics2D g, TileMap map, int xOffset, int yOffset) {
        if (collidedTiles.size() > 0) {
            int tileWidth = map.getTileWidth();
            int tileHeight = map.getTileHeight();

            g.setColor(Color.blue);
            for (Tile t : collidedTiles) {
                g.drawRect(t.getXC() + xOffset, t.getYC() + yOffset, tileWidth, tileHeight);
            }
        }
    }

    /**
     * Update any sprites and check for collisions
     *
     * @param elapsed The elapsed time between this call and the previous call of elapsed
     */
    public void update(long elapsed) {
        long currentTime = System.currentTimeMillis();

        int xo = -(int) player.getX() + 200; // Camera offset X
        int yo = -(int) player.getY() + 200; // Camera offset Y
        lightEffect.updateLightSource(0, player.getX(), player.getY(), 200, xo, yo);
        lightEffect.updateLightSource(1, boss.getX(), boss.getY(), 70, xo, yo);
        for (int i = 0; i < torches.size(); i++) {
            Torch torch = torches.get(i);
            lightEffect.updateLightSource(i + 2, torch.getX(), torch.getY(), 50, xo + 15, yo + 15);
        }
        // Make adjustments to the speed of the sprite due to gravity
        player.setVelocityY(player.getVelocityY() + (gravity * elapsed));
        box.setVelocityY(box.getVelocityY() + (gravity * elapsed));

        //Remove bullet if it goes out of bounds
        if (projectile.getY() > screenHeight) {
            projectile.deactivate();
        }
        if (projectile.getX() > screenWidth) {
            projectile.deactivate();
        }
        if (projectile2.getY() > screenHeight) {
            projectile2.deactivate();
        }
        if (projectile2.getX() > screenWidth) {
            projectile2.deactivate();
        }

        if (player.getHealth() <= 0) {
            dead = true;
        }
        if (collisions.boundingBoxCollision(player, box)) {

            moveSpeed = 0.02f;
            WalkSoundCooldown = 750;
            box.setVelocityX(player.getVelocityX());
            player.setAnimationSpeed(0.6f);

            Sound slide = new Sound("sounds/sliding.wav");
            if (currentTime - lastslideSoundTime > slideSoundCooldown) {

                if (!slide.isAlive()) {
                    slide.start();
                }

                lastslideSoundTime = currentTime; // Update the timestamp
            }
        } else {
            moveSpeed = 0.05f;
            box.setVelocityX(0.0f);
            WalkSoundCooldown = 750;
            player.setAnimationSpeed(1f);
        }

        player.setAnimationSpeed(1.0f);
        if (torchHealth < 0) {
            lightEffect.setEffectOn(false);
        }

        if (lightEffect.isEffectOn()) {

            long torchdrainlevel = 2000;

            if (currentTime - lasttorchTime > torchdrainlevel) {
                torchHealth = torchHealth - 1;
                lasttorchTime = currentTime;
            }
        }
        // Update Villans


        for (Villan villan : villans) {
            villan.update(elapsed);

            Sound growl = new Sound("sounds/growl.wav");
            // Calculate the distance between the player and the sprite
            float playerX = player.getX();
            float playerY = player.getY();

            float spriteX = villan.getX();
            float spriteY = villan.getY();

            float distance = (float) Math.sqrt(Math.pow(playerX - spriteX, 2) + Math.pow(playerY - spriteY, 2));
            if (currentTime - lasgrowlSoundTime > slidegrowlCooldown && distance < 80 && villan.isActive()) {

                if (!growl.isAlive()) {
                    growl.start();

                }

                lasgrowlSoundTime = currentTime; // Update the timestamp
            }


            villan.setAnimationSpeed(2.0f / (villans.size()));
            collisions.checkTileCollisionNPC2(villan, tmap);

            handleScreenEdge(villan, tmap, elapsed);

            //if projectile hits villan
            if (projectile.isActive() && collisions.boundingBoxCollision(projectile, villan) && villan.isActive()) {
                projectile.deactivate();
                villan.setAnimation(Villandeath);
                villan.setAnimationFrame(0);
                villan.setAnimationSpeed(1);
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
                    Sound hit = new Sound("sounds/hit.wav");
                    hit.start();
                } else {
                    isgettingDamaged = false;
                }
            }
        }
        //Update boss and projectile2

        boss.update(elapsed);
        collisions.checkTileCollisionNPC2(boss, tmap);
        if (projectile.isActive() && collisions.boundingBoxCollision(projectile, boss) && boss.isActive()) {
            projectile.deactivate();
            backgroundMusic.setTempo(1f);
            boss.setAnimation(bossdeath);
            boss.setAnimationFrame(0);
            boss.setAnimationSpeed(1);
            timer.schedule(new java.util.TimerTask() {
                @Override
                public void run() {
                    boss.deactivate();
                    lightEffect.removeLightSource(1);

                }
            }, 1050);
        }
        float distanceboss = (float) Math.sqrt(Math.pow(player.getX() - boss.getX(), 2) + Math.pow(player.getY() - boss.getY(), 2));
        float distinY = boss.getY() - player.getY();
        if (!projectile2.isActive() && distanceboss > 100 && distinY < 31 && boss.isActive()) {
            // change tempo of midi track
            backgroundMusic.setTempo(2f);
            // Calculate the angle between the player and the mouse position in world coordinates
            double angle = Math.atan2((player.getY() - boss.getY()) - 15, player.getX() - boss.getX());
            float speed = projectileSpeed / 1.5f;
            float velocityX = (float) (speed * cos(angle));
            float velocityY = (float) (speed * sin(angle));

            // Offset to start bullet from the player's gun position
            float bulletStartX = boss.getX() + boss.getWidth() / 2;
            float bulletStartY = (boss.getY() + boss.getHeight() / 2);

            projectile2.setPosition(bulletStartX, bulletStartY);
            projectile2.activate();

            // Set the velocity of the projectile
            projectile2.setVelocityX(velocityX);
            projectile2.setVelocityY(velocityY);

        }
        if (projectile2.isActive() && collisions.boundingBoxCollision(projectile2, player) && player.getAnimation() != marinestanding) {
            projectile2.deactivate();
            player.setHealth(player.getHealth() - 20);


        }

        //update Torches

        for (int i = 0; i < torches.size(); i++) {
            Torch fire = torches.get(i);
            fire.update(elapsed);


            // Calculate the distance between the player and the sprite
            float playerX = player.getX();
            float playerY = player.getY();

            float spriteX = torches.get(i).getX();
            float spriteY = torches.get(i).getY();

            float distance = (float) Math.sqrt(Math.pow(playerX - spriteX, 2) + Math.pow(playerY - spriteY, 2));

            // Sound Effect

            SoundFadeIn firesound = new SoundFadeIn("sounds/fire3.wav");

            if (currentTime - lastfireSoundTime > fireSoundCooldown && distance < 105) {

                if (!firesound.isAlive()) {
                    firesound.start();
                }

                lastfireSoundTime = currentTime; // Update the timestamp
            }
            // Check for collisions with the player
            if (collisions.boundingBoxCollision(player, fire) && fire.isActive()) {
                torchHealth = 100; // Restore torch health
                fire.deactivate(); // Deactivate the torch
                torches.remove(i);
                System.out.println(i);
                lightEffect.removeLightSource(i + 1);

            }

        }


        if (ideal && isgettingDamaged == false) {
            player.setAnimation(marinestanding);
        }


        if (dead) {

            player.setAnimation(marinedie);
            Sound die = new Sound("sounds/die.wav");
            backgroundMusic.stop();
            if (currentTime - lastdeadSoundTime > deadSoundCooldown) {

                if (!die.isAlive()) {
                    die.start();
                }

                lastdeadSoundTime = currentTime; // Update the timestamp
            }


            timer.schedule(new java.util.TimerTask() {
                @Override
                public void run() {
                    JOptionPane.showMessageDialog(null, "You ded bro RIP !");
                    stop();

                }
            }, 900);
        }


        if (flap) {
            if (collision) {

                Sound jumpSound = new Sound("sounds/jump5.wav");
                jumpSound.start();
                player.setVelocityY(fly);
            }
        }

        if (dash) {

            player.setAnimation(marinedash);

        }

        if (shoot) {
            if (!moveRight && !moveLeft) {
                player.setAnimation(marineshoot);
                player.setAnimationFrame(0);

            }
        }
        // Check if the player is moving
        if (moveRight && collision || moveLeft && collision) {

            if (currentTime - lastWalkSoundTime > WalkSoundCooldown) {

                Sound snd = new Sound("sounds/walk.wav");


                if (!snd.isAlive()) {
                    snd.start();
                }

                lastWalkSoundTime = currentTime; // Update the timestamp
            }
        }


        if (moveRight && !moveLeft) {

            player.setVelocityX(moveSpeed);
            player.setScale(1.0f, 1f);
            player.setAnimation(marinerun);
        } else if (moveLeft && !moveRight) {
            player.setVelocityX(-moveSpeed);
            player.setScale(-1.0f, 1f);
            player.setAnimation(marinerun);
        } else {
            player.setVelocityX(0);
        }

        flag.update(elapsed);
        if (collisions.boundingBoxCollision(player, flag) && !boss.isActive()) {
            backgroundMusic.stop();
            Sound s = new Sound("sounds/win.wav");
            s.start();
            JOptionPane.showMessageDialog(null, "Level Completed!");
            stop();
        }


        player.update(elapsed);
        box.update(elapsed);
        projectile.update(elapsed);
        projectile2.update(elapsed);

        Background1.setVelocityX(-player.getVelocityX() * 0.05f);
        Background2.setVelocityX(-player.getVelocityX() * 0.1f);
        Background3.setVelocityX(-player.getVelocityX() * 0.08f);
        Background4.setVelocityX(-player.getVelocityX() * 0.2f);
        Background5.setVelocityX(-player.getVelocityX() * 0.2f);

        Background1.update(elapsed);
        Background2.update(elapsed);
        Background3.update(elapsed);
        Background4.update(elapsed);
        Background5.update(elapsed);


        // Then check for any collisions that may have occurred

        handleScreenEdge(player, tmap, elapsed);


        collisions.checkTileCollision(player, tmap);
        collisions2.checkTileCollision(box, tmap);


        collisions.checkProjectileCollision(projectile, tmap);
        collisions.checkProjectileCollision(projectile2, tmap);
        collision = collisions.isCollision();

        if (collisions.boundingBoxCollision(player, pully)) {


            // Check if the cooldown has passed

            if (currentTime - lastPullySoundTime > pullySoundCooldown) {
                Sound snd = new Sound("sounds/lever.wav");

                if (!snd.isAlive()) {
                    snd.start();
                }

                lastPullySoundTime = currentTime; // Update the timestamp
            }


            pully.activate();
            pully.setAnimationSpeed(1f);
            timer.schedule(new java.util.TimerTask() {
                @Override
                public void run() {
                    pully.pauseAnimation();
                }
            }, 1500);
            pully.update(elapsed);
            spikes.get(0).deactivate();
        }
        if (collisions.boundingBoxCollision(player, spring)) {
            Sound s = new Sound("sounds/spring.wav");
            if (currentTime - springSoundTime > SpringCooldown) {

                if (!s.isAlive()) {
                    s.start();
                }

                springSoundTime = currentTime; // Update the timestamp
            }
            spring.activate();
            spring.setAnimationSpeed(2f);
            spring.update(elapsed);
            player.setVelocityY(-0.13f);


        }
        // player hit spike
        for (Spike spike : spikes) {
            if (spike.isActive() && collisions.boundingBoxCollision(player, spike)) {
                player.setHealth(0);
            }
        }

        if (collisions.boundingBoxCollision(box, spikes.get(1))) {
            spikes.get(1).deactivate();
        }


    }


    /**
     * Checks and handles collisions with the edge of the screen. You should generally
     * use tile map collisions to prevent the player leaving the game area. This method
     * is only included as a temporary measure until you have properly developed your
     * tile maps.
     *
     * @param s       The Sprite to check collisions for
     * @param tmap    The tile map to check
     * @param elapsed How much time has gone by since the last call
     */
    public void handleScreenEdge(Sprite s, TileMap tmap, long elapsed) {
        // This method just checks if the sprite has gone off the bottom screen.
        // Ideally you should use tile collision instead of this approach

        float difference = s.getY() + s.getHeight() - tmap.getPixelHeight();
        if (difference > 0) {
            // Put the player back on the map according to how far over they were
            s.setY(tmap.getPixelHeight() - s.getHeight() - (int) (difference));

            // and make them bounce
            s.setVelocityY(-s.getVelocityY() * 0.75f);
        }
    }


    /**
     * Override of the keyPressed event defined in GameCore to catch our
     * own events
     *
     * @param e The event that has been generated
     */
    public void keyPressed(KeyEvent e) {
        int key = e.getKeyCode();

        switch (key) {
            case KeyEvent.VK_UP:
                flap = true;
                break;
            case KeyEvent.VK_RIGHT:
                moveRight = true;
                break;
            case KeyEvent.VK_LEFT:
                moveLeft = true;
                break;
            case KeyEvent.VK_S:
                if (backgroundMusic.isPlaying()) {
                    backgroundMusic.pause();
                } else {
                    backgroundMusic.play();
                }

                break;

            case KeyEvent.VK_ESCAPE:
                stop();
                break;
            case KeyEvent.VK_B:
                debug = !debug;
                break; // Flip the debug state
            case KeyEvent.VK_L:
                if (torchHealth > 0) {
                    lightEffect.setEffectOn(!lightEffect.isEffectOn());
                }
                ;
                lasttorchTime = System.currentTimeMillis();
                break;
            default:
                ideal = true;
                break;
        }

    }


    public void keyReleased(KeyEvent e) {

        int key = e.getKeyCode();

        switch (key) {
            case KeyEvent.VK_ESCAPE:
                stop();
                break;
            case KeyEvent.VK_UP:
                flap = false;
                break;
            case KeyEvent.VK_RIGHT:
                moveRight = false;
                break;
            case KeyEvent.VK_LEFT:
                moveLeft = false;
                break;

            default:
                break;
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
            Sound dash = new Sound("sounds/dash.wav");
            dash.start();

            if (player.getScaleX() < 0.0F && player.getAnimation() == marinestanding) {
                player.setPosition(player.getX() - 80, player.getY());

            }
            if (player.getScaleX() > 0.0f && player.getAnimation() == marinestanding) {
                player.setPosition(player.getX() + 80, player.getY());
            }
        }
        // player shoots
        if (e.getButton() == MouseEvent.BUTTON1 && !projectile.isActive()) { // Left mouse button
            torchHealth = torchHealth - 20;
            Sound snd = new Sound("sounds/shoot.wav");
            SoundEcho sndecho = new SoundEcho("sounds/shoot.wav");

            if (player.getY() < 200) {
                snd.start();
            } else sndecho.start(); // Add echo with 500ms delay and 0.5 decay
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
            double angle = Math.atan2(worldMouseY - player.getY() - 15, worldMouseX - player.getX());
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
            if (velocityX < 0) {
                player.setScale(-1, 1);
            } else player.setScale(1);
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
