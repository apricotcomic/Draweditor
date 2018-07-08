
package Draweditor;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

abstract class Figure {
  protected int x,y,width,height;
  protected Color color;
  public Figure(int x,int y,int w,int h,Color c) {
    this.x = x; this.y = y;
    width = w; height = h;
    color = c;
  }
  //setSize:描画した図形の幅・高さを更新・保管するメソッド
  public void setSize(int w,int h) {
    width = w; height = h;
  }

  //setLocation:描画した図形のx座標、y座標を更新・保管するメソッド
  public void setLocation(int x,int y) {
    this.x = x; this.y = y;
  }

    abstract public void reshape(int x1,int y1,int x2,int y2);

    abstract public void paint(Graphics g);
}

class LineFigure extends Figure {
  public LineFigure(int x,int y,int w,int h,Color c) {
    super(x,y,w,h,c);
  }

//reshape:マウス操作に応じて再描画するメソッド
  public void reshape(int x1,int y1,int x2,int y2) {
    setLocation(x1,y1);
    setSize(x2,y2);
  }

//描画した図形をパネル上に表示させるメソッド
  public void paint(Graphics g) {
    g.setColor(color);
    g.drawLine(x,y,width,height);
  }
}

//CircleFigure:線を描画するクラス
class CircleFigure extends Figure {
  public CircleFigure(int x,int y,int w,int h,Color c) {
    super(x,y,w,h,c);
  }
  public void reshape(int x1,int y1,int x2,int y2) {
    int newx = Math.min(x1,x2);
    int newy = Math.min(y1,y2);
    int neww = Math.abs(x1 - x2);
    int newh = Math.abs(y1 - y2);
    setLocation(newx,newy);
    setSize(neww,newh);
  }
  public void paint(Graphics g) {
    g.setColor(color);
    g.drawOval(x,y,width,height);
  }
}

class RectangleFigure extends Figure {
  public RectangleFigure(int x,int y,int w,int h,Color c) {
    super(x,y,w,h,c);
  }
  public void reshape(int x1,int y1,int x2,int y2) {
    int newx = Math.min(x1,x2);
    int newy = Math.min(y1,y2);
    int neww = Math.abs(x1 - x2);
    int newh = Math.abs(y1 - y2);
    setLocation(newx,newy);
    setSize(neww,newh);
  }
  public void paint(Graphics g) {
    g.setColor(color);
    g.drawRect(x,y,width,height);
  }
}

class DrawApplication {
  protected Vector<Figure> figures;		//描画した図形を保存する
  protected Figure drawingFigure;			//描画する図形の情報を管理する
  protected String figurelabel;			//描画する図形の種類を判別する
  protected Color currentColor;			//描画する図形の色補判別する
  protected DrawPanel drawPanel;			//描画するパネルを判別する

  public DrawApplication() {
       figures = new Vector<Figure>();
       drawingFigure = null;
       currentColor = Color.red;
       figurelabel = "rect";
  }

  //setDrawPanel:DrawPanelクラスのインスタンスを設定
  public void setDrawPanel(DrawPanel c) {
       drawPanel = c;
  }

  //getNumberOfFigures:Vector配列Figuresの要素数を返す
  public int getNumberOfFigures() {
       return figures.size();
  }

  //getFigure:Figuersのi番目に格納されている図形を返す
  public Figure getFigure(int index) {
       return (Figure)figures.elementAt(index);
  }

  //createFigure:新たな図形を生成する
  public void createFigure(int x,int y) {
       Figure f = null;;
       if(figurelabel == "rect") f = new RectangleFigure(x,y,0,0,currentColor);
       else if(figurelabel == "circ") f = new CircleFigure(x,y,0,0,currentColor);
       else if(figurelabel == "line") f = new LineFigure(x,y,x,y,currentColor);

       figures.addElement(f);
       drawingFigure = f;
       drawPanel.repaint();
  }

  //reshapeFigure:生成した図形の再描画を行うメソッド
  public void reshapeFigure(int x1,int y1,int x2,int y2) {
       if (drawingFigure != null) {
            drawingFigure.reshape(x1,y1,x2,y2);
            drawPanel.repaint();
       }
  }

  //changecoloer:選択した色の情報を更新する
  public void changecolor(Color c){
       currentColor = c;
  }

  //changefigure:選択した図形の情報を更新する
  public void changefigure(String s){
       figurelabel = s;
  }

