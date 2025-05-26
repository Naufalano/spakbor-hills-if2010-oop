package cls.world;
import enums.*;

public class Tile {
    private int x;
    private int y;
    private boolean isOccupied;
    private Object objectOnTile;
    private TileState state;

    public Tile(int x, int y) {
        this.x = x;
        this.y = y;
        this.isOccupied = false;
        this.objectOnTile = null;
        this.state = TileState.DEFAULT;
    }

    public boolean isOccupied() {
        return isOccupied;
    }

    public void setOccupied(boolean isOccupied) {
        this.isOccupied = isOccupied;
    }

    public Object getObjectOnTile() {
        return objectOnTile;
    }

    public void setObjectOnTile(Object objectOnTile) {
        this.objectOnTile = objectOnTile;
        this.setOccupied(objectOnTile != null);
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public TileState getState() { 
        return state;
    }

    public void setState(TileState state) { 
        this.state = state;
    }
}