import java.awt.*;

/**
 * Created by Andrew on 3/7/2015.
 */
public class Block {
    int id;
    int x;
    int y;
    int width;
    int height;
    int level;


    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public GameServer getServer() {
        return server;
    }

    public void setServer(GameServer server) {
        this.server = server;
    }

    GameServer server;
    public Block(int id, int x, int y, int width, int height, GameServer gameServer, int level) {
        this.level = level;
        this.id = id;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        server = gameServer;


    }

    public void sendPacket(int i)
    {
        BrickPacket brickPacket = new BrickPacket();
        brickPacket.setID(getId());
        brickPacket.setX(getX());
        brickPacket.setY(getY());
        brickPacket.setWidth(getWidth());
        brickPacket.setHeight(getHeight());
        server.server.sendToTCP(i, brickPacket);

    }
    public void sendPacket()
    {
        BrickPacket brickPacket = new BrickPacket();
        brickPacket.setID(getId());
        brickPacket.setX(getX());
        brickPacket.setY(getY());
        brickPacket.setWidth(getWidth());
        brickPacket.setHeight(getHeight());
        server.server.sendToAllTCP( brickPacket);

    }


    public void destroy()
    {

       //setLevel(getLevel() - 1);
        if(getLevel()<=0)
        {

            BreakPacket breakPacket = new BreakPacket();
            breakPacket.setID(getId());
            server.server.sendToAllTCP(breakPacket);
            server.server.sendToAllTCP(breakPacket);
            server.server.sendToAllTCP(breakPacket);
           //updateColors();
        }
        else
        {
            updateColors();
            updateColors();
            updateColors();
        }



    }
    public void updateColors()
    {
        ColorUpdatePacket updatePacket = new ColorUpdatePacket();
        updatePacket.setID(getId());
        Color c = Color.white;
        if(getLevel()==10)
        {
            c = Color.lightGray;
        }
        if(getLevel()==9)
        {
            c = Color.YELLOW;
        }

        if(getLevel()==8)
        {
            c = Color.PINK;
        }
        if(getLevel()==7)
        {
            c = Color.ORANGE;
        }
        if(getLevel()==6)
        {
            c = Color.MAGENTA;
        }
        if(getLevel()==5)
        {
            c = Color.CYAN;
        }
        if(getLevel()==4)
        {
            c=Color.DARK_GRAY;
        }

        if(getLevel() ==3)
        {
            c=Color.BLUE;
        }
        if(getLevel()==2)
        {
            c = Color.GREEN;
        }
        if(getLevel()==1)
        {
            c = Color.RED;
        }
        if(getLevel()==0)
        {

        }
        updatePacket.setR(c.getRed());
        updatePacket.setG(c.getGreen());
        updatePacket.setB(c.getBlue());

        server.server.sendToAllTCP(updatePacket);
    }

    public Rectangle getRectangle()
    {
        return new Rectangle(x,y,width,height);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }
}
