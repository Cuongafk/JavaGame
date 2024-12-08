package TEST;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.im.InputContext;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Locale;

import java.util.Random;
import java.util.Set;

public class hinhvuong extends JFrame {

    public Dimension scr = Toolkit.getDefaultToolkit().getScreenSize();
    public int scw = (int) scr.getWidth();
    public int sch = (int) scr.getHeight();

    public hinhvuong() {
        setTitle("MINECRAFT 2D");
        setSize(scw, sch);
        setExtendedState(JFrame.MAXIMIZED_BOTH);

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setUndecorated(true); // Ẩn thanh tiêu đề và thanh Taskbar
        add(new SquarePanel());
        setLocationRelativeTo(null);
        // Chỉnh bàn phím sang tiếng Anh khi chương trình chạy
        InputContext inputContext = getInputContext();
        inputContext.selectInputMethod(new Locale("en", "US"));
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            hinhvuong slowDownSquare = new hinhvuong();
            slowDownSquare.setVisible(true);
        });
    }
}

class SquarePanel extends JPanel {

    public Dimension scr = Toolkit.getDefaultToolkit().getScreenSize();
    public int cr = (int) scr.getWidth();
    public int cc = (int) scr.getHeight() - 30;

    int x = 250, y = 250; // Vị trí ban đầu của hình vuông
    int vx = 0, vy = 0; // Vận tốc theo trục x và y
//    int ground=sch-250;
    int gravity = 10; // Giá trị gia tốc trọng lực
    int fallAcceleration = 10; // Giá trị gia tốc rơi
    int vtx = 20;
    int vty = 50;
    private double frictiony = 0.9; // Hệ số ma sát
    private double frictionx = 0; // Hệ số ma sát

    boolean canJump = true; // Biến để kiểm tra xem hình vuông có thể nhảy hay không
    boolean ccay = false;
    boolean fcg = false;
    boolean ittb = false;
    boolean xmitem = false;

    // Khai báo một cờ để theo dõi xem phím đã được giữ hay chưa
    private boolean isKeyPressed = false;
    // Sử dụng HashMap để lưu trữ trạng thái của các phím
    private HashMap<Integer, Boolean> keyState = new HashMap<>();
    ArrayList<MapObject> mapObjects = new ArrayList<>(); // Khởi tạo danh sách mapObjects;
    ArrayList<TextObject> textObjects = new ArrayList<>();
    ArrayList<ItemObject> itemObjects = new ArrayList<>();

    public class MapObject {

        private int x;
        private int y;
        private int width;
        private int height;
        public Color color;

        public MapObject(int x, int y, int width, int height, Color color) {
            this.x = x;
            this.y = y;
            this.width = width;
            this.height = height;
            this.color = color;
        }

        public void draw(Graphics g) {

            g.setColor(color);
            g.fillRect(x, y, width, height);
        }
    }

    public class TextObject {

        private int x;
        private int y;
        private int size;
        public Color color;
        private String text;

        public TextObject(int x, int y, int size, Color color, String text) {
            this.x = x;
            this.y = y;
            this.size = size;
            this.text = text;
            this.color = color;
        }

        public void drawT(Graphics g) {
            try {
                g.setFont(new Font("Arial", Font.PLAIN, size));
                g.setColor(black);
                g.fillRect(x - 2, y + 5, text.length() * 10 + 10, -25);
                g.setColor(color);
                g.drawString(text, x, y); // Vẽ chữ
            } catch (Exception e) {
                System.out.println("ERROR SIZE TEXT I THINK!");
            }

        }
    }

    public class ItemObject {

        private int x = 20;
        private int y = 50;
        private String Maitem;
        private int sl;

        public ItemObject(String Maitem, int sl) {
            try {
                //delete item=0
                itemObjects.removeIf(item -> item.sl == 0);

                // Kiểm tra xem vật phẩm đã tồn tại hay không
                ItemObject item = null;
                for (ItemObject it : itemObjects) {
                    if (it.Maitem.equals(Maitem)) {
                        item = it;
                        break;
                    }
                }

// Nếu vật phẩm đã tồn tại, thì cập nhật số lượng của vật phẩm đó lên sl
                if (item != null) {
                    item.sl += sl;
                } else {
// Tạo đối tượng mới
                    this.Maitem = Maitem;
                    this.sl = sl;
                }
            } catch (Exception e) {
                System.out.println("ERROR I SEE ITEM ZERO!");
            }

        }

        public void drawItem(Graphics g, int i) {
            if ("gỗ".equals(Maitem)) {
                g.setColor(gon);
            }
            if ("đá".equals(Maitem)) {
                g.setColor(da);
            }
            if ("than".equals(Maitem)) {
                g.setColor(Color.BLACK);
            }
            if ("sắt".equals(Maitem)) {
                g.setColor(iron);
            }
            if ("vàng".equals(Maitem)) {
                g.setColor(Color.YELLOW);
            }
            if ("kim cương".equals(Maitem)) {
                g.setColor(kc);
            }

            g.fillRect(x, y + i * 30, 25, 25);
            g.setColor(Color.BLACK);
            g.setFont(new Font("Arial", Font.BOLD, 20));
            if (Maitem != null) {
                g.drawString(Maitem + "x" + String.valueOf(sl), x + 30, y + 20 + i * 30); // Vẽ chữ
            }
        }

    }

