package game2D;

public class Spike extends Sprite {
    private int damage;

    public Spike(Animation anim, int damage) {
        super(anim);
        this.damage = damage;
    }

    public int getDamage() {
        return damage;
    }

    public void setDamage(int damage) {
        this.damage = damage;
    }

    public void update(long elapsedTime) {
        super.update(elapsedTime);
        // Add specific behavior for the spike here
    }
}
