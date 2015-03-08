import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.*;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Created by Andrew on 3/5/2015.
 */
public class Main extends JFrame implements KeyListener {
    int playerNum = 1;
    Client client;
    public void keyReleased(KeyEvent event)
    {

    }



    public void keyPressed(KeyEvent event)
    {

        //        }
        if(event.getKeyCode()==KeyEvent.VK_B)
        {
            BigModePacket bigModePacket =new BigModePacket();
            bigModePacket.setGo(true);
            client.sendTCP(bigModePacket);
        }
        PaddlePacket packet = new PaddlePacket();
        if(playerNum==1)
        {
            if(event.getKeyCode()==KeyEvent.VK_LEFT)
            {
                packet.setX(paddle1.getX() - 30);
                packet.setY(paddle1.getY());
                packet.setWidth(paddle1.getWidth());
                packet.setHeight(paddle1.getHeight());
                packet.setPlayer(playerNum);

                client.sendTCP(packet);
            }
            else if(event.getKeyCode()==KeyEvent.VK_RIGHT)
            {
                packet.setX(paddle1.getX() + 30);
                packet.setPlayer(playerNum);
                packet.setY(paddle1.getY());
                packet.setWidth(paddle1.getWidth());
                packet.setHeight(paddle1.getHeight());
                client.sendTCP(packet);
            }
        }
        else
        {
            if(event.getKeyCode()==KeyEvent.VK_LEFT)
            {
                packet.setX(paddle2.getX() - 30);
                packet.setY(paddle2.getY());
                packet.setWidth(paddle2.getWidth());
                packet.setHeight(paddle2.getHeight());
                packet.setPlayer(playerNum);

                client.sendTCP(packet);
            }
            else if(event.getKeyCode()==KeyEvent.VK_RIGHT)
            {
                packet.setX(paddle2.getX() + 30);
                packet.setPlayer(playerNum);
                packet.setY(paddle2.getY());
                packet.setWidth(paddle2.getWidth());
                packet.setHeight(paddle2.getHeight());
                client.sendTCP(packet);
            }
        }

    }
    public void keyTyped(KeyEvent event)
    {

    }
    public static void main(String[] args)
    {

        if(args.length>0 && args[0].equalsIgnoreCase("server"))
        {
            new GameServer();
        }
        else
        {
            new Main();
        }

    }




    JLabel ball = new JLabel();

    JLabel paddle1 = new JLabel("Player 1");
    JLabel paddle2 = new JLabel("Player 2");
   public int score = 0;
    JLabel scoreLabel = new JLabel("$-1");
    public void updateLabel()
    {
        scoreLabel.setText("$" + score);
        repaint();
    }
    public Main()
    {

        setBackground(Color.BLACK);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
  //      setOpacity(1);


        addKeyListener(this);
        setBounds(0,0,1001,700);

        scoreLabel.setBounds(getWidth() / 2,10,getWidth(),50);

        paddle2.setBounds(getWidth() /2, getHeight() - 75, 120, 50);
        paddle1.setBounds(getWidth() / 2,0,120,50);
        ball.setOpaque(true);
        ball.setBackground(Color.YELLOW);
        add(paddle1);
        add(paddle2);
        add(ball);
        add(scoreLabel);
        paddle2.setOpaque(true);
        paddle2.setBackground(Color.RED);
        paddle1.setOpaque(true);
        paddle1.setBackground(Color.RED);
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
            kryo.register(ScorePacket.class);
            kryo.register(BigModePacket.class);

            client.start();
            client.connect(10000,"127.0.0.1", 45666,45777 );


        }catch(Exception ex){ex.printStackTrace();}

      client.addListener(new Listener()
      {
          public void received(Connection connection, Object object)
          {
          //    repaint();

        //      System.out.print("R\n");


              if(object instanceof RegisterPacket)
              {
                  RegisterPacket packet = (RegisterPacket)object;
                  playerNum = packet.getPlayerNum();
                  PaddlePacket paddlePacket = new PaddlePacket();
                  if(playerNum==1)
                  {
                      paddlePacket.setHeight(paddle1.getHeight());
                      paddlePacket.setWidth(paddle1.getWidth());
                      paddlePacket.setX(paddle1.getX());
                      paddlePacket.setY(paddle1.getY());
                      paddlePacket.setPlayer(playerNum);
                  }
                  else
                  {
                      paddlePacket.setHeight(paddle2.getHeight());
                      paddlePacket.setWidth(paddle2.getWidth());
                      paddlePacket.setX(paddle2.getX());
                      paddlePacket.setY(paddle2.getY());
                      paddlePacket.setPlayer(playerNum);
                  }


              }
              if(object instanceof PaddlePacket)
              {
                  PaddlePacket paddlePacket = (PaddlePacket)object;
                  if(paddlePacket.getPlayer()==1)
                  {
                      paddle1.setLocation(paddlePacket.getX(), 50);
                      repaint();
                  }
                  else
                  {
                      paddle2.setLocation(paddlePacket.getX(), 700 -(paddlePacket.getHeight() * 2));
                      repaint();
                  }
              }
              if(object instanceof BallPacket)
              {
                  BallPacket ballPacket = (BallPacket)object;
                  ball.setBounds(ballPacket.getX(),ballPacket.getY(), ballPacket.getWidth(), ballPacket.getHeight());
                 // repaint();
                  score = ballPacket.getScore();
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
                  blocks.put(blockPacket.getID(), jButton);
                  repaint();



              }
              if(object instanceof BreakPacket)
              {
                 if(blocks.containsKey(((BreakPacket)object).getID()))
                 {
                     remove(blocks.get(((BreakPacket)object).getID()));
                 }

                  if(blocks.containsKey(((BreakPacket)object).getID()))
                  {
                      blocks.remove(((BreakPacket)object).getID());
                    //  score+=100;
                      updateLabel();
                  }

                  repaint();

              }
              if(object instanceof ColorUpdatePacket)
              {
                  ColorUpdatePacket colorUpdatePacket = (ColorUpdatePacket)object;

                  JLabel block = blocks.get(colorUpdatePacket.getID());
                  if(block!=null)
                  {
                      block.setBackground(new Color(colorUpdatePacket.getR(),colorUpdatePacket.getG(),colorUpdatePacket.getB()));
                  }

                  repaint();

              }

          }



      });

        ConnectPacket connectPacket = new ConnectPacket();
        client.sendTCP(connectPacket);
        PaddlePacket packet = new PaddlePacket();
        packet.setX(paddle2.getX() - 10);
        packet.setPlayer(playerNum);

        PaddlePacket paddlePacket = new PaddlePacket();




        client.sendTCP(packet);


    }
    JLabel scorePacket = new JLabel();
    HashMap<Integer,JLabel> blocks = new HashMap<Integer, JLabel>();
}