    public SquarePanel() {

        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                int key = e.getKeyCode();
                keyState.put(key, true); // Đặt trạng thái của phím là true
            }

            @Override
            public void keyReleased(KeyEvent e) {
                int key = e.getKeyCode();
                keyState.put(key, false); // Đặt trạng thái của phím là false
            }

        });
        setFocusable(true); // Cho phép panel nhận focus để xử lý sự kiện phím
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                int key = e.getKeyCode();
                if (key == KeyEvent.VK_A) {
                    vx = -vtx; // Di chuyển sang trái
                    isKeyPressed = true; // Đặt cờ khi phím được giữ
                } else if (key == KeyEvent.VK_D) {
                    vx = vtx; // Di chuyển sang phải
                    isKeyPressed = true; // Đặt cờ khi phím được giữ
                } else if (key == KeyEvent.VK_W && canJump == true) {
                    vy = -vty; // Di chuyển lên trên
                    isKeyPressed = true; // Đặt cờ khi phím được giữ
                    canJump = false;
                } else if (key == KeyEvent.VK_C) {
                    canJump = true; // game mode c
                    vtx = 500;
                    vty = 500;
                } else if (key == KeyEvent.VK_S) {
                    vy = vty; // Di chuyển xuống dưới
                } else if (key == KeyEvent.VK_E) {
                    if (ittb) {
                        ittb = false;
                    } else {
                        ittb = true;
                    }
                } else if (key == KeyEvent.VK_F) {

                    if (fcg == true) {
                        ccay = true;
                    }
                    if (dkitem == true) {
                        xmitem = true;
                    }
                    if (xnt) {
                        xnt = false;
                        dd = true;
                    }

                } else if (key == KeyEvent.VK_ESCAPE) {
                    restart();//Thoat
                }

            }

            @Override
            public void keyReleased(KeyEvent e) {
                isKeyPressed = false; // Hủy bỏ cờ khi phím được nhả ra
            }

            public void restart() {
                // Thoat chương trình
                System.exit(0);
            }

        });

        Timer timer = new Timer(50, (ActionEvent e) -> {
            // Kiểm tra cờ trước khi áp dụng ma sát
            if (!isKeyPressed) {
                // Kiểm tra trạng thái của các phím để cập nhật vận tốc
                if (keyState.getOrDefault(KeyEvent.VK_A, false)) {
                    vx = -vtx; // Di chuyển sang trái
                } else if (keyState.getOrDefault(KeyEvent.VK_D, false)) {
                    vx = vtx; // Di chuyển sang phải
                } else if (keyState.getOrDefault(KeyEvent.VK_W, false) && canJump == true) {
                    vy = -vty; // Di chuyển lên trên
                    canJump = false;

                } else {
                    vx *= frictionx; // Áp dụng lực ma sát
                }
                // Áp dụng ma sát nếu phím không được giữ

            }
            vy *= frictiony; // Áp dụng ma sát theo trục y

            x += vx; // Cập nhật vị trí theo tốc độ theo trục x
            y += vy; // Cập nhật vị trí theo tốc độ theo trục y

            vy += gravity; // Áp dụng trọng lực
//                vy += fallAcceleration; // Áp dụng gia tốc rơi

            if (y > cc - ghmp) {
                y = cc - ghmp; // Giới hạn hình vuông ở mặt phẳng đỡ
//                    System.out.println(cc);
                vy = 0; // Dừng khi chạm mặt phẳng đỡ
                canJump = true;
            }

//                System.out.println(vx);//Giá trị gia tốc rơi
            if (x > cr) {
                mm++;
                x = 0;
            }
            if (x < 0) {
                mm--;
                x = cr;
            }
            map(mm); // Thêm đối tượng bản đồ ban đầu

            repaint(); // Vẽ lại hình vuông ở vị trí mới
        });

//        mapObjects 
        timer.start(); // Bắt đầu timer
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        //times
        times++;
