package game2D;

public class Villan extends Sprite {
    private int health;
    private float speed;

    public Villan(Animation anim, float speed) {
        super(anim);
        this.speed = speed;
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
        // Add specific behavior for the villan here
    }
}