  //Vector配列Figuresの一番最後の要素を削除する
  public void undo(){
       figures.remove(figures.size()-1);
       drawPanel.repaint();
  }
}

//DrawPanel
//図形を描画するパネルを管理するクラス
//背景色の設定と図形を表示する
class DrawPanel extends JPanel {
     protected DrawApplication drawApplication;
     public DrawPanel(DrawApplication app) {
     setBackground(Color.white);
     drawApplication = app;
     }
     public void paintComponent(Graphics g) {
         super.paintComponent(g);
         //[すべてのFigureをpaintする]
         for(int i=0; i < drawApplication.getNumberOfFigures();i++){
              Figure f = drawApplication.getFigure(i);
              f.paint(g);
         }
     }
}

//DrawMouseListener
//マウスの操作情報を管理するクラス
class DrawMouseListener implements MouseListener,MouseMotionListener {
     protected DrawApplication drawApplication;
     protected int dragStartX,dragStartY;
     public DrawMouseListener(DrawApplication a) {
     drawApplication = a;
     }
     public void mouseClicked(MouseEvent e) {
     }
     public void mousePressed(MouseEvent e) {
     dragStartX = e.getX(); dragStartY = e.getY();
     if(SwingUtilities.isRightMouseButton(e) == true)
         drawApplication.undo();
     else if(SwingUtilities.isLeftMouseButton(e) == true)
         drawApplication.createFigure(dragStartX,dragStartY);
     }
     public void mouseReleased(MouseEvent e) {
     drawApplication.reshapeFigure(dragStartX,dragStartY,e.getX(),e.getY());
     }
     public void mouseEntered(MouseEvent e) { }
     public void mouseExited(MouseEvent e) { }
     public void mouseDragged(MouseEvent e) {
     drawApplication.reshapeFigure(dragStartX,dragStartY,e.getX(),e.getY());
     }
     public void mouseMoved(MouseEvent e) { }
}

//Select
//ボタンによる動作を管理・実行するクラス
class Select implements ActionListener {
	DrawApplication a;

    Select (DrawApplication ap){
        a = ap;
    }
    public void actionPerformed(ActionEvent e){ //設定の変更
        String es = e.getActionCommand();
        if(es.equals("red"))  a.changecolor(Color.red);
        if(es.equals("green"))  a.changecolor(Color.green);
        if(es.equals("blue"))  a.changecolor(Color.blue);
        if(es.equals("rect"))  a.changefigure("rect");
        if(es.equals("circ"))  a.changefigure("circ");
        if(es.equals("line"))  a.changefigure("line");
    }
}

//DrawMain
//フレーム・パネル・ブタンの設置・管理をする
class DrawMain {
    public static void main(String argv[]) {
    JFrame f = new JFrame("Draw");
    JPanel pc = new JPanel();
    JPanel pf = new JPanel();
    pc.setLayout(new GridLayout(1,3));
    pf.setLayout(new GridLayout(1,3));
    JButton r = new JButton("c.red");
    JButton g = new JButton("c.green");
    JButton b = new JButton("c.blue");

    JButton rect = new JButton("□");
    JButton circ = new JButton("○");
    JButton line = new JButton("─");

    r.setActionCommand("red");
    g.setActionCommand("green");
    b.setActionCommand("blue");

    rect.setActionCommand("rect");
    circ.setActionCommand("circ");
    line.setActionCommand("line");

    DrawApplication a = new DrawApplication();
    DrawPanel dp = new DrawPanel(a);
    a.setDrawPanel(dp);
    DrawMouseListener ml = new DrawMouseListener(a);
    dp.addMouseListener(ml);
    dp.addMouseMotionListener(ml);

    pc.add(r);
    pc.add(g);
    pc.add(b);

    pf.add(rect);
    pf.add(circ);
    pf.add(line);

    b.addActionListener(new Select(a));
    g.addActionListener(new Select(a));
    r.addActionListener(new Select(a));

    rect.addActionListener(new Select(a));
    circ.addActionListener(new Select(a));
    line.addActionListener(new Select(a));

    f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    f.getContentPane().add(dp,BorderLayout.CENTER);
    f.getContentPane().add(pc,BorderLayout.SOUTH);
    f.getContentPane().add(pf,BorderLayout.NORTH);

    f.setSize(400,300);
    f.setVisible(true);
    }
}