//        System.out.println("times=" + times);

        // Vẽ các đối tượng bản đồ
        mapObjects.forEach((mapObject) -> {
            mapObject.draw(g);
        });
        // Vẽ các đối tượng chữ
        textObjects.forEach((textObject) -> {
            textObject.drawT(g);
        });
        // Vẽ các đối tượng item
        try {
            if (ittb == true) {
                //delete item=0
                itemObjects.removeIf(item -> item.sl == 0);
                g.setColor(Color.WHITE);
                g.fillRect(0, 0, 200, 100 + itemObjects.size() * 30);
                g.setColor(Color.BLACK);
                g.setFont(new Font("Arial", Font.BOLD, 30));
                g.drawString("<Túi Đồ>", 20, 40);
                int i = 0;
                for (ItemObject itemObject : itemObjects) {
                    if (itemObject != null) {
                        itemObject.drawItem(g, i++);
                    }
                }
            }
        } catch (Exception e) {
            System.out.println("ERROR I CAN'T ART ITEM");
        }

    }
    public int times = 0;
    boolean pbb = false;
    int timepro = 1;

    private Color SkyColor() {

        if (times % 1 == 0) {
            //sun

            ckmt += 4 + (timepro - 1) * 5;
            //xe mo
            if (bd == true && xmhd) {
                xxm = ckmt;
            } else {
                xxm = -100;
            }

            if (ckmt > cr) {
                if (bd && pbb == false) {
                    ckmt = 50;
                    pbb = true;
                }
                if (bs && pbb) {
                    ckmt = 50;
                    pbb = false;
                }
            }
//            System.out.println(ckmt);

            //sáng 0-->200
            if (bs) {
                g = b % 2;
                b += timepro;
                if (b <= 50) {
                    r--;
                }
                if (b >= 200) {
                    bt = true;
                    bs = false;
                }
            }
            //trưa 200-->220
            if (bt) {
                g = b / 2;
                if (b >= 220) {
                    b += timepro;
                } else {
                    d += timepro;
                }
                if (d >= 80) {
                    bt = false;
                    bc = true;
                    d = 0;
                }
            }
            //chiều 250-->50
            if (bc) {
                g = b / 2;
                b -= timepro;
                if (b <= 100) {
                    r++;
                }
                if (b <= 50) {
                    r = 0;
                    bd = true;
                    bc = false;
                }
            }
            //tối 50-->0
            if (bd) {
                mctb = Color.WHITE;
                g = 0;
                d += timepro;
                if (b > 0) {
                    b -= timepro;
                }
                if (d >= 510) {
                    bs = true;
                    bd = false;
                    r = 50;
                    d = 0;
                }
            } else {
                mctb = Color.BLACK;
            }

        }
//        System.out.println(r + "," + g + "," + b);
        return new Color(r, g, b, 255 - b); // Trả về màu sắc với giá trị màu xanh được tính toán

    }

    public Color[] rdColor(int sl) {
        Color[] rdcl = new Color[sl];
        int ks = 1;
        if (dltm < 3) {
            ks = 65;
        }
        if (dltm == 0) {
            ks = 100;
        }
        for (int i = 0; i < sl; i++) {
            int rdt = (rd.nextInt(ks) + 1);
            if (rdt <= 35) {
                rdcl[i] = da;//35%
            } else if (rdt <= 65) {
                rdcl[i] = Color.BLACK;//30%
            } else if (rdt <= 85) {
                rdcl[i] = iron;//20%
            } else if (rdt <= 95) {
                rdcl[i] = Color.YELLOW;//10%
            } else if (rdt <= 100) {
                rdcl[i] = kc;//5%
            }
        }

        return rdcl;
    }

    public void ktm(Color m) {
        if (m == da) {
            itemObjects.add(new ItemObject("đá", 1));
        }
        if (m == Color.BLACK) {
            itemObjects.add(new ItemObject("than", 1));
        }
        if (m == iron) {
            itemObjects.add(new ItemObject("sắt", 1));
        }
        if (m == Color.YELLOW) {
            itemObjects.add(new ItemObject("vàng", 1));
        }
        if (m == kc) {
            itemObjects.add(new ItemObject("kim cương", 1));
        }

    }

    public Random rd = new Random();

    public int[][] rdb(int sl) {
        int rd3[][] = new int[50][sl];
        for (int j = 0; j < 50; j++) {
            for (int i = 0; i < sl; i++) {
                rd3[j][i] = rd.nextInt(20) * 50;
            }

        }
        return rd3;
    }

    public int[][][] rdl(int slc, int slx, int sly, int d, int c) {
        int mrdl[][][] = new int[slc][sly][slx];
        for (int z = 0; z < slc; z++) {
            for (int j = 0; j < sly; j++) {
                for (int i = 0; i < slx; i++) {
                    mrdl[z][j][i] = rd.nextInt(c) + d;
                }
            }
        }
        return mrdl;
    }

    public int[] rdxpro(int sl, int d, int c) {
        int mrdc[] = new int[sl];
        Set<Integer> generated = new HashSet<>(); // Sử dụng Set để lưu các giá trị đã tạo

        int i = 0;
        while (i < sl) {
            int randomValue = rd.nextInt(c) + d; // Tạo số ngẫu nhiên

            if (!generated.contains(randomValue)) { // Kiểm tra nếu giá trị chưa tồn tại
                mrdc[i] = randomValue;
                generated.add(randomValue); // Thêm giá trị vào Set
                i++;
            }
        }

        return mrdc;
    }

    public int[] rdx(int sl, int d, int c) {
        int rd1[] = new int[sl];
        for (int i = 0; i < sl; i++) {
            rd1[i] = rd.nextInt(c) + d;
        }
        return rd1;
    }

    public int[] rdy(int sl, int d, int c) {
        int rd2[] = new int[sl];
        for (int i = 0; i < sl; i++) {
            rd2[i] = rd.nextInt(c) + d;
        }
        return rd2;
    }

    public void ksmine(int sl) {
        for (int i = 0; i < sl; i++) {
            clmine = rdColor(sl);
        }
    }

    public void hpcay(int sl) {
        Arrays.fill(cdbc, sl);
    }

    //reset cay
    public void resetrdc() {
        if (rdc == true && bd == false) {
            mrdcx = rdxpro(slc, 0, cr / 150);
            mrdcy = rdy(slc, 3, 3);
            rdly = rdy(slc, 2, 3);
            rdlx = rdl(slc, 6, 4, 3, 3);
            hpcay(hpc);
            rdc = false;

        }
    }

    //reset sao
    public void resetrds() {
        if (rds == true && bs == true) {
            mrdsx = rdx(100, 0, cr);
            mrdsy = rdy(100, 0, cc);
            rds = false;
        }
    }
