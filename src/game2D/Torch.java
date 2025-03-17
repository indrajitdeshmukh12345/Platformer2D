package game2D;

public class Torch extends Sprite {
    private int torchHealth;

    public Torch(Animation anim, int torchHealth) {
        super(anim);
        this.torchHealth = torchHealth;
    }

    public int getTorchHealth() {
        return torchHealth;
    }

    public void setTorchHealth(int torchHealth) {
        this.torchHealth = torchHealth;
    }

    public void update(long elapsedTime) {
        super.update(elapsedTime);
        // Add specific behavior for the torch here
    }
}
