package game2D;

public class Villan extends Sprite {
    private int health;
    private float speed;
    private float velocityX; // Each villan has its own velocity
    private float animationSpeed; // Each villan has its own animation speed


    public Villan(Animation anim, float velocityX) {
        super(anim);
        this.velocityX = velocityX;
        this.animationSpeed = 1.0f; // Default animation speed
        this.health = 100; // Default health
    }

    public int getHealth() {
        return health;
    }

    public void setHealth(int health) {
        this.health = health;
    }

    public void takeDamage(int damage) {
        this.health -= damage;
        if (this.health <= 0) {
            this.deactivate();
        }
    }

    public void update(long elapsedTime) {
        super.update(elapsedTime);
        setAnimationSpeed(animationSpeed);

    }
}