//reset may

    public void resetrd(boolean mct, int sl) {
        if (mct) {
            mrdm = rdb(sl);
            vtt = -cr;
            mqm++;
            if (mqm == 3) {
                mqm = -2;
            }
            mct = false;
        }
    }

    //dung cau thang
    public void dct(boolean b) {
        if (b == true) {
            gravity = 0;
            ghmp = -100;
            canJump = true;
            vty = 15;
            frictionx = 0; // Hệ số ma sát
        } else {
            gravity = 10;
            ghmp = 200;
            vty = 50;

            frictionx = 0; // Hệ số ma sát
        }
    }

    public void checkItem(String Maitem, int sl) {
        try {
            for (ItemObject item : itemObjects) {
                if (item.Maitem.equals(Maitem)) {
                    if (item.sl >= sl) {
                        textObjects.add(new TextObject(x - 20, y - 20, 20, Color.WHITE, "Xát nhận[F]"));
                        dkitem = true;
                        if (xmitem) {
                            item.sl -= sl;
                            dkitem = false;
                            xmitem = false;
                            tb = true;
                            dd = true;
                            break;
                        } else {
                            break;
                        }
                    } else {
                        textObjects.add(new TextObject(x - 50, y - 20, 20, Color.WHITE, "Số lượng không đủ"));
                    }
                }
            }
            if (tb == true) {
                Mit = Maitem;
                slitem = -sl;
            }
        } catch (Exception e) {
            System.out.println("ERROR ITEM IS NULL I HATE THAT!");
        }

    }

    public void TB(String MIT, int sl) {
        de++;
        if (sl > 0) {
            textObjects.add(new TextObject(x - 20, y - 20 - de * 5, 20, Color.WHITE, "+" + String.valueOf(sl) + " " + MIT));
        } else {
            textObjects.add(new TextObject(x - 20, y - 20 - de * 5, 20, Color.WHITE, String.valueOf(sl) + " " + MIT));
        }
        if (de > 5) {
            tb = false;
            de = 0;
        }

    }

    public void CB() {
        if (bd) {
            blbs = true;
        }
        if (blbs == true && bd == false) {
            blbs = false;
            newbs = true;
        }

        if (bd != true) {
            blbd = true;
        }
        if (blbd == true && bd) {
            blbd = false;
            newbd = true;
        }
    }

    public void ttm(int sl) {
        //body

        for (int i = sl; i > 0; i--) {
            xm[i] = xm[i - 1];
            ym[i] = ym[i - 1];
        }
        int h = 0;

        tm = h;
       
        if (xm[0] < x) {
            h = 1;
             xm[0] += 50;
        }
        if (xm[0] > x) {
            h = 2;
            xm[0] -= 50;
        }
        if (ym[0] < y) {
            h = 3;
            ym[0] += 50;
        }
        if (ym[0] > y ) {
            h = 4;
            ym[0] -= 50;
        }
        
         h = rd.nextInt(4) + 1;

        switch (h) {
            case 1:
                if (xm[0] < cr) {
                    xm[0] += 50;
                }
                break;
            case 2:
                if (xm[0] > 0) {
                    xm[0] -= 50;
                }
                break;
            case 3:
                if (ym[0] < cc) {
                    ym[0] += 50;
                }
                break;
            case 4:
                if (ym[0] > 0) {
                    ym[0] -= 50;
                }
                break;

        }

        for (int i = 0; i < sl; i++) {
            Color ma = new Color(255, 255, 255, 255 - i * 25);
            mapObjects.add(new MapObject(xm[i] + i * 2, ym[i] + i * 2, 50 - i * 4, 50 - i * 4, ma));
           
        }
        if(x>xm[0]-20 && x<xm[0]+70 && y<ym[0] +70 && y>ym[0]-20){
            st=true;
            y-=100;
            if(x<xm[0]+20){
                x-=50;
            }else
            if(x>xm[0]){
                x+=50;
            }
        }else{
            st=false;
        }

    }

    public int d = 0;
    public boolean bs = true;
    public boolean bt, bc, bd = false;
    public int b = 0;
    public int g = 0;
    public int r = 50;
    //color map
    Color dat = new Color(139, 69, 19);
    Color co = new Color(0, 150, 0);
    Color la = new Color(0, 100, 0, 100);
    Color black = new Color(0, 0, 0, 100);
    Color da = new Color(128, 128, 128);
    Color dak = new Color(110, 110, 110);
    Color nda = new Color(50, 50, 50);
    Color nenda = new Color(100, 100, 100);
    Color go = new Color(210, 180, 100);
    Color daNg = new Color(250, 180, 140);
    Color ao = new Color(0, 240, 240);
    Color quan = new Color(0, 100, 250);
    Color cat = new Color(255, 240, 200);
    Color bien = new Color(0, 100, 255, 100);
    Color kc = new Color(0, 200, 255);
    Color as = new Color(255, 255, 255, 100);
    Color lava = new Color(255, 50, 0);
    Color gon = new Color(89, 66, 53);
    Color iron = new Color(255, 255, 255);
    Color nether = new Color(20, 0, 20);
    Color mctb;

    //mang rd
    int mrd[][] = rdb(10);
    int mrdm[][] = rdb(5);

    int mrdsx[] = rdx(100, 0, cr);
    int mrdsy[] = rdy(100, 0, cc);

    int slc = 5;
    int mrdcx[] = rdxpro(slc, 0, cr / 150);
    int mrdcy[] = rdy(slc, 3, 3);

    int rdlx[][][] = rdl(slc, 6, 4, 3, 3);
    int rdly[] = rdy(slc, 2, 3);

