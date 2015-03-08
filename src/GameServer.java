import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.kryonet.Server;
import org.w3c.dom.css.Rect;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * Created by Andrew on 3/5/2015.
 */
public class GameServer extends JFrame implements MouseListener,MouseMotionListener{
    public static boolean isBetween(int a, int b, int c) {
        return b > a ? c > a && c < b : c > b && c < a;
    }
    public void mouseDragged(MouseEvent event)
    {
        if(selected ==4)
        {


            boolean OK = true;


                int toSetX = -1;
                int toSetY = -1;
                for(int i = 0; i<=1000; i+=blockWidth)
                {
                    for(int j = 0; j<=700; j+=blockHeight)
                    {
                        if(isBetween(i-3, event.getX(), i+3))
                        {
                            if(isBetween(j-3,event.getY(),j+3))
                            {
                                toSetX = i;
                                toSetY = j;
                            }
                        }
                    }
                }
            for(Block b : blocks)
            {
                if(b.getRectangle().contains(new Point(toSetX,toSetY)))
                {
                    OK = false;
                }
            }

            if(OK)
            {
                int id = this.id + 1;
                this.id+=1;
                Block block = new Block(id, toSetX,toSetY,blockWidth,blockHeight,this,levelToSet);
                blocks.add(block);
                block.sendPacket();
                block.updateColors();
            }

        }
        if(selected ==5)
        {


            boolean OK = false;


            int toSetX = -1;
            int toSetY = -1;
            for(int i = 0; i<=1000; i+=blockWidth)
            {
                for(int j = 0; j<=700; j+=blockHeight)
                {
                    if(isBetween(i-3, event.getX(), i+3))
                    {
                        if(isBetween(j-3,event.getY(),j+3))
                        {
                            toSetX = i;
                            toSetY = j;
                        }
                    }
                }
            }
            Block block = null;
            for(Block b : blocks)
            {
                if(b.getRectangle().contains(new Point(toSetX,toSetY)))
                {
                    block = b;
                    OK = true;
                }
            }

            if(OK)
            {
                remove(blockLabels.get(block.getId()));
                blocks.remove(block);
            }

        }
    }

