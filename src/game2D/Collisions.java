package game2D;
public class Collisions {


    private boolean collision;

    public Collisions() {
        this.collision = false;
    }
    public boolean isCollision() {
        return collision;
    }
    public void setCollision(boolean collision) {
        this.collision = collision;
    }
    /**
     * Handle Tile Collision Between Sprite and tile map
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
    // Returns True if a tile is solid

    private boolean isTileSolid(TileMap tmap, int col, int row) {
        Tile tile = tmap.getTile(col, row);
        return tile != null && tile.getCharacter() != '.' && tile.getCharacter() !='d';
    }
    /**
     * Handle  Collision Between Non-Playable and tile map.
     * Reverses the scale and Speed of NPC Bouncing them off a solid wall
     */
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
    /**
     * Handle Collision Between Bullets and tile map.
     * Making any Projectile that hits a tile disappear!
     */
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

}