//    int dbc[]=new int[slc];
    int rdkx[] = rdx(100, 0, cr / 25);
    int rdky[] = rdy(100, 0, (cc - 150) / 25);

    Color[] clrd = rdColor(100);
    Color[] clmine = new Color[100];
    int cdbc[] = new int[slc];

    int rdclmine[];
    int xm[] = new int[100];
    int ym[] = new int[100];

    boolean newbs = false;
    boolean newbd = false;
    boolean blbs = true;
    boolean blbd = false;

    boolean rds = false;
    boolean rdc = true;
    boolean mct = false;
    boolean ct = false;
    boolean tb = false;
    boolean dkitem = false;
    boolean dd = false;
    boolean xnt = false;
    boolean xmhd = true;
    boolean xmd = false;
    boolean xnm = true;
    boolean bdm = false;
    boolean st = false;

    String Mit;
    int slitem;
    int vtt = -cr;
    int mqm = -2;
    int ckmt = 50;
    int ghmp = 0;
    int de = 0;
    int dltm = 0;
    int xxm = 0;
    int anmt = 0;
    int hpc = 20;
    int slmine = 5;
    int tm = 0;

    //map
    int mm = 100;

    // Trong phương thức map
    public void map(int m) {
        System.out.println("dtm=" + mapObjects.size());
//        System.out.println("dtc=" + textObjects.size());
//        System.out.println("dtitem=" + itemObjects.size());
        mapObjects.clear();
        textObjects.clear();
//        itemObjects.removeIf(item -> item.sl == 0);

//}
//        itemObjects.clear();
        //tao cay
        if (bd) {
            rdc = true;
        }
        resetrdc();
        //chuyen buoi
        CB();
        //lặp sinh khoán
        if (newbd) {
            xnm = true;
            ksmine(slmine);
        }

//   Tính toán màu của bầu trời
        Color bautroi = SkyColor();
        if (mm < 10) {

            mapObjects.add(new MapObject(0, 0, cr, cc, bautroi));

            Color may = new Color(255, 255, 255, b / 2 + 50);

            //sao
            resetrds();
            if (bd) {
                rds = true;

                for (int i = 0; i < mrdsx.length; i++) {
                    int k = 2;
                    if (i > 90) {
                        k = rd.nextInt(4);
                    }
                    mapObjects.add(new MapObject(mrdsx[i], mrdsy[i], k, k, Color.WHITE));
                }
            }

            //mặt trời
            if (bd) {
                mapObjects.add(new MapObject(0 + ckmt, 100, 50, 50, as));
                mapObjects.add(new MapObject(10 + ckmt, 110, 30, 30, Color.WHITE));
            } else {
                Color sun = new Color(255, 255 - r, 200 - r);
                mapObjects.add(new MapObject(0 + ckmt, 100, 50, 50, sun));
                mapObjects.add(new MapObject(10 + ckmt, 110, 30, 30, as));
            }
            //may

            if (vtt >= cr) {
                resetrd(mct, 5);
                mct = true;
            } else {
                vtt += 10 + (timepro - 1) * 10;//tốc độ bay của mây
            }
            if (m == mqm) {
                for (int vt : mrdm[0]) {
                    mapObjects.add(new MapObject(vt + vtt, 150, vt + 50, 50, may));
//                        System.out.println(vtt+","+cr);
                }
                for (int vt : mrdm[1]) {
                    mapObjects.add(new MapObject(vt + vtt, 200, vt + 50, 50, may));
                }
            }
        }
        switch (m) {
            case -2:
                ghmp = 200;
                //bien
                if (x < cr - 500) {
                    x = cr - 500;
                }
                mapObjects.add(new MapObject(0, cc - 100, cr - 500, 150, bien));
                mapObjects.add(new MapObject(0, cc - 50, cr - 500, 150, black));
                //dat
                mapObjects.add(new MapObject(cr, cc - 150, -500, 100, cat));
                mapObjects.add(new MapObject(cr, cc - 50, -500, 100, da));

                break;
            case -1:

                //dat
                mapObjects.add(new MapObject(0, cc - 50, cr, 100, da));
                mapObjects.add(new MapObject(0, cc - 150, cr, 100, dat));
                mapObjects.add(new MapObject(cr, cc - 150, -550, 10, co));
                mapObjects.add(new MapObject(0, cc - 150, cr - 600, 10, co));
                //cau thang
                mapObjects.add(new MapObject(cr - 570, cc - 160, 10, 160, go));
                mapObjects.add(new MapObject(cr - 590, cc - 160, 10, 160, go));
                mapObjects.add(new MapObject(cr - 570, cc - 160, 10, 160, black));
                mapObjects.add(new MapObject(cr - 590, cc - 160, 10, 160, black));

                for (int i = 0; i < 16; i += 2) {
                    mapObjects.add(new MapObject(cr - 555-40, cc - 150 + (i * 10), 40, 5, go));
                }
                //cai ho
                for (int j = 0; j < 5; j++) {
                    for (int i = 0; i < j; i++) {
                        mapObjects.add(new MapObject(cr - 550-50, cc - (i * 50), 50, 50, black));
                    }
                }
                //bong
                for (int vt : mrd[2]) {
                    mapObjects.add(new MapObject(vt / 5, cc - 100, vt, 150, black));
                }
                // dk xuong
                if (x + 20 > cr - 500 - 100 && x + 20 < cr - 500 - 50 && y + 50 >= cc - 150) {

                    dct(true);
                    if (y > cc) {
//                        dct(false);
                        x = 55;
                        y = 0;
                        mm = 100;
                    }

                    textObjects.add(new TextObject(x - 20, y - 20, 20, Color.WHITE, "Xuống [S]"));
                } else {
                    ghmp = 200;
                    dct(false);
                }
                mapObjects.add(new MapObject(cr - 500 - 50, y + 50, -50, 1, Color.red));
                break;

            case 0:
                ghmp = 200;
                int demf = 0;

                //dat
                mapObjects.add(new MapObject(0, cc - 150, cr, 100, dat));
                mapObjects.add(new MapObject(0, cc - 50, cr, 100, da));
                mapObjects.add(new MapObject(0, cc - 150, cr, 10, co));
                for (int vt : mrd[0]) {
                    mapObjects.add(new MapObject(vt, cc - 100, vt + 50, 150, black));
                }
                //cay

                for (int j = 0; j < mrdcx.length; j++) {
                    //chat cay
                    if (mrdcx[j] * 150 + 125 <= x + 20 && mrdcx[j] * 150 + 200 >= x + 20) {
                        textObjects.add(new TextObject(x - 20, y - 20, 20, Color.WHITE, "Chặt cây[F] " + hpc + "/" + String.valueOf(cdbc[j])));
                        fcg = true;
                        if (ccay == true) {
                            cdbc[j]--;
                            y -= 50;
                            if (cdbc[j] <= 0) {
                                tb = true;
                                itemObjects.add(new ItemObject("gỗ", 1));
                                mrdcx[j] = -5;
                            }
                            ccay = false;

                        }
                    } else {
                        fcg = false;
                        demf++;
                    }
                    if (demf != slc) {
                        fcg = true;
                    } else {
                        fcg = false;
                    }
                    


                    //cay
                    for (int i = 0; i < mrdcy[j]; i++) {
                        mapObjects.add(new MapObject(mrdcx[j] * 150 + 150, cc - 175 - i * 25, 25, 25, gon));
                    }
                    //lá
                    for (int ii = 0; ii < rdly[j]; ii++) {
                        for (int l : rdlx[j][ii]) {
                            mapObjects.add(new MapObject(mrdcx[j] * 150 + 50 + l * 25, cc - 175 - (mrdcy[j] - 1) * 25 - ii * 25, 25, 25, la));
                        }
                    }

                }

                if (tb == true) {
                    TB("gỗ", 1);
                }

                break;
            case 1:
                ghmp = 200;
                //dat
                mapObjects.add(new MapObject(0, cc - 150, cr, 100, dat));
                mapObjects.add(new MapObject(0, cc - 50, cr, 100, da));
                mapObjects.add(new MapObject(0, cc - 150, cr, 10, co));
                for (int vt : mrd[1]) {
                    mapObjects.add(new MapObject(vt, cc - 100, vt + 50, 150, black));
                }
                break;
            case 3:

                break;
            case 100:
                ghmp = 200;
                //bien
                if (x < 50) {
                    x = 50;
                }
                mapObjects.add(new MapObject(0, 0, cr, cc, nenda));
                //lặp sinh khoán
                for (int i = 0; i < rdkx.length; i++) {
                    mapObjects.add(new MapObject(rdkx[i] * 25, rdky[i] * 25, 25, 25, dak));
                    mapObjects.add(new MapObject(rdkx[i] * 25 + 4, rdky[i] * 25 + 5, 8, 4, clrd[i]));
                    mapObjects.add(new MapObject(rdkx[i] * 25 + 14, rdky[i] * 25 + 15, 8, 4, clrd[i]));

                }

                //nen khoan
                for (int i = 0; i < 18; i++) {
                    for (int vt : mrd[i + 4]) {
                        if (i % 4 != 0) {
                            mapObjects.add(new MapObject(vt, 0 + i * 50, vt + 50, 50, black));
                        }
                    }
                }
                //cau thang
                mapObjects.add(new MapObject(50, 0, 50, cc - 150, dak));
                mapObjects.add(new MapObject(60, 0, 10, cc - 150, go));
                mapObjects.add(new MapObject(80, 0, 10, cc - 150, go));
                mapObjects.add(new MapObject(60, 0, 10, cc - 150, black));
                mapObjects.add(new MapObject(80, 0, 10, cc - 150, black));
                for (int i = 0; i < 100; i += 2) {
                    mapObjects.add(new MapObject(55, (i * 10), 40, 5, go));
                }
                // dk xuong
                if (x + 20 > 50 && x + 20 < 100 && y + 50 <= cc - 150) {

                    dct(true);
                    if (y + 50 < 0) {
//                        dct(false);
                        x = cr - 500 - 100;
                        y = cc - 50;
                        mm = -1;
                    }

                    textObjects.add(new TextObject(x - 20, y - 20, 20, Color.WHITE, "Xuống [S]"));
                } else {
                    ghmp = 200;
                    dct(false);
                }
                mapObjects.add(new MapObject(cr - 500 - 50, y + 50, -50, 1, Color.red));

                //nha tho ren
                for (int i = 0; i < 11; i++) {
                    mapObjects.add(new MapObject(cr + 100 - i * 10, cc - 350 + i * 10, -400, -10, Color.DARK_GRAY));
                }
                mapObjects.add(new MapObject(cr - 100, cc - 300, -50, -120, nda));
                mapObjects.add(new MapObject(cr - 90, cc - 300, -70, -20, dak));
                mapObjects.add(new MapObject(cr, cc - 150, -300, -100, nda));
                mapObjects.add(new MapObject(cr, cc - 150, -25, -100, gon));
                mapObjects.add(new MapObject(cr - 350, cc - 150, -25, -100, gon));
                mapObjects.add(new MapObject(cr - 200 - 5, cc - 205, -50 + 10, -5, lava));
                mapObjects.add(new MapObject(cr - 200, cc - 180, -50, -60, black));
                mapObjects.add(new MapObject(cr - 140 - 5, cc - 205, -50 + 10, -5, lava));
                mapObjects.add(new MapObject(cr - 140, cc - 180, -50, -60, black));
                mapObjects.add(new MapObject(cr, cc - 150, -400, -20, dak));
                mapObjects.add(new MapObject(0, cc - 150, cr, 100, da));
                for (int vt : mrd[3]) {
                    mapObjects.add(new MapObject(vt, cc - 100, vt + 50, 50, black));
                }
                //khoang
                //lặp sinh khoán
                if (xnm) {
                    int xx = 0;
                    int ii = -1;
                    for (int i = 0; i < clmine.length; i++) {
                        ii++;
                        if (cc - 200 - ii * 25 <= 0) {
                            xx -= 25;
                            ii = -1;
                        }

                        mapObjects.add(new MapObject(xxm - 100 + 8 + xx, cc - 200 - ii * 25, 25, 25, dak));
                        mapObjects.add(new MapObject(xxm - 100 + 8 + 4 + xx, cc - 200 - ii * 25 + 5, 8, 4, clmine[i]));
                        mapObjects.add(new MapObject(xxm - 100 + 8 + 14 + xx, cc - 200 - ii * 25 + 15, 8, 4, clmine[i]));
                        if (xxm - 100 <= x && xxm - 100 + 40 >= x && y >= cc - 400) {
                            ktm(clmine[i]);
                            xnm = false;
                        }
                    }
                }

                //xe mo
                mapObjects.add(new MapObject(xxm - 100,  cc-175, 40, 20, go));


                for (int i = 0; i < 2; i++) {
                    mapObjects.add(new MapObject(xxm - 100 + 5 + i * 20, cc - 160, 10, 10, Color.BLACK));
                }

                mapObjects.add(new MapObject(0, cc - 50, cr, 100, lava));
                break;
            case 101:
                //phong tho ren

                ghmp = 100;
                mapObjects.add(new MapObject(0, 0, cr, cc, nda));
                mapObjects.add(new MapObject(50, 500, cr - 700, 500, black));
                mapObjects.add(new MapObject(0, cc - 50, 50, -150, black));
                mapObjects.add(new MapObject(50, 50, cr - 100, 400, black));
                mapObjects.add(new MapObject(cr - 200, 500, 150, 500, black));

                //tho ren
                int xd = 100;
                int yd = 400;
                mapObjects.add(new MapObject(xd + 13, yd, 15, 50, Color.WHITE));
                mapObjects.add(new MapObject(xd + 6, yd + 10, 29, 20, Color.WHITE));
                mapObjects.add(new MapObject(xd + 16, yd + 5, 9, 5, daNg));
//        mapObjects.add(new MapObject(xd + 20, yd - 50, 1, 150, Color.red));

                textObjects.add(new TextObject(xd - 40, yd - 20, 20, Color.WHITE, "Dân làng thợ mỏ"));
                //map101
                if (x > xd - 100 && x < xd + 100) {
                    switch (dltm) {
                        case 0:
                            textObjects.add(new TextObject(xd - 80, yd - 50, 20, Color.YELLOW, "Tôi cần 5 gỗ để chế tạo xe mỏ gỗ cơ bản."));
                            checkItem("gỗ", 5);
                            break;
                        case 1:
                            textObjects.add(new TextObject(xd - 80, yd - 50, 20, Color.YELLOW, "Xe mỏ gỗ cơ bản đã được đưa vào khai thác."));
                            textObjects.add(new TextObject(x - 20, y - 20, 20, Color.WHITE, "Xát nhận[F]"));
                            xnt = true;
                            xmhd = true;
                            break;
                    }
                } else {
                    xnt = false;
                    dkitem = false;
                }
                if (tb) {
                    TB(Mit, slitem);
                }

                if (dd) {
                    dltm++;
                    dd = false;
                }
                break;
            // Thêm các trường hợp khác nếu cần thiết
            }
        //tra ve buoi
        newbs = false;
        newbd = false;
        
        if(st==false){

        if (canJump == true) {
            if (vx == 0) {
                mapObjects.add(new MapObject(x + 13, y, 15, 50, quan));
                mapObjects.add(new MapObject(x + 5, y + 10, 30, 20, ao));
                mapObjects.add(new MapObject(x + 13, y, 15, 10, daNg));
            } else if (vx != 0) {
                //mặc định 
                mapObjects.add(new MapObject(x + 13, y, 15, 10, daNg));     // đầu người
                mapObjects.add(new MapObject(x + 17, y + 10, 7, 20, ao));  //áo xanh
                anmt++;
                if (anmt == 5) {
                    anmt = 0;
                }

                if (anmt <= 2) {
                    mapObjects.add(new MapObject(x + 17, y + 30, 7, 20, quan));  // quần xanh dương

                } else if (anmt <= 4) {
// CHÉO CHÂN 
//             quần
//             chân sau
                    for (int i = 0; i < 12; i++) {
                        mapObjects.add(new MapObject(x + 15 - i, y + 30 + i, 5, 4, quan));
                    }
//             chân trước
                    for (int i = 0; i < 12; i++) {
                        mapObjects.add(new MapObject(x + 21 + i, y + 30 + i, 5, 4, quan));
                    }
//             cánh tay sau 
                    for (int i = 0; i < 12; i++) {
                        mapObjects.add(new MapObject(x + 15 - i, y + 13 + i, 5, 3, ao));
                    }
//             cánh tay trước
                    for (int i = 0; i < 12; i++) {
                        mapObjects.add(new MapObject(x + 21 + i, y + 13 + i, 5, 3, ao));
                    }
                }
            }
        } else if (canJump == false) {

            mapObjects.add(new MapObject(x + 17, y + 10, 7, 20, ao));  //áo xanh
            // 180 CHÂN    
            // Quần 
            mapObjects.add(new MapObject(x, y + 30, 40, 6, quan));
// cánh tay sau 
            for (int i = 0; i < 12; i++) {
                mapObjects.add(new MapObject(x + 15 - i, y + 13 - i, 5, 3, ao));
            }

//cánh tay trước
            for (int i = 0; i < 12; i++) {
                mapObjects.add(new MapObject(x + 21 + i, y + 13 - i, 5, 3, ao));
            }
            //mặc định 
            mapObjects.add(new MapObject(x + 13, y, 15, 10, daNg));     // đầu người

        }
        }
        
        if(st){
            mapObjects.add(new MapObject(x + 13, y, 15, 50, Color.red));
                mapObjects.add(new MapObject(x + 5, y + 10, 30, 20, Color.red));
                mapObjects.add(new MapObject(x + 13, y, 15, 10, Color.red));
        }

        mapObjects.add(new MapObject(x + 20, y - 50, 1, 150, Color.red));
        if (vtx != 20) {
            vtx = 20;
        }

//        ttm(10);

    }
}