    public void breakAllBlocks()
    {
        for(Block b : blocks)
        {

            b.setLevel(0);
            b.destroy();

            BreakPacket breakPacket = new BreakPacket();
            breakPacket.setID(b.getId());
            server.sendToAllTCP(breakPacket);
        }

        blocks= new ArrayList<Block>();
        id = 1;
    }
    public void loadLevel(String name)
    {


        breakAllBlocks();



        try {
            lines = new ArrayList<String>();
            BufferedReader br = new BufferedReader(new FileReader(new File(System.getProperty("user.home") + "/" + name + ".txt")));
            String line;
            while ((line = br.readLine()) != null) {
                lines.add(line);
            }
            br.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        for(String s : lines)
        {
            //X:Y:LEVEL
            String[] args = s.split(":");
            int x = Integer.parseInt(args[0]);
            int y = Integer.parseInt(args[1]);
            int level = Integer.parseInt(args[2]);


            int id = this.id + 1;
            this.id+=1;
            Block block = new Block(id, x,y,blockWidth,blockHeight,this,level);
            blocks.add(block);
            block.sendPacket();
            block.updateColors();


        }

    }


    List<String> lines = new ArrayList<String>();

    public void updateLines()
    {

    }
    int id = 1;
    public void mouseMoved(MouseEvent event)
    {

    }
    public void mousePressed(MouseEvent event)
    {
        if(selected==1)
        {
           BallPacket ballPacket = new BallPacket();
           ballPacket.setHeight((int)rectangle.getHeight());
           ballPacket.setWidth((int)rectangle.getWidth());
           ballPacket.setX(event.getX());
           ballPacket.setY(event.getY());
            locX = event.getX();
            locY = event.getY();
            server.sendToAllTCP(ballPacket);
        }
        if(selected==2)
        {
            PaddlePacket paddlePacket = new PaddlePacket();
            paddlePacket.setHeight((int)paddle1.getHeight());
            paddlePacket.setWidth((int) paddle1.getWidth());
            paddlePacket.setX((int) event.getPoint().getX());
            paddlePacket.setY((int) paddle1.getY());
            paddlePacket.setPlayer(1);
            client.sendTCP(paddlePacket);

        }
        if(selected ==3)
        {
            PaddlePacket paddlePacket = new PaddlePacket();
            paddlePacket.setHeight((int)paddle2.getHeight());
            paddlePacket.setWidth((int) paddle2.getWidth());
            paddlePacket.setX((int) event.getPoint().getX());
            paddlePacket.setY((int) paddle2.getY());
            paddlePacket.setPlayer(2);
            client.sendTCP(paddlePacket);
        }
    }
    public void mouseReleased(MouseEvent event)
    {

    }
    public void mouseClicked(MouseEvent event)
    {

    }
    public void mouseEntered(MouseEvent event)
    {

    }
    public void mouseExited(MouseEvent event)
    {

    }




    boolean p1 = false;
    boolean p2 = false;
    public int x = 1;
    public int y = 1;
     Server server = null;
    Client client = null;
    JLabel ball = new JLabel();
    JLabel jPaddle1 = new JLabel();
    JLabel jPaddle2  = new JLabel();
    JButton ballSelect = new JButton("Ball");
    JButton paddle1Select = new JButton("Paddle1");
    JButton paddle2Select = new JButton("Paddle2");
    JTextField velocity = new JTextField();
    JButton addBlock = new JButton("Add Block");
    JTextField loadLevel = new JTextField();
    int selected = -1;

    JFrame optionBox = new JFrame();

    JButton levelMode = new JButton("Level Mode");
    boolean isEditingLevel = false;

    JButton deleteBlock = new JButton("Delete Blocks");
    JTextField boxLevel = new JTextField();

    public void saveLevel(String s)
    {
        String words = "";
        for(Block block : blocks)
        {
            words+=block.getX() + ":" + block.getY() + ":" + block.getLevel() + System.getProperty("line.separator");
        }
        File file = new File(System.getProperty("user.home") + "/" + s + ".txt");
        if(!file.exists())
        {
            try
            {
                file.createNewFile();
            }catch(Exception ex){}

        }
        try
        {
            BufferedWriter writer = new BufferedWriter(new FileWriter(file));
            writer.write(words);
            writer.close();
        }catch(Exception ex){}
    }
    public void toggleLevelMode()
    {


        isEditingLevel = !isEditingLevel;

        if(isEditingLevel)
        {
            y = 0;
            x = 0;
            levelMode.setText("Save...");
            breakAllBlocks();
        }
        else
        {

            levelMode.setText("Level Mode");
            final JFrame name = new JFrame("Set a name...");
            name.setVisible(true);
            name.setBounds(0,0,200,200);

            final JTextField field = new JTextField();
            name.add(field);
            field.addKeyListener(new KeyListener() {
                @Override
                public void keyTyped(KeyEvent e) {

                }

                @Override
                public void keyPressed(KeyEvent e) {
                    if(e.getKeyCode()==KeyEvent.VK_ENTER)
                    {
                        saveLevel(field.getText());
                        name.dispose();
                        y = 1;
                        x = 1;
                    }
                }

                @Override
                public void keyReleased(KeyEvent e) {

                }
            });


        }

    }
    int levelToSet = 10;
    public void startClient()
    {
        optionBox.setTitle("Options...");
        optionBox.setLayout(null);
        optionBox.setBounds(0, 0, 200, 500);
        optionBox.add(ballSelect);
        optionBox.add(paddle1Select);
        optionBox.add(paddle2Select);
        optionBox.add(velocity);
        optionBox.add(levelMode);
        optionBox.add(addBlock);
        optionBox.add(loadLevel);
        optionBox.add(boxLevel);
        optionBox.add(deleteBlock);
        int height = 500 / 9;
        ballSelect.setBounds(0, height * 0, 200, height);
        paddle1Select.setBounds(0,height * 1,200,height);
        paddle2Select.setBounds(0,height * 2,200,height);
        addBlock.setBounds(0,(height * 3),200,height);
        levelMode.setBounds(0,height*5,200,height);
        velocity.setBounds(0,height*4,200,height);
        loadLevel.setBounds(0,height*6,200,height);
        deleteBlock.setBounds(0,height*7,200,height);
        boxLevel.setBounds(0,height*8,200,height);

        deleteBlock.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                selected = 5;
            }
        });
        boxLevel.addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent event) {

            }

            @Override
            public void keyPressed(KeyEvent event) {
                if(event.getKeyCode()==KeyEvent.VK_ENTER)
                {
                    levelToSet = Integer.parseInt(boxLevel.getText());
                }
            }

            @Override
            public void keyReleased(KeyEvent event) {

            }
        });
        levelMode.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                toggleLevelMode();
            }
        });
        loadLevel.addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {

            }

            @Override
            public void keyPressed(KeyEvent e) {
                if(e.getKeyCode()==KeyEvent.VK_ENTER)
                {
                    loadLevel(loadLevel.getText());
                    loadLevel.setText("");
                }

            }

            @Override
            public void keyReleased(KeyEvent e) {

            }
        });



        addBlock.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                selected = 4;
            }
        });
        velocity.addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {

            }

            @Override
            public void keyPressed(KeyEvent e) {
                if(e.getKeyCode()==KeyEvent.VK_ENTER)
                {
                    String[] args = velocity.getText().split(":");
                    int xx = Integer.parseInt(args[0]);
                    int yy = Integer.parseInt(args[1]);
                    x = xx;
                    y = yy;
                    velocity.setText("");
                    repaint();
                }
            }

            @Override
            public void keyReleased(KeyEvent e) {

            }
        });

        ballSelect.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                selected = 1;
            }
        });
        paddle1Select.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                selected = 2;
            }
        });
        paddle2Select.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                selected = 3;
            }
        });

        optionBox.setVisible(true);
       //.setBounds(0,0,200,500 / 4);



        setBackground(Color.BLACK);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);


      //  addKeyListener(this);
        setBounds(0,0,1001,700);
        jPaddle1.setBounds(getWidth() /2, getHeight() - 75, 120, 50);
        jPaddle2.setBounds(getWidth() / 2, 0, 120, 50);
        ball.setOpaque(true);
        ball.setBackground(Color.YELLOW);
        add(jPaddle1);
        add(jPaddle2);
        add(ball);
        jPaddle2.setOpaque(true);
        jPaddle2.setBackground(Color.RED);
        jPaddle1.setOpaque(true);
        jPaddle1.setBackground(Color.RED);
        setLayout(null);
        setVisible(true);

        try
        {
            client = new Client();
            Kryo kryo = client.getKryo();
            kryo.register(BallPacket.class);
            kryo.register(PaddlePacket.class);
            kryo.register(RegisterPacket.class);
            kryo.register(ConnectPacket.class);
            kryo.register(BrickPacket.class);
            kryo.register(BreakPacket.class);
            kryo.register(ColorUpdatePacket.class);

            kryo.register(BigModePacket.class);

            client.start();
            client.connect(10000,"127.0.0.1", 45666,45777 );


        }catch(Exception ex){ex.printStackTrace();}

        client.addListener(new Listener()
        {
            public void received(Connection connection, Object object)
            {
              //  repaint();

                //      System.out.print("R\n");
                if(object instanceof BigModePacket)
                {

                    bigMode = true;
                    Executors.newSingleThreadScheduledExecutor().schedule(new Runnable() {
                        @Override
                        public void run() {
                            bigMode = false;
                        }
                    },30,TimeUnit.SECONDS);
                }


                if(object instanceof PaddlePacket)
                {
                    PaddlePacket paddlePacket = (PaddlePacket)object;
                    if(paddlePacket.getPlayer()==1)
                    {
                        jPaddle1.setLocation(paddlePacket.getX(), 50);
                        repaint();
                    }
                    else
                    {
                        jPaddle2.setLocation(paddlePacket.getX(), 700 - (paddlePacket.getHeight() * 2));
                        repaint();
                    }
                }
                if(object instanceof BallPacket)
                {
                    BallPacket ballPacket = (BallPacket)object;
                    ball.setBounds(ballPacket.getX(),ballPacket.getY(), ballPacket.getWidth(), ballPacket.getHeight());
                    repaint();
                    //Math.toDegrees();
                }
                if(object instanceof BrickPacket)
                {
                    BrickPacket blockPacket = (BrickPacket)object;
                    JLabel jButton = new JLabel();
                    jButton.setOpaque(true);
                    jButton.setBounds(blockPacket.getX(),blockPacket.getY(),blockPacket.getWidth(),blockPacket.getHeight());
                    jButton.setBackground(Color.RED);
                    jButton.setBorder(BorderFactory.createLineBorder(Color.BLACK));
                    add(jButton);
                    blockLabels.put(blockPacket.getID(), jButton);
                    repaint();



                }
                if(object instanceof BreakPacket)
                {
                    if(blockLabels.containsKey(((BreakPacket)object).getID()))
                    {
                        remove(blockLabels.get(((BreakPacket)object).getID()));
                    }

                    if(blockLabels.containsKey(((BreakPacket)object).getID()))
                    {
                        blockLabels.remove(((BreakPacket)object).getID());
                    }
                    repaint();

                }
                if(object instanceof ColorUpdatePacket)
                {
                    ColorUpdatePacket colorUpdatePacket = (ColorUpdatePacket)object;

                    JLabel block = blockLabels.get(colorUpdatePacket.getID());
                    if(block!=null)
                    {
                        block.setBackground(new Color(colorUpdatePacket.getR(),colorUpdatePacket.getG(),colorUpdatePacket.getB()));
                        repaint();
                    }


                }
            }



        });

        ConnectPacket connectPacket = new ConnectPacket();
        client.sendTCP(connectPacket);


    }
    public GameServer()
    {
        addMouseMotionListener(this);
        addMouseListener(this);
        setLayout(null);
        setBounds(0,0,1000,700);
        setVisible(true);



        try
        {

            server =  new Server();
            Kryo kryo = server.getKryo();
            // Kryo kryo = client.getKryo();
            kryo.register(BallPacket.class);
            kryo.register(PaddlePacket.class);
            kryo.register(RegisterPacket.class);
            kryo.register(ConnectPacket.class);
            kryo.register(ConnectPacket.class);
            kryo.register(BrickPacket.class);
            kryo.register(BreakPacket.class);
            kryo.register(ColorUpdatePacket.class);
            kryo.register(BigModePacket.class);




            server.bind(45666,45777);

            server.start();
        }catch(Exception ex){ex.printStackTrace();}
        server.addListener(new Listener()
        {



            public void received(Connection connection, Object object)
            {

                if(object instanceof BigModePacket)
                {
                    if(score-1000>=0 && !bigMode)
                    {
                        score-=1000;
                //        server.sendToAllTCP(new BigModePacket());


                        bigMode= true;
                        Executors.newSingleThreadScheduledExecutor().schedule(new Runnable() {
                            @Override
                            public void run() {
                                bigMode = false;
                            }
                        }, 30, TimeUnit.SECONDS);
                    }
                }

                if(lastTick + 300 < System.currentTimeMillis())
                {
                    Executors.newSingleThreadScheduledExecutor().scheduleAtFixedRate(new Runnable() {
                        @Override
                        public void run() {

                                update();

                        }
                    }, 10, 10, TimeUnit.MILLISECONDS);
                }


                if(object instanceof ConnectPacket)
                {
                    if(connection.getID()>1)
                    {
                        RegisterPacket registerPacket = new RegisterPacket();
                        System.out.print(registerPacket.getPlayerNum() + "::\n");
                        if(!p1)
                        {

                            registerPacket.setPlayerNum(1);
                            p1 = true;
                        }
                        else
                        {
                            registerPacket.setPlayerNum(2);
                            p2 = true;
                        }
                        server.sendToTCP(connection.getID(), registerPacket);
                        for(Block b : blocks)
                        {
                            b.sendPacket(connection.getID());

                            b.updateColors();

                        }
                    }


                }
                if(object instanceof PaddlePacket)
                {
                    PaddlePacket paddlePacket = (PaddlePacket)object;
                  // System.out.print(paddlePacket.getX() + "::" + paddlePacket.getPlayer() + "\n");

                    int x = paddlePacket.getX();
                    int y = (paddlePacket.getPlayer() ==1 ? 50 : 450);


                    if(!(x<0) && !(x>1001))
                    {
                        if(paddlePacket.getPlayer()==1)
                        {
                            paddle1.setBounds(paddlePacket.getX(), paddlePacket.getY(), paddlePacket.getWidth(), paddlePacket.getHeight());
                        }
                        else
                        {
                            paddle2.setBounds(paddlePacket.getX(), paddlePacket.getY(), paddlePacket.getWidth(), paddlePacket.getHeight());
                        }
                        server.sendToAllTCP(object);

                    }


                }




            }



        });




        setupBlocks();

        Executors.newSingleThreadScheduledExecutor().scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                update();
            }
        }, 10, 10, TimeUnit.MILLISECONDS);





        Executors.newSingleThreadScheduledExecutor().schedule(new Runnable() {
            @Override
            public void run() {

                startClient();
                for(Block b : blocks)
                {
                    b.sendPacket(1);

                    b.updateColors();

                }

            }
        }, 50, TimeUnit.MILLISECONDS);
    }
    boolean backup = true;
    long lastTick = System.currentTimeMillis();
    int locX = 250;
    int locY = 250;
    Rectangle rectangle = new Rectangle(locX,locY,1000 / 50, 1000 / 50);

    Rectangle paddle1 = new Rectangle(250, 0, 75, 25);
    Rectangle paddle2 = new Rectangle(250, 625, 75, 25);

    java.util.List<Block> blocks = new ArrayList<Block>();

    HashMap<Integer,JLabel> blockLabels = new HashMap<Integer, JLabel>();
    int width = 1000;
    int blockWidth = width / 50;
    int blockHeight = blockWidth;
    public void setupBlocks()
    {

        int middle = 350;
        //int y = 350;
        boolean up = true;
        int s = 1;
        for(int x = 0;x<=1001; x+=(x>blockHeight* 22 && x<blockHeight * 28 ? 200 : blockHeight))
        {
            for(int y = blockHeight * 14; y<= blockHeight * 19; y+=blockHeight)
            {
                //new Random().nextInt(1000000), x, y, blockWidth,blockHeight, this, 3)
                int id = this.id + 1;
                this.id+=1;
                blocks.add(new Block(id, x,y,blockWidth,blockHeight,this,s));
                if(y==blockHeight*19)
                {
                   if(up)
                   {
                       s++;
                   }
                    else
                   {
                       s--;
                   }
                    if(s==11)
                    {
                        s--;
                        up = false;
                    }
                    if(s == 0)
                    {
                        s++;
                        up = true;
                    }


                }




            }

        }


    }

    int score = 0;

    public void update()
    {
        // System.out.print("TICK\n");
        lastTick = System.currentTimeMillis();
        int newLeftX = locX + x;
        int newTopY = locY + y;
        int newRightX = locX + (int)rectangle.getWidth() + x;
        int newBottomY = locY + (int)rectangle.getWidth() - y;
        if(score>=10000 && !bigMode)
        {
            score-=10000;
            bigMode = true;
            Executors.newSingleThreadScheduledExecutor().schedule(new Runnable() {
                @Override
                public void run() {
                    bigMode = false;
                }
            }, 10, TimeUnit.SECONDS);
        }

        if(x>10)
        {
            x=5;
        }
        if(y>10)
        {
            y = 5;
        }

        if(newLeftX<0)
        {
            x = (x>0 ? x + 3 : -x);
        }
        if(newRightX > 1001)
        {
            x = (x>0 ? -x - 3 : x);
        }
        if(newTopY<1)
        {
            y = (y>0 ? y +3: -y);
        }
        if(newBottomY>701)
        {
            y = (y>0 ? -y -3: y);
        }
        Rectangle newRect = new Rectangle(newLeftX, newTopY,(int) rectangle.getWidth(), (int)rectangle.getHeight());

        List<Block> toRemove = new ArrayList<Block>();
        for(Block b : blocks)
        {
            if(b.getRectangle().intersects(newRect) && !toRemove.contains(b))
            {
                x = -x;
                boolean bounceBack = false;
                for(int i= b.getX(); i<=b.getX() + b.getWidth(); i++)
                {
                    if(newRect.contains(i,b.getY()))
                    {
                        bounceBack = true;
                    }
                    if(newRect.contains(i, b.getY() + b.getHeight()))
                    {
                        bounceBack = true;
                    }
                }
                if(y>0 && b.getY() > newRect.getY())
                {
                    y = -y - 1;
                }
                if(y<0 && b.getY() < newRect.getY())
                {
                    y = -y + 1;
                }

                
                    int sector1 = b.getWidth() / 3;
                    int sector2 = b.getWidth() / 3;
                    sector2*=2;
                    int sector3 = b.getWidth() /3;
                    sector3*=3;

                 
              

                    


                



                 //   y = -y;






              //  y = -y;
                b.setLevel(b.getLevel() - 1);
                System.out.print("B3");
                b.destroy();
                score+=100;
                System.out.print("A3");




                if(b.getLevel()<=0)
                {
                    toRemove.add(b);
                }
            }

        }
        for(Block b : toRemove)
        {
            blocks.remove(b);
        }



        if(newRect.intersects(paddle1))
        {
            boolean left = false;
            boolean center = false;
            
            int sector1 = (int)paddle1.getWidth() / 3 + (int)paddle1.getX();
            int sector2 = (int)paddle1.getWidth() / 3 * 2 + (int)paddle1.getX();
            int sector3 = (int)paddle1.getWidth()+ (int)paddle1.getX();
            if(isBetween(sector1,(int)paddle1.getCenterX(),sector3))
            {
                center = true;
            }
            if(!(newRect.getCenterX() > paddle1.getCenterX()))
            {
                left = true;
            }

            

            x = (center ? 0 : (left? -2 : 2));
            y = -y;
        }
        if(newRect.intersects(paddle2))
        {
            boolean left = false;
            boolean center = false;

            int sector1 = (int)paddle2.getWidth() / 3 + (int)paddle2.getX();
            int sector2 = (int)paddle2.getWidth() / 3 * 2 + (int)paddle2.getX();
            int sector3 = (int)paddle2.getWidth()+ (int)paddle2.getX();
            if(isBetween(sector1,(int)paddle2.getCenterX(),sector3))
            {
                center = true;
            }
            if(!(newRect.getCenterX() > paddle1.getCenterX()))
            {
                left = true;
            }



            x = (center ? 0 : (left? -2 : 2));
            y = -y;
        }

        locX = locX + x;
        locY = locY+ y;
        rectangle = new Rectangle(locX ,locY ,1000 / (bigMode ? 25 : 50),1000 / (bigMode ? 25 : 50));
        BallPacket ballPacket = new BallPacket();
        ballPacket.setX(locX);
        ballPacket.setY(locY);
        ballPacket.setWidth((int)rectangle.getWidth());
        ballPacket.setHeight((int)rectangle.getHeight());
        ballPacket.setScore(score);


        server.sendToAllTCP(ballPacket);
     //   System.out.print("S\n");





    }
    boolean bigMode = false;
}
