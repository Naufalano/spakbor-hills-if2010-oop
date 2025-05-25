public class House {
    private int posX;
    private int posY;
    private int width;
    private int height;
    private boolean hasTV;

    public House(int posX, int posY, int width, int height) {
        this.posX = posX;
        this.posY = posY;
        this.width = width;
        this.height = height;
        this.hasTV = false;
    }

    public boolean isWithinHouse(int x, int y) {
        return x >= posX && x < posX + width && y >= posY && y < posY + height;
    }

    // getters and setters
    public int getPosX() { return posX; }
    public int getPosY() { return posY; }
    public int getWidth() { return width; }
    public int getHeight() { return height; }
    public boolean hasTV() { return hasTV; }

    public void setPosX(int posX) { this.posX = posX; }
    public void setPosY(int posY) { this.posY = posY; }
    public void setWidth(int width) { this.width = width; }
    public void setHeight(int height) { this.height = height; }
    public void setHasTV(boolean hasTV) { this.hasTV = hasTV; }
}
