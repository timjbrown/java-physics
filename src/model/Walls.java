package model;

public class Walls {

    private boolean northWall;
    private boolean eastWall;
    private boolean southWall;
    private boolean westWall;

    public Walls(boolean northWall, boolean eastWall,
            boolean southWall, boolean westWall) {
        this.northWall = northWall;
        this.eastWall = eastWall;
        this.southWall = southWall;
        this.westWall = westWall;
    }

    public boolean isNorthWall() {
        return northWall;
    }

    public boolean isEastWall() {
        return eastWall;
    }

    public boolean isSouthWall() {
        return southWall;
    }

    public boolean isWestWall() {
        return westWall;
    }
}
